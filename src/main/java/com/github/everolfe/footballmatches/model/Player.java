package com.github.everolfe.footballmatches.model;


import lombok.Data;

@Data
public class Player {
    private Integer id;
    private String name;
    private Integer age;
    private String country;
    private Team team;

    public Player(Integer id, String name, Integer age, String country) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.country = country;
        this.team = null;
    }

}
