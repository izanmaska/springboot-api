package com.ethan.apiproject.IntegrationTests.APIIntegrationTests;
import com.ethan.apiproject.model.Transactions;
import com.ethan.apiproject.model.enums.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class TransactionsAPITests {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;
        restTemplate = new RestTemplate();
    }

    @Test
    public void testCreateTransaction() {
        Transactions newTransaction = new Transactions();
        newTransaction.setUser1Id(UUID.randomUUID().toString());
        newTransaction.setUser2Id(UUID.randomUUID().toString());
        newTransaction.setUser1Type(Type.B2C);
        newTransaction.setUser2Type(Type.B2C);

        ResponseEntity<Transactions> response = restTemplate.postForEntity(
                baseUrl + "/api/transactions",
                newTransaction,
                Transactions.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Transactions createdTransaction = response.getBody();

        assertNotNull(createdTransaction);
        assertNotNull(createdTransaction.getId());
        assertEquals(newTransaction.getUser1Id(), createdTransaction.getUser1Id());
        assertEquals(newTransaction.getUser2Id(), createdTransaction.getUser2Id());
    }

    @Test
    public void testListAllTransactions() {
        int page = 0;
        int size = 10;


        ResponseEntity<Page<Transactions>> response = restTemplate.exchange(
                baseUrl + "/api/transactions?size=" + size + "&page=" + page,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<Transactions> transactionsPage = response.getBody();
        assertNotNull(transactionsPage);
    }

    @Test
    public void testFindTransactionById() {
        Transactions newTransaction = new Transactions();
        newTransaction.setUser1Id(UUID.randomUUID().toString());
        newTransaction.setUser2Id(UUID.randomUUID().toString());
        newTransaction.setUser1Type(Type.B2C);
        newTransaction.setUser2Type(Type.B2C);

        ResponseEntity<Transactions> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/transactions",
                newTransaction,
                Transactions.class
        );

        Transactions createdTransaction = createResponse.getBody();
        assert createdTransaction != null;
        UUID transactionId = createdTransaction.getId();

        ResponseEntity<Transactions> findResponse = restTemplate.getForEntity(
                baseUrl + "/api/transactions/" + transactionId,
                Transactions.class
        );

        assertEquals(HttpStatus.OK, findResponse.getStatusCode());
        Transactions foundTransaction = findResponse.getBody();

        assertNotNull(foundTransaction);
        assertEquals(transactionId, foundTransaction.getId());
        assertEquals(newTransaction.getUser1Id(), foundTransaction.getUser1Id());
        assertEquals(newTransaction.getUser2Id(), foundTransaction.getUser2Id());
    }
    @Test
    public void testDeleteTransaction() {
        com.ethan.apiproject.model.Transactions newTransaction = new com.ethan.apiproject.model.Transactions();
        newTransaction.setUser1Id(UUID.randomUUID().toString());
        newTransaction.setUser2Id(UUID.randomUUID().toString());

        ResponseEntity<com.ethan.apiproject.model.Transactions> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/transactions",
                newTransaction,
                com.ethan.apiproject.model.Transactions.class
        );

        com.ethan.apiproject.model.Transactions createdTransaction = createResponse.getBody();
        assert createdTransaction != null;
        UUID transactionId = createdTransaction.getId();

        restTemplate.delete(baseUrl + "/api/transactions/" + transactionId);

        ResponseEntity<com.ethan.apiproject.model.Transactions> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/transactions/" + transactionId,
                com.ethan.apiproject.model.Transactions.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        assertNull(getResponse.getBody());
    }
    @Test
    public void testTransactionHistory() {
        com.ethan.apiproject.model.Transactions newTransaction = new com.ethan.apiproject.model.Transactions();
        newTransaction.setUser1Id(UUID.randomUUID().toString());
        newTransaction.setUser2Id(UUID.randomUUID().toString());

        ResponseEntity<com.ethan.apiproject.model.Transactions> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/transactions",
                newTransaction,
                com.ethan.apiproject.model.Transactions.class
        );

        com.ethan.apiproject.model.Transactions createdTransaction = createResponse.getBody();
        assert createdTransaction != null;

        ResponseEntity<com.ethan.apiproject.model.Transactions[]> historyResponse = restTemplate.getForEntity(
                baseUrl + "/api/users/1/transactions",
                com.ethan.apiproject.model.Transactions[].class
        );
        assertEquals(HttpStatus.OK, historyResponse.getStatusCode());
        com.ethan.apiproject.model.Transactions[] transactionHistory = historyResponse.getBody();
        assertNotNull(transactionHistory);
    }
    @Test
    public void testListTransactionsByUser() {
        Transactions newTransaction = new Transactions();
        newTransaction.setUser1Id(UUID.randomUUID().toString());
        newTransaction.setUser2Id(UUID.randomUUID().toString());
        newTransaction.setUser1Type(Type.B2C);
        newTransaction.setUser2Type(Type.B2C);

        ResponseEntity<Transactions> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/transactions",
                newTransaction,
                Transactions.class
        );

        Transactions createdTransaction = createResponse.getBody();
        assertNotNull(createdTransaction);

        ResponseEntity<Page<Transactions>> transactionsByUserResponse = restTemplate.exchange(
                baseUrl + "/api/transactions/1/transactions?page=0&size=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, transactionsByUserResponse.getStatusCode());
        Page<Transactions> transactionsByUser = transactionsByUserResponse.getBody();
        assertNotNull(transactionsByUser);
        assertEquals(1, transactionsByUser.getTotalElements());
    }


}
