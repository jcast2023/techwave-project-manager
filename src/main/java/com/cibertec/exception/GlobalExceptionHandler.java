package com.cibertec.exception;

import com.cibertec.dto.ErrorDetails; // Importar el DTO para los detalles del error
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Importar para manejar AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * GlobalExceptionHandler es una clase de Spring que actúa como un manejador centralizado de excepciones.
 * Utiliza @ControllerAdvice para aplicar el manejo de excepciones a todos los controladores.
 * Extiende ResponseEntityExceptionHandler para personalizar el manejo de excepciones estándar de Spring MVC.
 */
@ControllerAdvice // Indica que esta clase es un manejador global de excepciones
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Maneja excepciones ResourceNotFoundException.
     * Esta excepción se lanza cuando un recurso solicitado no se encuentra en la base de datos.
     * @param exception La excepción ResourceNotFoundException que se ha lanzado.
     * @param webRequest La solicitud web actual.
     * @return ResponseEntity con los detalles del error y el estado HTTP 404 (NOT_FOUND).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest){
        // Crear un objeto ErrorDetails con la marca de tiempo, mensaje de la excepción y descripción de la solicitud.
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), webRequest.getDescription(false));
        // Devolver una ResponseEntity con el objeto ErrorDetails y el estado HTTP NOT_FOUND.
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones AccessDeniedException.
     * Esta excepción se lanza cuando un usuario autenticado intenta acceder a un recurso sin los permisos necesarios.
     * Se produce cuando un @PreAuthorize o una configuración de seguridad deniega el acceso.
     * @param exception La excepción AccessDeniedException que se ha lanzado.
     * @param webRequest La solicitud web actual.
     * @return ResponseEntity con los detalles del error y el estado HTTP 403 (FORBIDDEN).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException exception,
            WebRequest webRequest){
        // Crear un objeto ErrorDetails con la marca de tiempo, mensaje de la excepción y descripción de la solicitud.
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), webRequest.getDescription(false));
        // Devolver una ResponseEntity con el objeto ErrorDetails y el estado HTTP FORBIDDEN.
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }


    /**
     * Maneja excepciones genéricas (Exception.class).
     * Esta es una excepción de "captura todo" que manejará cualquier excepción
     * que no haya sido manejada por un manejador más específico.
     * @param exception La excepción genérica que se ha lanzado.
     * @param webRequest La solicitud web actual.
     * @return ResponseEntity con los detalles del error y el estado HTTP 500 (INTERNAL_SERVER_ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception exception,
            WebRequest webRequest){
        // Crear un objeto ErrorDetails con la marca de tiempo, mensaje de la excepción y descripción de la solicitud.
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), webRequest.getDescription(false));
        // Devolver una ResponseEntity con el objeto ErrorDetails y el estado HTTP INTERNAL_SERVER_ERROR.
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

