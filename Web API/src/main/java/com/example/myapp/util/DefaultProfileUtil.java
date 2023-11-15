package com.example.myapp.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;

public class DefaultProfileUtil {

	private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";

    private DefaultProfileUtil() {
    }

    public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<String, Object>();
        defProperties.put(SPRING_PROFILE_DEFAULT, "dev");
        app.setDefaultProperties(defProperties);
    }
    
}
