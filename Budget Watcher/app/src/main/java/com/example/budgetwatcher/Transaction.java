package com.example.budgetwatcher;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable {
    private String account;
    private String type;
    private Date date;
    private String description;
    private double amount;
    private double balance;
    private List<String> tags;

    public Transaction(String account, String type, Date date, String description, double amount,double balance, List<String> tags) {
        this.account = account;
        this.type = type;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.tags = tags;
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }
    public String getAccount() {
        return account;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public List<String> getTags() {
        return tags;
    }
    public void addTag(String tag){
        tags.add(tag);
    }

    @Override
    public String toString() {
        return "Account: " + account + "\n" +
                "Type: " + type + "\n" +
                "Date: " + date +"\n"+
                "Description: " + description + "\n" +
                "Amount: " + amount + "\n"+
                "Balance: " + balance + "\n"+
                "Tags: " + tags;

    }
}
