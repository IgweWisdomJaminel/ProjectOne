package com.hng.projectonee.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocationService {

    @Value("${ipstack.access_key}")
    private String ipstackAccessKey;

    @Value("${openweathermap.api_key}")
    private String openWeatherMapApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> getLocationAndTemperature(String ip) {
        Map<String, String> result = new HashMap<>();

        // Now I get location from IP Stack
        String ipstackUrl = "http://api.ipstack.com/" + ip + "?access_key=" + ipstackAccessKey;
        Map<?, ?> ipstackResponse = restTemplate.getForObject(ipstackUrl, Map.class);
        String city = (String) ipstackResponse.get("city");

        //  temperature from OpenWeatherMap
        String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + openWeatherMapApiKey;
        Map<?, ?> weatherResponse = restTemplate.getForObject(weatherUrl, Map.class);
        Map<?, ?> main = (Map<?, ?>) weatherResponse.get("main");
        String temperature = main.get("temp").toString();

        result.put("location", city);
        result.put("temperature", temperature + " degrees Celsius");
        return result;
    }
}
