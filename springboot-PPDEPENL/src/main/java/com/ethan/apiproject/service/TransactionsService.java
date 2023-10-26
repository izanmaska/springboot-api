package com.ethan.apiproject.service;

import com.ethan.apiproject.model.Transactions;
import com.ethan.apiproject.model.Type;
import com.ethan.apiproject.model.Users;
import com.ethan.apiproject.repository.TransactionsRepository;
import com.ethan.apiproject.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionsService {
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private UsersRepository usersRepository;

    public Transactions createTransaction(Transactions transactions) {
        Users user1 = usersRepository.findById(transactions.getUser1Id()).orElse(null);
        Users user2 = usersRepository.findById(transactions.getUser2Id()).orElse(null);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("Invalid user IDs in the transaction");
        }

        if (!(user1.getUserType() == Type.B2C || user2.getUserType() == Type.B2C)) {
            throw new IllegalArgumentException("At least one user must be of type 'B2C'");
        }

        return transactionsRepository.save(transactions);
    }
    public Page<Transactions> transactionsFindAll(Pageable pageable) {
        return transactionsRepository.findAll(pageable);
    }    public void deleteTransaction(Transactions transactions){
        transactionsRepository.delete(transactions);
    }

    public Optional<Transactions> transactionsFindById(Long id){
        return transactionsRepository.findById(id);
    }
    public List<Transactions> getAllTransactionsByUserId(Long userId) {
        return transactionsRepository.findByUser1IdOrUser2Id(userId, userId);
    }


    public boolean transactionsExistsById(Long id) {
        return transactionsRepository.existsById(id);
    }

    public Page<Transactions> listTransactionsByUser(Long userId, Pageable pageable) {
        List<Transactions> allTransactions = transactionsRepository.findAllByUser1IdOrUser2Id(userId, userId);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Transactions> paginatedTransactions;

        if (startItem < allTransactions.size()) {
            int endItem = Math.min(startItem + pageSize, allTransactions.size());
            paginatedTransactions = allTransactions.subList(startItem, endItem);
        } else {
            paginatedTransactions = Collections.emptyList();
        }

        return new PageImpl<>(paginatedTransactions, pageable, allTransactions.size());
    }

    public void deleteTransactionsByUser(Long userId) {
        transactionsRepository.deleteAllByUser1IdOrUser2Id(userId, userId);
    }


}
