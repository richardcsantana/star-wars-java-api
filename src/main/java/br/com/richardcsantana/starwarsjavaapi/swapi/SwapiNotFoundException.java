package br.com.richardcsantana.starwarsjavaapi.swapi;

public class SwapiNotFoundException extends Throwable {
    public SwapiNotFoundException(String message) {
        super(message);
    }
}
