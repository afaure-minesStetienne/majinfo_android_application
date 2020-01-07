package com.info.majeur.majeurapp.models;


public class Room {


    private Long id;

    private Light light;


    private String name;

    public Room(Light light, String name) {
        this.light = light;
        this.name = name;
    }

    public Room (){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}