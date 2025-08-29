SHOW TABLES;

-- Check departments table structure
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'DEPARTMENTS';

-- Check employees table structure  
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'EMPLOYEES';

-- Check payments table structure
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'PAYMENTS';

-- Check if data exists
SELECT * FROM departments;
SELECT * FROM employees;
SELECT * FROM payments;

SELECT 
    e.EMP_ID, 
    e.FIRST_NAME, 
    e.LAST_NAME, 
    d.DEPARTMENT_NAME, 
    (SELECT COUNT(*) 
     FROM employees e2 
     WHERE e2.DEPARTMENT = e.DEPARTMENT 
     AND e2.DOB > e.DOB) AS YOUNGER_EMPLOYEES_COUNT 
FROM employees e 
JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID 
ORDER BY e.EMP_ID DESC;

-- ============================================================================
-- SOLUTION: Find highest salary not credited on 1st day of any month
-- ============================================================================

-- Solution 1: Using JOIN approach (CORRECTED TABLE NAMES)
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

-- Solution 2: Using subquery approach (CORRECTED TABLE NAMES)
SELECT 
    p.AMOUNT AS SALARY,
    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
    d.DEPARTMENT_NAME
FROM payments p
JOIN employees e ON p.EMP_ID = e.EMP_ID
JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE p.AMOUNT = (
    SELECT MAX(AMOUNT) 
    FROM payments 
    WHERE DAY(PAYMENT_TIME) != 1
);

-- Solution 3: Using window function approach (for MySQL 8.0+) (CORRECTED TABLE NAMES)
SELECT 
    SALARY,
    NAME,
    AGE,
    DEPARTMENT_NAME
FROM (
    SELECT 
        p.AMOUNT AS SALARY,
        CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
        TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
        d.DEPARTMENT_NAME,
        ROW_NUMBER() OVER (ORDER BY p.AMOUNT DESC) as rn
    FROM payments p
    JOIN employees e ON p.EMP_ID = e.EMP_ID
    JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID
    WHERE DAY(p.PAYMENT_TIME) != 1
) ranked
WHERE rn = 1;

-- ============================================================================
-- ALTERNATIVE APPROACHES FOR DIFFERENT DATABASE SYSTEMS
-- ============================================================================

-- For PostgreSQL (using EXTRACT instead of DAY)
/*
SELECT 
    p.AMOUNT AS SALARY,
    e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, e.DOB)) AS AGE,
    d.DEPARTMENT_NAME
FROM payments p
JOIN employees e ON p.EMP_ID = e.EMP_ID
JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1
ORDER BY p.AMOUNT DESC
LIMIT 1;
*/

-- For SQL Server (using DATEPART instead of DAY)
/*
SELECT 
    p.AMOUNT AS SALARY,
    e.FIRST_NAME + ' ' + e.LAST_NAME AS NAME,
    DATEDIFF(YEAR, e.DOB, GETDATE()) AS AGE,
    d.DEPARTMENT_NAME
FROM payments p
JOIN employees e ON p.EMP_ID = e.EMP_ID
JOIN departments d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE DATEPART(DAY, p.PAYMENT_TIME) != 1
ORDER BY p.AMOUNT DESC
LIMIT 1;
*/

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Verify payments not on 1st day of month
SELECT 
    PAYMENT_ID,
    EMP_ID,
    AMOUNT,
    PAYMENT_TIME,
    DAY(PAYMENT_TIME) as payment_day
FROM payments
WHERE DAY(PAYMENT_TIME) != 1
ORDER BY AMOUNT DESC;

-- Verify the highest amount among non-1st day payments
SELECT 
    MAX(AMOUNT) as max_amount_not_first_day
FROM payments
WHERE DAY(PAYMENT_TIME) != 1;

-- Count payments by day of month
SELECT 
    DAY(PAYMENT_TIME) as day_of_month,
    COUNT(*) as payment_count,
    MAX(AMOUNT) as max_amount
FROM payments
GROUP BY DAY(PAYMENT_TIME)
ORDER BY day_of_month;