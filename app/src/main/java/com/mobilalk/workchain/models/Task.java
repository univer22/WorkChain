package com.mobilalk.workchain.models;

public class Task {
    private String id;
    private String name;
    private String dueDate;
    private String description;
    private String priority;
    private String projectId;

    public Task(String name, String dueDate, String description, String priority, String projectId) {
        this.name = name;
        this.dueDate = dueDate;
        this.description = description;
        this.priority = priority;
        this.projectId = projectId;
    }
    public Task() {}

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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
