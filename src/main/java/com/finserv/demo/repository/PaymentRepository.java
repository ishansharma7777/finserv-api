package com.finserv.demo.repository;

import com.finserv.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find the highest payment amount among payments not made on the 1st day of month
     * 
     * @return Optional containing the highest payment or empty if none found
     */
    @Query("SELECT p FROM Payment p WHERE DAY(p.paymentTime) != 1 ORDER BY p.amount DESC")
    Optional<Payment> findHighestPaymentNotOnFirstDay();
    
    /**
     * Find all payments not made on the 1st day of month
     * 
     * @return List of payments not on 1st day
     */
    @Query("SELECT p FROM Payment p WHERE DAY(p.paymentTime) != 1 ORDER BY p.amount DESC")
    List<Payment> findByPaymentTimeNotOnFirstDay();
    
    /**
     * Find the maximum amount among payments not made on the 1st day of month
     * 
     * @return Maximum amount or null if no payments found
     */
    @Query("SELECT MAX(p.amount) FROM Payment p WHERE DAY(p.paymentTime) != 1")
    BigDecimal findMaxAmountNotOnFirstDay();
    
    /**
     * Find payments by employee ID
     * 
     * @param empId Employee ID
     * @return List of payments for the employee
     */
    List<Payment> findByEmpId(Long empId);
    
    /**
     * Find payments by amount range
     * 
     * @param minAmount Minimum amount
     * @param maxAmount Maximum amount
     * @return List of payments within the range
     */
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                     @Param("maxAmount") BigDecimal maxAmount);
}
