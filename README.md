# FinServ API - Salary Analysis Solution

## Overview

This Spring Boot application provides a solution for analyzing salary data to find the highest salary that was credited to an employee, but only for transactions that were not made on the 1st day of any month. The solution includes both SQL queries and a RESTful API implementation.

## Problem Statement

Find the highest salary that was credited to an employee, but only for transactions that were not made on the 1st day of any month. Along with the salary, extract the employee data including:
- **SALARY**: The highest salary that was credited not on the 1st day of the month
- **NAME**: Combined first name and last name (e.g., "John Doe")
- **AGE**: The age of the employee who received that salary
- **DEPARTMENT_NAME**: Name of the department against employee

## Database Schema

### Tables

1. **DEPARTMENT**
   - `DEPARTMENT_ID` (Primary Key)
   - `DEPARTMENT_NAME`

2. **EMPLOYEE**
   - `EMP_ID` (Primary Key)
   - `FIRST_NAME`
   - `LAST_NAME`
   - `DOB` (Date of Birth)
   - `GENDER`
   - `DEPARTMENT` (Foreign Key referencing DEPARTMENT_ID)

3. **PAYMENTS**
   - `PAYMENT_ID` (Primary Key)
   - `EMP_ID` (Foreign Key referencing EMP_ID)
   - `AMOUNT` (Salary credited)
   - `PAYMENT_TIME` (Date and time of the transaction)

## SQL Solution

The solution is provided in `SQL_SOLUTION.sql` with multiple approaches:

### Primary Solution (MySQL)
```sql
SELECT 
    p.AMOUNT AS SALARY,
    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
    d.DEPARTMENT_NAME
FROM PAYMENTS p
JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE DAY(p.PAYMENT_TIME) != 1
ORDER BY p.AMOUNT DESC
LIMIT 1;
```

### Alternative Solutions
- **Subquery approach**: Using subquery to find max amount
- **Window function approach**: Using ROW_NUMBER() for MySQL 8.0+
- **Cross-platform solutions**: For PostgreSQL and SQL Server

## Application Architecture

### Refactored Structure

The application has been refactored to follow clean architecture principles:

```
src/main/java/com/finserv/demo/
├── config/
│   └── RestTemplateConfig.java
├── controller/
│   └── SalaryAnalysisController.java      # NEW: REST API for salary analysis
├── dto/
│   ├── SolutionRequest.java
│   ├── WebhookRequest.java
│   ├── WebhookResponse.java
│   └── SalaryAnalysisResult.java          # NEW: DTO for salary analysis results
├── entity/
│   ├── Department.java
│   ├── Employee.java
│   ├── Order.java
│   └── Payment.java
├── repository/
│   ├── DepartmentRepository.java
│   ├── EmployeeRepository.java
│   ├── OrderRepository.java
│   └── PaymentRepository.java             # ENHANCED: Added custom query methods
├── service/
│   ├── WebhookService.java                # REFACTORED: Integrated with salary analysis
│   └── SalaryAnalysisService.java         # NEW: Dedicated service for salary analysis
└── DemoApplication.java
```

### Key Components

1. **SalaryAnalysisService**: Core business logic for salary analysis
2. **SalaryAnalysisController**: REST API endpoints for salary analysis
3. **PaymentRepository**: Enhanced with custom query methods
4. **SalaryAnalysisResult**: DTO for structured response data

## API Endpoints

### Salary Analysis Endpoints

- `GET /api/salary/highest-not-first-day` - Get highest salary not on 1st day
- `GET /api/salary/sql-query` - Get the SQL query used
- `GET /api/salary/payments-not-first-day` - Get all payments not on 1st day
- `GET /api/salary/max-amount-not-first-day` - Get maximum amount not on 1st day
- `GET /api/salary/health` - Health check endpoint

### Sample Response

```json
{
  "salary": 74998.00,
  "name": "Emily Brown",
  "age": 32,
  "departmentName": "Sales"
}
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Test the API
```bash
# Get highest salary not on 1st day
curl http://localhost:8080/api/salary/highest-not-first-day

# Get SQL query
curl http://localhost:8080/api/salary/sql-query

# Health check
curl http://localhost:8080/api/salary/health
```

## Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SalaryAnalysisServiceTest
```

### Test Coverage
The application includes comprehensive unit tests for:
- SalaryAnalysisService
- Repository methods
- Controller endpoints
- Business logic validation

## Data Population

The application automatically populates sample data on startup:
- 6 departments (HR, Finance, Engineering, Sales, Marketing, IT)
- 10 employees with realistic demographics
- 16 payment records with various dates and amounts

## Expected Results

Based on the sample data:
- **Highest salary not on 1st day**: 74,998.00
- **Employee**: Emily Brown
- **Age**: 32 (as of 2025)
- **Department**: Sales
- **Payment Date**: 2025-03-02 (not 1st day)

## Technical Features

- **Spring Boot 3.5.5** with Java 17
- **Spring Data JPA** for data access
- **H2 Database** for in-memory storage
- **RESTful API** with proper error handling
- **Comprehensive logging** for debugging
- **Unit testing** with Mockito and JUnit 5
- **Validation** and error handling
- **Actuator** for monitoring and health checks

## Future Enhancements

1. **Database Integration**: Connect to external databases (MySQL, PostgreSQL)
2. **Caching**: Implement Redis caching for frequently accessed data
3. **Authentication**: Add JWT-based authentication
4. **Swagger Documentation**: API documentation with OpenAPI
5. **Metrics**: Prometheus metrics integration
6. **Docker**: Containerization support

## Troubleshooting

### Common Issues

1. **Port conflicts**: Change server.port in application.properties
2. **Database issues**: Check H2 console at http://localhost:8080/h2-console
3. **Test failures**: Ensure all dependencies are properly installed

### Logs
Check application logs for detailed error information and debugging details.

## Contributing

1. Follow the existing code structure and naming conventions
2. Add comprehensive tests for new functionality
3. Update documentation for any API changes
4. Ensure all tests pass before submitting changes

## License

This project is licensed under the MIT License.
