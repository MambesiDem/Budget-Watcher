package com.example.budgetwatcher;

import java.io.Serializable;
import java.util.List;

public class Aggregation implements Serializable {
    private String dateRange;
    private List<String> tags;
    private double total;
    List<Transaction> transactions;

    public Aggregation(String dateRange, List<String> tags, double total,List<Transaction> transactions) {
        this.dateRange = dateRange;
        this.tags = tags;
        this.total = total;
        this.transactions = transactions;
    }

    public String getDateRange() {
        return dateRange;
    }

    public List<String> getTags() {
        return tags;
    }

    public double getTotal() {
        return total;
    }
    public  List<Transaction> getTransactions(){return transactions;}
}
