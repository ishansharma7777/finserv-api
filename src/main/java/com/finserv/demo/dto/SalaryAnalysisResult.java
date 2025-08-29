package com.finserv.demo.dto;

import java.math.BigDecimal;

/**
 * DTO for salary analysis result
 * Contains the highest salary not credited on 1st day of month along with employee details
 */
public class SalaryAnalysisResult {
    private BigDecimal salary;
    private String name;
    private Integer age;
    private String departmentName;

    // Default constructor
    public SalaryAnalysisResult() {}

    // Parameterized constructor
    public SalaryAnalysisResult(BigDecimal salary, String name, Integer age, String departmentName) {
        this.salary = salary;
        this.name = name;
        this.age = age;
        this.departmentName = departmentName;
    }

    // Getters and Setters
    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "SalaryAnalysisResult{" +
                "salary=" + salary +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
