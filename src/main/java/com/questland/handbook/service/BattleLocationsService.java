package com.questland.handbook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questland.handbook.loader.PrivateBattleLocationDetailsConverter;
import com.questland.handbook.loader.model.PrivateBattleLocation;
import com.questland.handbook.loader.model.PrivateMob;
import com.questland.handbook.model.BattleLocation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class BattleLocationsService {

    private final PrivateBattleLocationDetailsConverter privateBattleLocationDetailsConverter;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String latestTokenUrl =
            "http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android";
    private final String battleLocationsUrl =
            "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/static_battle_locations_new/";
    private final String mobLocationsUrl =
            "http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/%s/static_mobs_on_stages/";

    public List<BattleLocation> getBattleLocationDetails() {
        try {
            String latestTokenResponse = restTemplate.getForObject(latestTokenUrl, String.class);

            JsonNode crcDetails = new ObjectMapper().readTree(latestTokenResponse)
                    .path("data")
                    .path("static_data")
                    .path("crc_details");

            String battleLocationsToken = crcDetails.path("static_battle_locations_new").asText();
            log.info("Latest battle locations token is: " + battleLocationsToken);

            String mobLocationsToken = crcDetails.path("static_mobs_on_stages").asText();
            log.info("Latest mob locations token is: " + mobLocationsToken);

            List<PrivateBattleLocation> privateBattleLocations = Arrays.asList(
                    restTemplate.getForObject(String.format(battleLocationsUrl, battleLocationsToken), PrivateBattleLocation[].class));

            ParameterizedTypeReference<Map<Integer, PrivateMob>> responseType =
                    new ParameterizedTypeReference<>() {
                    };
            RequestEntity<Void> request = RequestEntity
                    .get(new URI(String.format(mobLocationsUrl, mobLocationsToken)))
                    .accept(MediaType.APPLICATION_JSON).build();

            Map<Integer, PrivateMob> privateMobs = restTemplate.exchange(request, responseType).getBody();

            return privateBattleLocationDetailsConverter.convertFromPrivate(privateBattleLocations, privateMobs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
