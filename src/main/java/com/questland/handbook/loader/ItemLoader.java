package com.questland.handbook.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.questland.handbook.config.QuestlandApiConfigs;
import com.questland.handbook.config.QuestlandServer;
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
    private final String latestTokenUrl = "client/checkstaticdata/?lang=en&graphics_quality=hd_android";
    private final String itemUrl = "staticdata/fb/key/en/android/%s/fb_item_templates/";
    private final String setEmblemUrl = "staticdata/key/en/android/%s/wearable_sets/";
    private final String weaponPassivesUrl = "staticdata/key/en/android/%s/static_passive_skills/";
    private final QuestlandApiConfigs questlandApiConfigs;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadItems();
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    private void loadItems() throws JsonProcessingException {
        String qlServer = questlandApiConfigs.regionWorkerMap().get(QuestlandServer.GLOBAL);
        String latestTokenResponse = restTemplate.getForObject(qlServer + latestTokenUrl, String.class);

        String latestToken = new ObjectMapper().readTree(latestTokenResponse)
                .path("data")
                .path("static_data")
                .path("crc_details")
                .path("fb_item_templates").asText();
        log.info("Latest item token is: " + latestToken);

        byte[] itemBytes = restTemplate.getForObject(String.format(qlServer + itemUrl, latestToken), byte[].class);
        ItemTemplates itemTemplates = ItemTemplates.getRootAsItemTemplates(ByteBuffer.wrap(itemBytes));
        List<ItemTemplate> privateItems = new ArrayList<>();
        for (int i = 0; i < itemTemplates.templatesLength(); i++) {
            privateItems.add(itemTemplates.templates(i));
        }

        Map<Long, Emblem> emblemMap = getEmblemMap(qlServer);

        Map<Integer, PrivateWeaponPassive> weaponPassives = getWeaponPassives(qlServer);
        log.info("Loaded " + weaponPassives.size() + " weapon passives");

        //loading graphics id mapper
        Map<Integer, Integer> rawToFinalGraphicIdMap = LoaderUtility.getRawToFinalGraphicIdMap(qlServer);

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


    private Map<Long, Emblem> getEmblemMap(String qlServer) {
        String latestTokenResponse = restTemplate.getForObject(qlServer + latestTokenUrl, String.class);
        Map<Long, Emblem> emblemMap = new HashMap<>();
        try {
            String emblemToken = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("crc_details")
                    .path("wearable_sets").asText();
            log.info("Latest set/emblem token is: " + emblemToken);

            String emblemUrl = String.format(qlServer + setEmblemUrl, emblemToken);
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

    private Map<Integer, PrivateWeaponPassive> getWeaponPassives(String qlServer) {
        try {
            String latestTokenResponse = restTemplate.getForObject(qlServer + latestTokenUrl, String.class);

            String passiveToken = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("crc_details")
                    .path("static_passive_skills").asText();
            log.info("Latest weapon passives token is: " + passiveToken);

            String passiveWeaponsUrl = String.format(qlServer + weaponPassivesUrl, passiveToken);
            String passiveItemsRaw =
                    restTemplate.getForObject(passiveWeaponsUrl, String.class);

            TypeReference<HashMap<Integer, PrivateWeaponPassive>> typeRef = new TypeReference<>() {
            };
            return new ObjectMapper().readValue(passiveItemsRaw, typeRef);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
