package com.realestate.controller;

import com.realestate.model.Transaction;
import com.realestate.dto.ProfitLossDTO;
import com.realestate.model.User;
import com.realestate.repository.UserRepository;
import com.realestate.service.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;  // 🔹 Add UserRepository

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    // 🔹 Get transactions using email (like View Properties)
    @GetMapping("/user")
    public ResponseEntity<List<Transaction>> getUserTransactions(@RequestParam String email) {
        List<Transaction> transactions = transactionService.getUserTransactionsByEmail(email);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user-transactions")
    public ResponseEntity<?> getUserTransactions(HttpServletRequest request) {
        HttpSession session = request.getSession(false); 
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        String email = (String) session.getAttribute("email");
        List<Transaction> transactions = transactionService.getUserTransactionsByEmail(email);
        return ResponseEntity.ok(transactions);
    }

    // 🔹 New API: Fetch Profit/Loss for Sold Properties
    @GetMapping("/profit-loss")
    public ResponseEntity<List<ProfitLossDTO>> getProfitLoss(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ✅ Fix: Return correct type
        }

        String email = (String) session.getAttribute("email");
        return userRepository.findByEmail(email)
                .map(user -> {
                    List<ProfitLossDTO> profitLossList = transactionService.getProfitLossList(user.getId());
                    return ResponseEntity.ok(profitLossList);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of())); // ✅ Fix: Return empty list instead of string
    }
}
