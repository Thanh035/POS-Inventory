package com.example.myapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    // jhipster-needle-application-properties-property
    // jhipster-needle-application-properties-property-getter
    // jhipster-needle-application-properties-property-class
	
	private String name;

    // Getter và setter cho 'name'

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
