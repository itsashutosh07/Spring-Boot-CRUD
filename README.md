#Built a bunch of CRUD APIs using Java & Spring Boot.

## Salient Features:
  - User authentication and role based access management all the APIs.
  - Multi-threaded batch processing for file uploads.
  - Persiting data to a RDBMS, here MySQL server.
  - Caching data for faster retrieval using Redis.
  - Publishing Events and Subscribing to Events via Apache Kafka.
  - Utilization of standardised practice of logic division in differnt packages like - Entity, Controller, Service, DAO, Security, Batch. 


###Overview:
**1. An Employee Entity having following features:**
  a. `EmployeeID` - Auto Generated - Integer
  b. `FirstName` - String
  c. `LastName` - String
  d. `Email` - String
  e. `Gender` - Enumerated
**2. Employee Controller with the following APIs exposed:**
  a. GET Mapping:
    - Get list of all employees
    - Get employee by `EmployeeID`
    - Get employee by `Email`
  b. POST Mapping:
    - Add an employee
    - Add a list of employees
    - ==Upload== List of Employees using a `MultipartFile`
      - _Multi-threading support built for file upload._
  c. PUT Mapping:
    - Edit employee data using `EmployeeID`
  d. DELETE Mapping:
    - Delete employee data using `EmployeeID`
    
