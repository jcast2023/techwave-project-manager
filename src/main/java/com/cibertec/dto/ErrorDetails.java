package com.cibertec.dto;

import java.util.Date;

/**
 * ErrorDetails es un DTO (Data Transfer Object) utilizado para estructurar las respuestas de error de la API.
 * Proporciona información estandarizada sobre el error, incluyendo la marca de tiempo,
 * el mensaje de error y los detalles de la solicitud.
 */
public class ErrorDetails {
    private Date timestamp; // Marca de tiempo cuando ocurrió el error
    private String message; // Mensaje descriptivo del error
    private String details; // Detalles adicionales sobre la solicitud o el error

    // Constructor para inicializar los detalles del error
    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    // Getters para acceder a las propiedades (no se necesitan setters para un DTO de respuesta de error)
    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
