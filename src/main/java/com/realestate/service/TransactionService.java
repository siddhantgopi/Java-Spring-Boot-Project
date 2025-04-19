package com.realestate.service;

import com.realestate.model.Transaction;
import com.realestate.model.Property;
import com.realestate.model.User;
import com.realestate.dto.ProfitLossDTO;
import com.realestate.repository.TransactionRepository;
import com.realestate.repository.UserRepository;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction recordTransaction(Long userId, Property property, double price, String type) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Transaction transaction = new Transaction(new Date(), price, type, property, userOptional.get());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactionsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(transactionRepository::findByUser)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Fetches the latest buy transaction for a property and user before it was sold.
     */
    public Optional<Transaction> findLatestBuyTransaction(Long propertyId, Long userId) {
        return transactionRepository.findLatestBuyPrice(propertyId, userId);
    }

    /**
     * Computes the profit and loss for all sold properties of a user.
     */
    public List<ProfitLossDTO> getProfitLossList(Long userId) {
        List<Transaction> sellTransactions = transactionRepository.findAllSellTransactions(userId);
        List<ProfitLossDTO> profitLossList = new ArrayList<>();

        for (Transaction sellTransaction : sellTransactions) {
            Long propertyId = sellTransaction.getProperty().getId();
            double sellPrice = sellTransaction.getPrice();
            String sellDate = sellTransaction.getDate().toString();

            // Fetch the latest buy price before this sale
            Optional<Transaction> buyTransactionOpt = transactionRepository.findLatestBuyPrice(propertyId, userId);
            buyTransactionOpt.ifPresent(buyTransaction -> {
                profitLossList.add(new ProfitLossDTO(buyTransaction, sellTransaction));
            });
        }

        return profitLossList;
    }
}
