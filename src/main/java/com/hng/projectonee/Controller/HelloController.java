package com.hng.projectonee.Controller;

import com.hng.projectonee.Service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/api/hello")
    public String sayHello(@RequestParam(name = "visitor_name") String visitorName, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        try {
            // Fetch location by IP
            String locationResponse = weatherService.getLocationByIp(clientIp);

            JSONObject locationJson = new JSONObject(locationResponse);
            String city = locationJson.optString("city", "Unknown");

            // Check if the city was found
            if ("Unknown".equals(city)) {
                throw new Exception("City not found in the location response.");
            }

            // Fetch weather by location
            String weatherResponse = weatherService.getWeatherByLocation(city);
            JSONObject weatherJson = new JSONObject(weatherResponse);
            double temperature = weatherJson.getJSONObject("current").getDouble("temp_c");

            // Create response JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("client_ip", clientIp);
            responseJson.put("location", city);
            responseJson.put("greeting", String.format("Hello, %s! The temperature is %.1f degrees Celsius in %s", visitorName, temperature, city));

            return responseJson.toString();
        } catch (Exception e) {
            // Log the error
            System.err.println("Error occurred in /api/hello endpoint: " + e.getMessage());

            // Return an error response
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Unable to fetch location and weather information");
            errorResponse.put("details", e.getMessage());

            return errorResponse.toString();
        }
    }
}
