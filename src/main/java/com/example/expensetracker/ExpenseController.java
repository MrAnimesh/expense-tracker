package com.example.expensetracker;

import com.azure.storage.blob.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final BlobContainerClient blobContainerClient;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
        
        // Grab the connection string variable you saved in the Azure Portal UI
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        
        if (connectionString != null) {
            this.blobContainerClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient()
                .getBlobContainerClient("receipts"); // Matches the UI container folder
        } else {
            this.blobContainerClient = null;
        }
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @PostMapping
    public Expense createExpense(
            @RequestParam("amount") double amount,
            @RequestParam("category") String category,
            @RequestParam("merchant") String merchant,
            @RequestParam(value = "receipt", required = false) MultipartFile receipt) throws IOException {

        String fileUrl = null;

        // If an image file is attached, stream it straight up to Azure Storage
        if (receipt != null && !receipt.isEmpty() && blobContainerClient != null) {
            String uniqueFileName = UUID.randomUUID() + "-" + receipt.getOriginalFilename();
            BlobClient blobClient = blobContainerClient.getBlobClient(uniqueFileName);
            
            // Push raw file bytes up to the cloud storage vault
            blobClient.upload(receipt.getInputStream(), receipt.getSize(), true);
            fileUrl = blobClient.getBlobUrl(); // Extract the public web image link
        }

        // Save everything securely to the PostgreSQL DB
        Expense newExpense = new Expense(amount, category, merchant, fileUrl);
        return expenseRepository.save(newExpense);
    }
}
