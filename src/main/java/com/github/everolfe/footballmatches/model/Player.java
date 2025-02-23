package com.github.everolfe.footballmatches.model;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
