package br.com.richardcsantana.starwarsjavaapi.application.errors;

public class ResourceNotFoundException extends Throwable {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
