package com.hng.projectonee.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    @Value("${weatherapi.api.key}")
    private String weatherApiKey;

    @Value("${weatherapi.api.url}")
    private String weatherApiUrl;

    @Value("${ipinfo.api.key}")
    private String ipinfoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getLocationByIp(String ip) {
        String url = UriComponentsBuilder.fromHttpUrl("https://ipinfo.io")
                .pathSegment(ip)
                .queryParam("token", ipinfoApiKey)
                .toUriString();
        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("IP Geolocation response: " + response);
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.toString();
        } catch (HttpClientErrorException e) {
            System.err.println("Error getting location by IP: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching location by IP.", e);
        }
    }

    public String getWeatherByLocation(String location) {
        String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                .pathSegment("current.json")
                .queryParam("key", weatherApiKey)
                .queryParam("q", location)
                .toUriString();
        try {
            String response = restTemplate.getForObject(url, String.class);
            return response;
        } catch (HttpClientErrorException e) {
            System.err.println("Error getting weather by location: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching weather by location.", e);
        }
    }
}
