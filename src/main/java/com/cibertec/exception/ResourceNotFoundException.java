package com.cibertec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Esto hace que Spring devuelva un 404 NOT FOUND
public class ResourceNotFoundException extends RuntimeException {

    // Constructor sin argumentos (opcional, pero buena práctica)
    public ResourceNotFoundException() {
        super();
    }

    // ¡ESTE ES EL CONSTRUCTOR QUE NECESITAS AGREGAR O VERIFICAR!
    public ResourceNotFoundException(String message) {
        super(message); // Llama al constructor de la clase padre (RuntimeException)
    }

    // Si también manejas causas (otra excepción):
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}