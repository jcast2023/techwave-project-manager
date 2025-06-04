package com.cibertec.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para recursos no encontrados.
 * Se mapea automáticamente a un estado HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Esto le dice a Spring que devuelva un 404
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // Mensaje de error personalizado para la excepción
        super(String.format("%s no encontrado con %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    // Puedes añadir getters si necesitas acceder a estos campos desde un manejador de excepciones
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
