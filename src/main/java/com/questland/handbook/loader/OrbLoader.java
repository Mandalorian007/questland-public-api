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
import com.questland.handbook.repository.OrbRepository;
import com.questland.handbook.publicmodel.Orb;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrbLoader implements ApplicationRunner {

    private final PrivateItemAndOrbConverter privateConverter;
    private final OrbRepository orbRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String latestTokenUrl =
            "http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android";
    private final String orbUrl =
            "http://gs-bhs-wrk-01.api-ql.com/staticdata/fb/key/en/android/%s/fb_item_templates/";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadOrbs();
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    private void loadOrbs() throws JsonProcessingException {
        String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

        String latestToken = new ObjectMapper().readTree(latestTokenResponse)
                .path("data")
                .path("static_data")
                .path("crc_details")
                .path("fb_item_templates").asText();
        log.info("Latest orb token is: " + latestToken);

        byte[] itemBytes = restTemplate.getForObject(String.format(orbUrl, latestToken), byte[].class);
        ItemTemplates itemTemplates = ItemTemplates.getRootAsItemTemplates(ByteBuffer.wrap(itemBytes));
        List<ItemTemplate> privateOrbs = new ArrayList<>();
        for (int i = 0; i < itemTemplates.templatesLength(); i++) {
            privateOrbs.add(itemTemplates.templates(i));
        }
        //load graphics id map
        Map<Integer, Integer> rawToFinalGraphicIdMap = getRawToFinalGraphicIdMap();

        Set<Byte> validItemTypes = Set.of(
                ItemQuality.Uncommon,
                ItemQuality.Rare,
                ItemQuality.Epic,
                ItemQuality.Legendary,
                ItemQuality.LegendaryPlus
        );

        List<Orb> orbs = privateOrbs.stream()
                // Filter out any item that wouldn't be considered an orb
                .filter(orb -> orb.s() == EquipSlot.Orb)
                .filter(orb -> validItemTypes.contains(orb.q()))
                // Convert to our internal orb model
                .map(privateOrb -> privateConverter.convertOrbFromPrivate(privateOrb, rawToFinalGraphicIdMap))
                .collect(Collectors.toList());

        log.info("dropping existing orb table");
        orbRepository.deleteAllInBatch();

        log.info("Loading " + orbs.size() + " orbs into database...");
        orbRepository.saveAll(orbs);
        log.info("Database load of " + orbRepository.count() + " orbs complete.");
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
