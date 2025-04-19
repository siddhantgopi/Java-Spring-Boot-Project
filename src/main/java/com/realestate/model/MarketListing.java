package com.realestate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "market_listings")
public class MarketListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    private String name;
    private String location;
    private double dimensions;
    private double sellingPrice;
    private double pricePerSqFt;
    private String documentPath;
    private String dateOfBuy;

    public MarketListing() {}

    public MarketListing(Property property) {
        this.property = property;
        this.name = property.getName();
        this.location = property.getLocation();
        this.dimensions = property.getDimensions();
        this.pricePerSqFt = property.getPricePerSqFt();
        this.documentPath = property.getDocumentPath();
        this.dateOfBuy = property.getDateOfBuy();
        this.sellingPrice = sellingPrice;
    }

    public Long getId() { return id; }
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getDimensions() { return dimensions; }
    public double getPricePerSqFt() { return pricePerSqFt; }
    public String getDocumentPath() { return documentPath; }
    public String getDateOfBuy() { return dateOfBuy; }
    public double getSellingPrice() { return sellingPrice; } 
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public void setPricePerSqFt(double pricePerSqFt) {
        this.pricePerSqFt = pricePerSqFt;
    }
}
