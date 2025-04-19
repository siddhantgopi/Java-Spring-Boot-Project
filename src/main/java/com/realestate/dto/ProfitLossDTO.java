package com.realestate.dto;

import com.realestate.model.Transaction;

public class ProfitLossDTO {
    private Long propertyId;
    private double buyPrice;
    private double sellPrice;
    private double profitLoss;
    private String sellDate;

    public ProfitLossDTO(Transaction buyTransaction, Transaction sellTransaction) {
        this.propertyId = sellTransaction.getProperty().getId();
        this.buyPrice = buyTransaction.getPrice();
        this.sellPrice = sellTransaction.getPrice();
        this.profitLoss = sellPrice - buyPrice;
        this.sellDate = sellTransaction.getDate().toString();
    }

    // Getters
    public Long getPropertyId() {
        return propertyId;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public String getSellDate() {
        return sellDate;
    }
}
