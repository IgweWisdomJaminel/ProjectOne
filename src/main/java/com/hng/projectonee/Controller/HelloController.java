package com.hng.projectonee.Controller;

import com.hng.projectonee.Service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class HelloController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/api/hello")
    public String sayHello(@RequestParam(name = "visitor_name") String visitorName, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        try {

            String locationResponse = weatherService.getLocationByIp(clientIp);
            JSONObject locationJson = new JSONObject(locationResponse);
            String city = locationJson.optString("city", "City not found");

            if (city.equals("City not found")) {
                throw new RuntimeException("City not found in the location response.");
            }


            System.out.println("City from IP geolocation: " + city);


            String weatherResponse = weatherService.getWeatherByLocation(city);
            JSONObject weatherJson = new JSONObject(weatherResponse);
            double temperature = weatherJson.getJSONObject("current").getDouble("temp_c");

            // Create response JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("client_ip", clientIp);
            responseJson.put("location", city);
            responseJson.put("greeting", String.format("Hello, %s! The temperature is %.1f degrees Celsius in %s", visitorName, temperature, city));

            return responseJson.toString();
        } catch (HttpClientErrorException e) {

            System.err.println("Error getting weather: " + e.getResponseBodyAsString());


            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Unable to fetch location and weather information");
            errorResponse.put("details", e.getResponseBodyAsString());

            return errorResponse.toString();
        } catch (Exception e) {

            System.err.println("Error occurred in /api/hello endpoint: " + e.getMessage());

            // Return a generic error response
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Unable to fetch location and weather information");
            errorResponse.put("details", e.getMessage());

            return errorResponse.toString();
        }
    }
   //God Abeg😭
    private String getClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header == null || header.isEmpty()) {
            return request.getRemoteAddr();
        }
        return header.split(",")[0].trim();
    }
}
