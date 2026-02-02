package com.saksham_kumar;

public class Expense {
    private int id;
    private String description;
    private double amount;
    private String category;
    private String date;
    private String userName;

    public Expense() {}

    public Expense(int id, String description, double amount, String category, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }
}