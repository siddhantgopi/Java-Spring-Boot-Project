package com.realestate.service;

import com.realestate.model.Property;
import com.realestate.model.User;
import com.realestate.model.Transaction;
import com.realestate.model.MarketListing;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import com.realestate.repository.TransactionRepository;
import com.realestate.repository.MarketListingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.nio.file.*;
import java.io.IOException;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MarketListingRepository marketListingRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public PropertyService(PropertyRepository propertyRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository,
                           MarketListingRepository marketListingRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.marketListingRepository = marketListingRepository;
    }

    // ✅ Add Property
    public Property addProperty(String email, String name, String location, double dimensions, double pricePerSqFt,
                                String dateOfBuy, MultipartFile document) throws IOException {
        User user = userRepository.findByEmail(email)
                  .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String filePath = UPLOAD_DIR + document.getOriginalFilename();
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        Files.write(Paths.get(filePath), document.getBytes());

        Property property = new Property(null, name, location, dimensions, pricePerSqFt, filePath, dateOfBuy, user);
        property = propertyRepository.save(property);

        // ✅ Record Transaction
        Transaction transaction = new Transaction();
        transaction.setDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        transaction.setPrice(dimensions * pricePerSqFt);
        transaction.setType("ADD");
        transaction.setProperty(property);
        transaction.setUser(user);
        transactionRepository.save(transaction);

        return property;
    }

    // ✅ Get User's Properties
    public List<Property> getUserProperties(String email) {
        return userRepository.findByEmail(email)
                  .map(propertyRepository::findByUser)
                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // ✅ Sell Property (Move to Market Listings)
    @Transactional
    public void sellProperty(Long propertyId, String email, double sellingPrice) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        if (!property.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't own this property");
        }

        if (sellingPrice <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selling price must be greater than zero");
        }

        // ✅ Update pricePerSqFt before listing
        double newPricePerSqFt = sellingPrice / property.getDimensions();
        property.setPricePerSqFt(newPricePerSqFt);
        property.setUser(null); // Remove ownership
        propertyRepository.save(property);

        // ✅ Add to Market Listings with updated pricePerSqFt
        MarketListing listing = new MarketListing(property);
        listing.setSellingPrice(sellingPrice);
        listing.setPricePerSqFt(newPricePerSqFt); // Update in MarketListing too
        marketListingRepository.save(listing);

        // ✅ Record Transaction
        Transaction transaction = new Transaction();
        transaction.setDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        transaction.setPrice(sellingPrice);
        transaction.setType("SELL");
        transaction.setProperty(property);
        transaction.setUser(userRepository.findByEmail(email).orElseThrow());
        transactionRepository.save(transaction);
    }



    // ✅ Buy Property (Transfer Ownership)
    @Transactional
    public void buyProperty(Long listingId, String email) {
        MarketListing listing = marketListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Transfer ownership
        Property property = listing.getProperty();
        property.setUser(buyer);
        property.setPricePerSqFt(listing.getPricePerSqFt());           
        propertyRepository.save(property);

        // Remove from market listings
        marketListingRepository.delete(listing);

        // ✅ Record Transaction
        Transaction transaction = new Transaction();
        transaction.setDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        transaction.setPrice(listing.getSellingPrice());
        transaction.setType("BUY");
        transaction.setProperty(property);
        transaction.setUser(buyer);
        transactionRepository.save(transaction);
    }

    // ✅ Get all properties listed for sale
    public List<MarketListing> getMarketListings() {
        return marketListingRepository.findAll();
    }

    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }
}
