package com.realestate.repository;
import com.realestate.model.Property;

import com.realestate.model.MarketListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketListingRepository extends JpaRepository<MarketListing, Long> {
    boolean existsByProperty(Property property);
}
