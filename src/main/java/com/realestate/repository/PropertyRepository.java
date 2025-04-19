package com.realestate.repository;
import com.realestate.model.User;
import com.realestate.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByUser(User user);
}
