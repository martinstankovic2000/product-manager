package com.ingemark.productmanager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configuration class for locale resolution.
 * Defines a LocaleResolver bean that resolves locale from the HTTP Accept-Language header,
 * defaulting to English.
 */
@Configuration
public class LocaleConfig {

    /**
     * Configures a locale resolver that uses the Accept-Language HTTP header.
     *
     * @return the configured LocaleResolver bean
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}
