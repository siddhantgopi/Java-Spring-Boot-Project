package com.realestate.repository;

import com.realestate.model.Transaction;
import com.realestate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.type = 'SELL'")
    List<Transaction> findAllSellTransactions(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.property.id = :propertyId AND t.user.id = :userId AND t.type = 'BUY' ORDER BY t.date DESC LIMIT 1")
    Optional<Transaction> findLatestBuyPrice(Long propertyId, Long userId);
}
