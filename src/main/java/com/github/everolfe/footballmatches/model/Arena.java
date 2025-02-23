package com.github.everolfe.footballmatches.model;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private Integer id;
    private String city;
    private Integer capacity;
    private  List<Match> matchList;

    public Arena(Integer id, String city, Integer capacity) {
        this.id = id;
        this.city = city;
        this.capacity = capacity;
        this.matchList = new ArrayList<Match>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<Match> getMatchList() {
        return matchList;
    }
}
