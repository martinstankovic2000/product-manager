package com.ingemark.productmanager.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Service to retrieve localized messages from message source.
 * Supports argument substitution and default messages.
 */
@Component
public class MessageService {
    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Retrieves a localized message by key.
     *
     * @param key Message key.
     * @param args Optional arguments for message formatting.
     * @return Localized message string.
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a localized message by key with a default fallback.
     *
     * @param key Message key.
     * @param args Arguments for formatting.
     * @param defaultMessage Default message if key is not found.
     * @return Localized message or default.
     */
    public String getMessage(String key, Object[] args, String defaultMessage) {
        return messageSource.getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a localized message by key without arguments.
     *
     * @param key Message key.
     * @return Localized message.
     */
    public String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }
}
