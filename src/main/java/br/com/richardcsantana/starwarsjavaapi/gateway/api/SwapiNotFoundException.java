package br.com.richardcsantana.starwarsjavaapi.gateway.api;

public class SwapiNotFoundException extends Throwable {
    public SwapiNotFoundException(String message) {
        super(message);
    }
}
