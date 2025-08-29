package com.finserv.demo.service;

import com.finserv.demo.dto.SalaryAnalysisResult;
import com.finserv.demo.entity.Department;
import com.finserv.demo.entity.Employee;
import com.finserv.demo.entity.Payment;
import com.finserv.demo.repository.DepartmentRepository;
import com.finserv.demo.repository.EmployeeRepository;
import com.finserv.demo.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service for analyzing salary data and finding specific payment patterns
 */
@Service
public class SalaryAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(SalaryAnalysisService.class);
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    /**
     * Find the highest salary that was credited to an employee, 
     * but only for transactions that were not made on the 1st day of any month
     * 
     * @return SalaryAnalysisResult containing salary, employee name, age, and department
     */
    public SalaryAnalysisResult findHighestSalaryNotOnFirstDay() {
        logger.info("Finding highest salary not credited on 1st day of month");
        
        try {
            // Find the highest payment amount among non-1st day payments
            Optional<Payment> highestPayment = paymentRepository.findHighestPaymentNotOnFirstDay();
            
            if (highestPayment.isEmpty()) {
                logger.warn("No payments found that are not on the 1st day of month");
                return null;
            }
            
            Payment payment = highestPayment.get();
            logger.info("Found highest payment: ID={}, Amount={}, Date={}", 
                payment.getPaymentId(), payment.getAmount(), payment.getPaymentTime());
            
            // Get employee details
            Optional<Employee> employeeOpt = employeeRepository.findById(payment.getEmpId());
            if (employeeOpt.isEmpty()) {
                logger.error("Employee not found for payment ID: {}", payment.getPaymentId());
                return null;
            }
            
            Employee employee = employeeOpt.get();
            
            // Get department details
            Optional<Department> departmentOpt = departmentRepository.findById(employee.getDepartment());
            if (departmentOpt.isEmpty()) {
                logger.error("Department not found for employee ID: {}", employee.getEmpId());
                return null;
            }
            
            Department department = departmentOpt.get();
            
            // Calculate age
            int age = calculateAge(employee.getDob());
            
            // Create result
            SalaryAnalysisResult result = new SalaryAnalysisResult(
                payment.getAmount(),
                employee.getFirstName() + " " + employee.getLastName(),
                age,
                department.getDepartmentName()
            );
            
            logger.info("Salary analysis result: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Error finding highest salary not on first day: ", e);
            return null;
        }
    }
    
    /**
     * Get the SQL query for finding the highest salary not on first day
     * 
     * @return SQL query string
     */
    public String getSqlQuery() {
        return """
            SELECT 
                p.AMOUNT AS SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
                TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
                d.DEPARTMENT_NAME
            FROM payments p
            JOIN employees e ON p.EMP_ID = e.EMP_ID
            JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE DAY(p.PAYMENT_TIME) != 1
            ORDER BY p.AMOUNT DESC
            LIMIT 1;
            """;
    }
    
    /**
     * Calculate age from date of birth
     * 
     * @param dob Date of birth
     * @return Age in years
     */
    private int calculateAge(LocalDate dob) {
        return (int) ChronoUnit.YEARS.between(dob, LocalDate.now());
    }
    
    /**
     * Get all payments not on the 1st day of month for verification
     * 
     * @return List of payments
     */
    public List<Payment> getPaymentsNotOnFirstDay() {
        return paymentRepository.findByPaymentTimeNotOnFirstDay();
    }
    
    /**
     * Get the maximum amount among payments not on the 1st day
     * 
     * @return Maximum amount or null if no payments found
     */
    public BigDecimal getMaxAmountNotOnFirstDay() {
        return paymentRepository.findMaxAmountNotOnFirstDay();
    }
}
