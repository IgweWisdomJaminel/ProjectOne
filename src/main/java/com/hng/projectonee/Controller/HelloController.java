package com.hng.projectonee.Controller;


import com.hng.projectonee.Service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {


    @Autowired
    private LocationService locationService;

    @GetMapping("/api/hello")
    public Map<String, String> hello(@RequestParam String visitor_name, HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
g

        Map<String, String> locationData = locationService.getLocationAndTemperature(clientIp);
        String location = locationData.get("location");
        String temperature = locationData.get("temperature");

        Map<String, String> response = new HashMap<>();
        response.put("client_ip", clientIp);
        response.put("location", location);
        response.put("greeting", "Hello, " + visitor_name + "!, the temperature is " + temperature + " in " + location);

        return response;
    }
}
