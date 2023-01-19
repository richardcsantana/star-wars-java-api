package br.com.richardcsantana.starwarsjavaapi.batch.swapi;

public class SwapiNotFoundException extends Throwable {
    public SwapiNotFoundException(String message) {
        super(message);
    }
}
