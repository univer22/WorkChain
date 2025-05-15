package com.mobilalk.workchain.models;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private String id;
    private String name;
    private String description;
    private String userID;

    public Project(String name, String description, String userID) {
        this.name = name;
        this.description = description;
        this.userID = userID;
    }

    public Project() {}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
