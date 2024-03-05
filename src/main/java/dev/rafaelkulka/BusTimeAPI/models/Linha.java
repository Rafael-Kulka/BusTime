package dev.rafaelkulka.BusTimeAPI.models;

public class Linha {
    private String path;
    private String name;

    public Linha(String path, String name) {
        this.path = path;
        this.name = name;
    }
    public String getPath(){
        return this.path;
    }

    public String getName(){
        return this.name;
    }
}
