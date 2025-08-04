package com.ingemark.productmanager.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.productmanager.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles AccessDeniedException by returning a custom JSON error response
 * with HTTP status 403 Forbidden and a localized error message.
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    /**
     * Handles access denied exceptions by setting the response status and body
     * with a JSON error message.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @param accessDeniedException the exception thrown when access is denied
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        String message = messageService.getMessage("not.authorized");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                message,
                LocalDateTime.now()
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
