package com.realestate.controller;

import com.realestate.model.MarketListing;
import com.realestate.model.Property;
import com.realestate.repository.MarketListingRepository;
import com.realestate.service.PropertyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/market")
public class MarketListingController {

    private final PropertyService propertyService;
    private final MarketListingRepository marketListingRepository;

    public MarketListingController(PropertyService propertyService, MarketListingRepository marketListingRepository) {
        this.marketListingRepository = marketListingRepository; 
        this.propertyService = propertyService;
    }


    // Sell Property (Move it to Market Listings)
    @PostMapping("/sell/{propertyId}")
public ResponseEntity<String> sellProperty(@PathVariable Long propertyId, @RequestBody Map<String, Double> request, HttpServletRequest httpRequest) {
    HttpSession session = httpRequest.getSession(false);
    if (session == null || session.getAttribute("email") == null) {
        return ResponseEntity.status(401).body("User not logged in");
    }

    String email = (String) session.getAttribute("email");
    double sellingPrice = request.get("sellingPrice");

    if (sellingPrice <= 0) {
        return ResponseEntity.badRequest().body("Invalid selling price");
    }

    // 🔹 Fetch property to check price constraints
    Property property = propertyService.getPropertyById(propertyId);
    double maxAllowedPrice = 1.3 * (property.getPricePerSqFt() * property.getDimensions());

    if (sellingPrice > maxAllowedPrice) {
        return ResponseEntity.badRequest().body("Invalid selling price: Cannot exceed 130% of original value");
    }

    propertyService.sellProperty(propertyId, email, sellingPrice);
    return ResponseEntity.ok("Property listed for sale at $" + sellingPrice);
}


    // Buy Property (Transfer ownership)
    @PostMapping("/buy/{listingId}")
    public ResponseEntity<String> buyProperty(@PathVariable Long listingId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        String email = (String) session.getAttribute("email");
        propertyService.buyProperty(listingId, email);
        return ResponseEntity.ok("Property purchased successfully!");
    }

    // Get all properties for sale
    @GetMapping("/listings")
    public ResponseEntity<List<MarketListing>> getMarketListings() {
        List<MarketListing> availableListings = marketListingRepository.findAll();
        return ResponseEntity.ok(availableListings);
    }

}
