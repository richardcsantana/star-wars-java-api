package br.com.richardcsantana.starwarsjavaapi.common.application.errors;

public class ResourceNotFoundException extends Throwable {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
