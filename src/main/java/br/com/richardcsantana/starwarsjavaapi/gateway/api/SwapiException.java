package br.com.richardcsantana.starwarsjavaapi.gateway.api;

public class SwapiException extends Throwable {
    public SwapiException(String message) {
        super(message);
    }
}
