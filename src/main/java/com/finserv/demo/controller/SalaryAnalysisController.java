package com.finserv.demo.controller;

import com.finserv.demo.dto.SalaryAnalysisResult;
import com.finserv.demo.entity.Payment;
import com.finserv.demo.service.SalaryAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for salary analysis operations
 */
@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(SalaryAnalysisController.class);
    
    @Autowired
    private SalaryAnalysisService salaryAnalysisService;
    
    /**
     * Get the highest salary not credited on 1st day of month
     * 
     * @return Salary analysis result with employee details
     */
    @GetMapping("/highest-not-first-day")
    public ResponseEntity<SalaryAnalysisResult> getHighestSalaryNotOnFirstDay() {
        logger.info("Request received for highest salary not on first day");
        
        try {
            SalaryAnalysisResult result = salaryAnalysisService.findHighestSalaryNotOnFirstDay();
            
            if (result != null) {
                logger.info("Successfully retrieved salary analysis result: {}", result);
                return ResponseEntity.ok(result);
            } else {
                logger.warn("No salary analysis result found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving salary analysis: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get the SQL query for the salary analysis
     * 
     * @return SQL query string
     */
    @GetMapping("/sql-query")
    public ResponseEntity<String> getSqlQuery() {
        logger.info("Request received for SQL query");
        
        try {
            String sqlQuery = salaryAnalysisService.getSqlQuery();
            logger.info("Successfully retrieved SQL query");
            return ResponseEntity.ok(sqlQuery);
            
        } catch (Exception e) {
            logger.error("Error retrieving SQL query: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get all payments not on the 1st day of month for verification
     * 
     * @return List of payments
     */
    @GetMapping("/payments-not-first-day")
    public ResponseEntity<List<Payment>> getPaymentsNotOnFirstDay() {
        logger.info("Request received for payments not on first day");
        
        try {
            List<Payment> payments = salaryAnalysisService.getPaymentsNotOnFirstDay();
            logger.info("Successfully retrieved {} payments not on first day", payments.size());
            return ResponseEntity.ok(payments);
            
        } catch (Exception e) {
            logger.error("Error retrieving payments not on first day: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get the maximum amount among payments not on the 1st day
     * 
     * @return Maximum amount
     */
    @GetMapping("/max-amount-not-first-day")
    public ResponseEntity<BigDecimal> getMaxAmountNotOnFirstDay() {
        logger.info("Request received for max amount not on first day");
        
        try {
            BigDecimal maxAmount = salaryAnalysisService.getMaxAmountNotOnFirstDay();
            
            if (maxAmount != null) {
                logger.info("Successfully retrieved max amount: {}", maxAmount);
                return ResponseEntity.ok(maxAmount);
            } else {
                logger.warn("No max amount found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving max amount: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Salary Analysis Service is running");
    }
}
