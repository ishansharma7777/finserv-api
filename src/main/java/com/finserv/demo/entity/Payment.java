package com.finserv.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private Long paymentId;
    
    @Column(name = "EMP_ID")
    private Long empId;
    
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    
    @Column(name = "PAYMENT_TIME")
    private LocalDateTime paymentTime;

    // Default constructor
    public Payment() {}

    // Parameterized constructor
    public Payment(Long empId, BigDecimal amount, LocalDateTime paymentTime) {
        this.empId = empId;
        this.amount = amount;
        this.paymentTime = paymentTime;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", empId=" + empId +
                ", amount=" + amount +
                ", paymentTime=" + paymentTime +
                '}';
    }
}
