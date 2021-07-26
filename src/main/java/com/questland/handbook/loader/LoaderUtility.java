package com.questland.handbook.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class LoaderUtility {
    private static final String latestTokenUrl = "client/checkstaticdata/?lang=en&graphics_quality=hd_android";

    public static String getGraphicsServerUrl(String qlServer) {
        try {
            String latestTokenResponse = new RestTemplate().getForObject(qlServer + latestTokenUrl, String.class);
            String graphicsServerUrl = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("server_domains").asText();
            log.info("Graphics server url: " + graphicsServerUrl);
            return graphicsServerUrl;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Integer> getRawToFinalGraphicIdMap(String qlServer) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String latestTokenResponse = restTemplate.getForObject(qlServer + latestTokenUrl, String.class);
            String graphicsEndpoint = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("uri_assets").asText();

            String graphicsMapAsString = restTemplate.getForObject(qlServer + graphicsEndpoint, String.class);
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
}
