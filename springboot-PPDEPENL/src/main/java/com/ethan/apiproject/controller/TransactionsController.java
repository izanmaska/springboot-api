package com.ethan.apiproject.controller;

import com.ethan.apiproject.model.Transactions;
import com.ethan.apiproject.model.Users;
import com.ethan.apiproject.service.TransactionsService;
import com.ethan.apiproject.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;
    @Autowired
    private TransactionsController(TransactionsService transactionsService){
        this.transactionsService = transactionsService;
    }


    @GetMapping("/transactions")
    private ResponseEntity<Page<Transactions>> listAllTransactions(Pageable pageable) {
        Page<Transactions> transactionsPage = transactionsService.transactionsFindAll(pageable);
        return ResponseEntity.ok(transactionsPage);
    }
    @GetMapping (value = "/{id}")
    private ResponseEntity<Optional<Transactions>> findTransactionById (@PathVariable ("id") Long id){
        return ResponseEntity.ok(transactionsService.transactionsFindById(id));
    }
    @PostMapping
    private ResponseEntity<Transactions> saveTransaction (@RequestBody Transactions transactions){
        Transactions temp = transactionsService.createTransaction(transactions);
        try {
            return ResponseEntity.created(new URI("api/transactions/"+temp.getId())).body(temp);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateTransaction(@RequestBody Transactions transactions, @PathVariable Long id){
        if(!transactionsService.transactionsExistsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Content not found!");
        }
        transactionsService.createTransaction(transactions);
    }
    @DeleteMapping
    private ResponseEntity<Void> deleteTransaction (@RequestBody Transactions transactions){
        transactionsService.deleteTransaction(transactions);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{userId}/transactions")
    public ResponseEntity<Page<Transactions>> listTransactionsByUser(@PathVariable Long userId,
                                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<Transactions> transactionsPage = transactionsService.listTransactionsByUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(transactionsPage);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}/transactions")
    public void deleteTransactionsByUser(@PathVariable Long userId) {
        transactionsService.deleteTransactionsByUser(userId);
    }


}
