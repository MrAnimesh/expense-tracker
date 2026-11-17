package com.example.expensetracker;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    private String id;
    private double amount;
    private String category;
    private String merchant;
    private String receiptUrl; // New field holding the image link

    public Expense() {}

    public Expense(double amount, String category, String merchant, String receiptUrl) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.category = category;
        this.merchant = merchant;
        this.receiptUrl = receiptUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
}
