package com.finserv.demo.service;

import com.finserv.demo.dto.SolutionRequest;
import com.finserv.demo.dto.WebhookRequest;
import com.finserv.demo.dto.WebhookResponse;
import com.finserv.demo.entity.Department;
import com.finserv.demo.entity.Employee;
import com.finserv.demo.entity.Order;
import com.finserv.demo.entity.Payment;
import com.finserv.demo.repository.DepartmentRepository;
import com.finserv.demo.repository.EmployeeRepository;
import com.finserv.demo.repository.OrderRepository;
import com.finserv.demo.repository.PaymentRepository;
import com.finserv.demo.service.SalaryAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    
    private static final String WEBHOOK_GENERATION_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_TEST_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SalaryAnalysisService salaryAnalysisService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Application started. Beginning webhook process...");
        try {
            // Add a small delay to ensure all beans are properly initialized
            Thread.sleep(2000);
            processWebhookFlow();
        } catch (Exception e) {
            logger.error("Error during webhook process: ", e);
        }
    }
    
    public void processWebhookFlow() {
        try {
            // Step 1: Generate webhook
            logger.info("Step 1: Generating webhook...");
            WebhookResponse webhookResponse = generateWebhook();
            if (webhookResponse == null) {
                logger.error("Failed to generate webhook. Exiting process.");
                return;
            }
            
            // Step 2: Solve SQL problem based on registration number
            logger.info("Step 2: Solving SQL problem...");
            String sqlQuery = solveSqlProblem("REG12347"); // Using the registration number from our request
            
            // Step 3: Submit solution
            logger.info("Step 3: Submitting solution...");
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            
            logger.info("Webhook process completed successfully!");
            
        } catch (Exception e) {
            logger.error("Unexpected error during webhook process: ", e);
        }
    }
    
    private WebhookResponse generateWebhook() {
        try {
            WebhookRequest request = new WebhookRequest("John Doe", "REG12347", "john@example.com");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            logger.info("Sending webhook generation request: {}", request);
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                WEBHOOK_GENERATION_URL, entity, WebhookResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Webhook generated successfully: {}", response.getBody());
                return response.getBody();
            } else {
                logger.error("Failed to generate webhook. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception during webhook generation: ", e);
            return null;
        }
    }
    
    private String solveSqlProblem(String regNo) {
        try {
            // Extract last two digits from registration number
            String lastTwoDigits = regNo.substring(regNo.length() - 2);
            int lastTwoDigitsInt = Integer.parseInt(lastTwoDigits);
            
            logger.info("Registration number: {}, Last two digits: {}", regNo, lastTwoDigitsInt);
            
            if (lastTwoDigitsInt % 2 == 1) {
                // Odd number - Question 1: Employee analysis with age comparison
                logger.info("Odd number detected. Solving Employee Problem (Question 1)");
                return solveEmployeeProblem();
            } else {
                // Even number - Question 2: Salary analysis problem
                logger.info("Even number detected. Solving Salary Analysis Problem (Question 2)");
                return solveSalaryAnalysisProblem();
            }
        } catch (Exception e) {
            logger.error("Error solving SQL problem: ", e);
            // Return a default query in case of error
            return "SELECT 'Error occurred while generating query' as message";
        }
    }
    
    private String solveEmployeeProblem() {
        logger.info("Solving Employee Problem (Question 1)");
        
        try {
            // Populate sample data
            populateEmployeeData();
            
            // The SQL query for Question 1: Calculate younger employees count by department
            String sqlQuery = "SELECT " +
                             "e.EMP_ID, " +
                             "e.FIRST_NAME, " +
                             "e.LAST_NAME, " +
                             "d.DEPARTMENT_NAME, " +
                             "(SELECT COUNT(*) " +
                             "FROM employees e2 " +
                             "WHERE e2.DEPARTMENT = e.DEPARTMENT " +
                             "AND e2.DOB > e.DOB) AS YOUNGER_EMPLOYEES_COUNT " +
                             "FROM employees e " +
                             "JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                             "ORDER BY e.EMP_ID DESC";
            
            logger.info("Generated SQL Query for Employee Problem: {}", sqlQuery);
            return sqlQuery;
        } catch (Exception e) {
            logger.error("Error solving employee problem: ", e);
            return "SELECT 'Error in employee analysis' as message";
        }
    }
    
    private String solveSalaryAnalysisProblem() {
        logger.info("Solving Salary Analysis Problem (Question 2)");
        
        try {
            // Populate sample data
            populateEmployeeData();
            
            // Get the SQL query from the SalaryAnalysisService
            String sqlQuery = salaryAnalysisService.getSqlQuery();
            
            logger.info("Generated SQL Query for Salary Analysis Problem: {}", sqlQuery);
            return sqlQuery;
        } catch (Exception e) {
            logger.error("Error solving salary analysis problem: ", e);
            return "SELECT 'Error in salary analysis' as message";
        }
    }
    
    private void populateEmployeeData() {
        try {
            // First populate departments
            List<Department> departments = Arrays.asList(
                new Department("HR"),
                new Department("Finance"),
                new Department("Engineering"),
                new Department("Sales"),
                new Department("Marketing"),
                new Department("IT")
            );
            
            departmentRepository.saveAll(departments);
            logger.info("Populated {} department records successfully", departments.size());
            
            // Then populate employees
            List<Employee> employees = Arrays.asList(
                new Employee("John", "Williams", LocalDate.of(1980, 5, 15), "Male", 3L),
                new Employee("Sarah", "Johnson", LocalDate.of(1990, 7, 20), "Female", 2L),
                new Employee("Michael", "Smith", LocalDate.of(1985, 2, 10), "Male", 3L),
                new Employee("Emily", "Brown", LocalDate.of(1992, 11, 30), "Female", 4L),
                new Employee("David", "Jones", LocalDate.of(1988, 9, 5), "Male", 5L),
                new Employee("Olivia", "Davis", LocalDate.of(1995, 4, 12), "Female", 1L),
                new Employee("James", "Wilson", LocalDate.of(1983, 3, 25), "Male", 6L),
                new Employee("Sophia", "Anderson", LocalDate.of(1991, 8, 17), "Female", 4L),
                new Employee("Liam", "Miller", LocalDate.of(1979, 12, 1), "Male", 1L),
                new Employee("Emma", "Taylor", LocalDate.of(1993, 6, 28), "Female", 5L)
            );
            
            employeeRepository.saveAll(employees);
            logger.info("Populated {} employee records successfully", employees.size());
            
            // Finally populate payments
            List<Payment> payments = Arrays.asList(
                new Payment(2L, new BigDecimal("65784.00"), LocalDateTime.of(2025, 1, 1, 13, 44, 12, 824000000)),
                new Payment(4L, new BigDecimal("62736.00"), LocalDateTime.of(2025, 1, 6, 18, 36, 37, 892000000)),
                new Payment(1L, new BigDecimal("69437.00"), LocalDateTime.of(2025, 1, 1, 10, 19, 21, 563000000)),
                new Payment(3L, new BigDecimal("67183.00"), LocalDateTime.of(2025, 1, 2, 17, 21, 57, 341000000)),
                new Payment(2L, new BigDecimal("66273.00"), LocalDateTime.of(2025, 2, 1, 11, 49, 15, 764000000)),
                new Payment(5L, new BigDecimal("71475.00"), LocalDateTime.of(2025, 1, 1, 7, 24, 14, 453000000)),
                new Payment(1L, new BigDecimal("70837.00"), LocalDateTime.of(2025, 2, 3, 19, 11, 31, 553000000)),
                new Payment(6L, new BigDecimal("69628.00"), LocalDateTime.of(2025, 1, 2, 10, 41, 15, 113000000)),
                new Payment(4L, new BigDecimal("71876.00"), LocalDateTime.of(2025, 2, 1, 12, 16, 47, 807000000)),
                new Payment(3L, new BigDecimal("70098.00"), LocalDateTime.of(2025, 2, 3, 10, 11, 17, 341000000)),
                new Payment(6L, new BigDecimal("67827.00"), LocalDateTime.of(2025, 2, 2, 19, 21, 27, 753000000)),
                new Payment(5L, new BigDecimal("69871.00"), LocalDateTime.of(2025, 2, 5, 17, 54, 17, 453000000)),
                new Payment(2L, new BigDecimal("72984.00"), LocalDateTime.of(2025, 3, 5, 9, 37, 35, 974000000)),
                new Payment(1L, new BigDecimal("67982.00"), LocalDateTime.of(2025, 3, 1, 6, 9, 51, 983000000)),
                new Payment(6L, new BigDecimal("70198.00"), LocalDateTime.of(2025, 3, 2, 10, 34, 35, 753000000)),
                new Payment(4L, new BigDecimal("74998.00"), LocalDateTime.of(2025, 3, 2, 9, 27, 26, 162000000))
            );
            
            paymentRepository.saveAll(payments);
            logger.info("Populated {} payment records successfully", payments.size());
            
        } catch (Exception e) {
            logger.error("Error populating employee data: ", e);
        }
    }
    
    private void populateOrderData() {
        try {
            List<Order> orders = Arrays.asList(
                new Order("John Doe", "Laptop", 2, new BigDecimal("999.99"), LocalDate.of(2023, 6, 15), "Completed"),
                new Order("Jane Smith", "Mouse", 5, new BigDecimal("29.99"), LocalDate.of(2023, 7, 20), "Completed"),
                new Order("Bob Johnson", "Keyboard", 1, new BigDecimal("149.99"), LocalDate.of(2023, 8, 10), "Completed"),
                new Order("Alice Brown", "Monitor", 3, new BigDecimal("299.99"), LocalDate.of(2023, 9, 5), "Completed")
            );
            
            orderRepository.saveAll(orders);
            logger.info("Populated {} order records successfully", orders.size());
        } catch (Exception e) {
            logger.error("Error populating order data: ", e);
        }
    }
    
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            if (webhookUrl == null || accessToken == null || sqlQuery == null) {
                logger.error("Invalid parameters for solution submission. webhookUrl: {}, accessToken: {}, sqlQuery: {}", 
                    webhookUrl, accessToken != null ? "***" : "null", sqlQuery);
                return;
            }
            
            SolutionRequest request = new SolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);
            
            logger.info("Submitting solution to webhook: {}", webhookUrl);
            logger.info("SQL Query: {}", sqlQuery);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                webhookUrl, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Solution submitted successfully. Response: {}", response.getBody());
            } else {
                logger.error("Failed to submit solution. Status: {}, Response: {}", 
                    response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("Exception during solution submission: ", e);
        }
    }
}
