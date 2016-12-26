package com.example.yavengy.hidemyteam;

/**
 * Created by yavengy on 12/17/16.
 */

public class Filter {
    private String team;
    private String devision;

    public Filter(){
    }

    public Filter(String team, String devision) {
        this.team = team;
        this.devision = devision;
    }

    public String getTitle() {
        return team;
    }

    public void setTitle(String team) {
        this.team = team;
    }

    public String getDevision() {
        return devision;
    }

    public void setDevision(String devision) {
        this.devision = devision;
    }
}
