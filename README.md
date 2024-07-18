# Built a bunch of CRUD APIs using Java & Spring Boot.

## Salient Features:
  - User authentication and role based access management all the APIs.
  - Multi-threaded batch processing for file uploads.
  - Persiting data to a RDBMS, here MySQL server.
  - Caching data for faster retrieval using Redis.
  - Publishing Events and Subscribing to Events via Apache Kafka.
  - Utilization of standardised practice of logic division in differnt packages like - Entity, Controller, Service, DAO, Security, Batch. 


### Overview:
1. **An Employee Entity having following features:**
    1. `EmployeeID` - Auto Generated - Integer
    2. `FirstName` - String
    3. `LastName` - String
    4. `Email` - String
    5. `Gender` - Enumerated
2. **Employee Controller with the following APIs exposed:**
    1. GET Mapping:
        - Get list of all employees
        - Get employee by `EmployeeID`
        - Get employee by `Email`
    2. POST Mapping:
        - Add an employee
        - Add a list of employees
        - _Upload_ List of Employees using a `MultipartFile`
            > Multi-threading support built for CSV file upload.
    3. PUT Mapping:
        - Edit employee data using `EmployeeID`
    4. DELETE Mapping:
        - Delete employee data using `EmployeeID`
    
