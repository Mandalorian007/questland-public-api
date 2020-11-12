package com.questland.handbook.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.questland.handbook.flatbuffers.EquipSlot;
import com.questland.handbook.flatbuffers.ItemQuality;
import com.questland.handbook.flatbuffers.ItemTemplate;
import com.questland.handbook.flatbuffers.ItemTemplates;
import com.questland.handbook.loader.model.PrivateWeaponPassive;
import com.questland.handbook.publicmodel.Emblem;
import com.questland.handbook.publicmodel.Item;
import com.questland.handbook.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemLoader implements ApplicationRunner {

    private final PrivateItemAndOrbConverter privateConverter;
    private final ItemRepository itemRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String latestTokenUrl =
            "http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android";
    private final String itemUrl =
            "http://gs-bhs-wrk-01.api-ql.com/staticdata/fb/key/en/android/%s/fb_item_templates/";
    private final String setEmblemUrl =
            "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/wearable_sets/";
    private final String weaponPassivesUrl =
            "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/static_passive_skills/";

    @Override
    @Scheduled(cron = "0 * * * *")
    public void run(ApplicationArguments args) throws Exception {
        String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

        String latestToken = new ObjectMapper().readTree(latestTokenResponse)
                .path("data")
                .path("static_data")
                .path("crc_details")
                .path("fb_item_templates").asText();
        log.info("Latest item token is: " + latestToken);

        byte[] itemBytes = restTemplate.getForObject(String.format(itemUrl, latestToken), byte[].class);
        ItemTemplates itemTemplates = ItemTemplates.getRootAsItemTemplates(ByteBuffer.wrap(itemBytes));
        List<ItemTemplate> privateItems = new ArrayList<>();
        for (int i = 0; i < itemTemplates.templatesLength(); i++) {
            privateItems.add(itemTemplates.templates(i));
        }

        Map<Long, Emblem> emblemMap = getEmblemMap();

        Map<Integer, PrivateWeaponPassive> weaponPassives = getWeaponPassives();
        log.info("Loaded " + weaponPassives.size() + " weapon passives");

        //loading graphics id mapper
        Map<Integer, Integer> rawToFinalGraphicIdMap = getRawToFinalGraphicIdMap();

        Set<Byte> validItemTypes = Set.of(
                ItemQuality.Common,
                ItemQuality.Uncommon,
                ItemQuality.Rare,
                ItemQuality.Epic,
                ItemQuality.Legendary,
                ItemQuality.LegendaryPlus,
                ItemQuality.Artifact,
                ItemQuality.ArtifactPlus,
                ItemQuality.Relic,
                ItemQuality.RelicPlus
        );

        Set<Byte> validItemSlots = Set.of(
                EquipSlot.Head,
                EquipSlot.Chest,
                EquipSlot.Hands,
                EquipSlot.Legs,
                EquipSlot.Amulet,
                EquipSlot.Ring,
                EquipSlot.Talisman,
                EquipSlot.MainHand,
                EquipSlot.OffHand
        );

        List<Item> itemData = privateItems.stream()
                // Filter out any item that wouldn't be considered gear
                .filter(item -> validItemTypes.contains(item.q()))
                .filter(item -> validItemSlots.contains(item.s()))
                // Convert to our internal gear model
                .map(item -> {
                    return privateConverter.convertItemFromPrivate(
                            item,
                            emblemMap,
                            weaponPassives,
                            rawToFinalGraphicIdMap);
                })
                // For some reason I find common versions of items with a potential of 0 so filter them
                .filter(item -> item.getTotalPotential() > 0)
                .collect(Collectors.toList());
        log.info("# of items discovered: " + itemData.size());

        log.info("dropping existing item table");
        itemRepository.deleteAllInBatch();

        log.info("Loading " + itemData.size() + " items into database...");
        itemRepository.saveAll(itemData);
        log.info("Database load of " + itemRepository.count() + " items complete.");
    }


    private Map<Long, Emblem> getEmblemMap() {
        String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);
        Map<Long, Emblem> emblemMap = new HashMap<>();
        try {
            String emblemToken = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("crc_details")
                    .path("wearable_sets").asText();
            log.info("Latest set/emblem token is: " + emblemToken);

            String emblemUrl = String.format(setEmblemUrl, emblemToken);
            log.info(emblemUrl);
            String emblemDataRaw =
                    restTemplate.getForObject(emblemUrl, String.class);

            JsonNode emblemArray = new ObjectMapper().readTree(emblemDataRaw);
            for (JsonNode emblemEntry : emblemArray) {
                Iterable<JsonNode> iterable = emblemEntry::elements;
                List<JsonNode> emblemEntryList =
                        StreamSupport.stream(iterable.spliterator(), false)
                                .collect(Collectors.toList());
                emblemMap.put(
                        emblemEntryList.get(0).asLong(),
                        PrivateItemAndOrbConverter.getEmblemFromPrivate(emblemEntryList.get(1).asText()));
            }
            return emblemMap;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, PrivateWeaponPassive> getWeaponPassives() {
        try {
            String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

            String passiveToken = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("crc_details")
                    .path("static_passive_skills").asText();
            log.info("Latest weapon passives token is: " + passiveToken);

            String passiveWeaponsUrl = String.format(weaponPassivesUrl, passiveToken);
            String passiveItemsRaw =
                    restTemplate.getForObject(passiveWeaponsUrl, String.class);

            TypeReference<HashMap<Integer, PrivateWeaponPassive>> typeRef = new TypeReference<>() {
            };
            return new ObjectMapper().readValue(passiveItemsRaw, typeRef);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, Integer> getRawToFinalGraphicIdMap() {
        try {
            String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);
            String graphicsEndpoint = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("uri_assets").asText();

            String graphicsMapAsString = restTemplate.getForObject(getGraphicsServerUrl() + graphicsEndpoint, String.class);
            TypeReference<HashMap<String, Object[]>> typeRef = new TypeReference<>() {
            };
            Map<String, Object[]> graphicsMap = new ObjectMapper().readValue(graphicsMapAsString, typeRef);

            return graphicsMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> Integer.parseInt(entry.getKey()),
                            entry -> {
                                Object[] value = entry.getValue();
                                if (value == null || value.length <= 1) {
                                    return -1;
                                }
                                return (Integer) value[0];
                            }));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getGraphicsServerUrl() {
        try {
            String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);
            ArrayNode serverDomainsNode = (ArrayNode) new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("server_domains");
            List<JsonNode> serverDomains = StreamSupport
                    .stream(serverDomainsNode.spliterator(), false)
                    .collect(Collectors.toList());
            if (serverDomains.size() < 1) {
                throw new RuntimeException("Couldn't locate the image server");
            }
            String graphicsServerUrl = serverDomains.get(0).asText();
            log.info("Graphics server url: " + graphicsServerUrl);
            return graphicsServerUrl;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
