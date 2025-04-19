package com.realestate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String location;
    private double dimensions;
    private double pricePerSqFt;
    private String documentPath;
    private String dateOfBuy;  // New field for date of purchase

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)  // Foreign key to User table
    private User user;

    // Constructors
    public Property() {}

    public Property(Long id, String name, String location, double dimensions, double pricePerSqFt, 
                    String documentPath, String dateOfBuy, User user) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.dimensions = dimensions;
        this.pricePerSqFt = pricePerSqFt;
        this.documentPath = documentPath;
        this.dateOfBuy = dateOfBuy;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getDimensions() { return dimensions; }
    public void setDimensions(double dimensions) { this.dimensions = dimensions; }

    public double getPricePerSqFt() { return pricePerSqFt; }
    public void setPricePerSqFt(double pricePerSqFt) { this.pricePerSqFt = pricePerSqFt; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public String getDateOfBuy() { return dateOfBuy; }  // Getter for dateOfBuy
    public void setDateOfBuy(String dateOfBuy) { this.dateOfBuy = dateOfBuy; }  // Setter for dateOfBuy

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
