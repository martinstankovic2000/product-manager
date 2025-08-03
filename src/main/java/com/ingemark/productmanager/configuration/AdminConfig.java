package com.ingemark.productmanager.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
@Data
public class AdminConfig {
    private String username;
    private String email;
    private String password;
    private boolean createOnStartup;
}
