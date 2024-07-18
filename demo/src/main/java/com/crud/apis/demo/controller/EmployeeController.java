package com.crud.apis.demo.controller;

import com.crud.apis.demo.entity.Employee;
import com.crud.apis.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService service;


    // Get

    @GetMapping("/")
    public String sayHello() {
        return "Hello, Ashutosh!";
    }
    @GetMapping("/getemployees")
    public ResponseEntity<?> findEmployees() {
        if (service.getEmployees().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No employee found in database!");
        }
        else {
            return ResponseEntity.ok(service.getEmployees());
        }
    }
    @GetMapping("/getemployeebyid")
    public ResponseEntity<?> findEmployeeById(@RequestParam("id") int id) {
        Employee theEmployee = service.getEmployeeById(id);
        if (theEmployee == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No employee found with id : " + id);
        }
        return ResponseEntity.ok(theEmployee);
    }
    @GetMapping("/getemployeebyemail")
    public ResponseEntity<?> findEmployeeByEmail(@RequestParam("email") String email) {
        Employee theEmployee = service.getEmployeeByEmail(email);
        if (theEmployee == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No unique employee found with the given email-id '" + email + "'.");
        }
        return ResponseEntity.ok(theEmployee);
    }

    // Post

    @PostMapping("/addemployee")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        Employee theEmployee = service.saveEmployee(employee);
        if (theEmployee == null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("An employee with the same data already exists");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theEmployee);
    }
    @PostMapping("/addemployees")
    public ResponseEntity<?> addEmployees(@RequestBody List<Employee> employees) {
        List<Employee> allEmployees = service.saveEmployees(employees);
        if (allEmployees == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body("All given employees data already exists in the database");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(allEmployees);
    }

    // Update

    @PutMapping("/editemployee")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee newEmployee) {
        if (newEmployee.getId() == 0) {
            System.out.println("Employee ID required for updating employee data.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Employee ID required for updating employee data.");
        }
        Employee theEmployee = service.updateEmployee(newEmployee);
        if (theEmployee == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Either ID not present or Email not unique. Try again ");
        }
        return ResponseEntity.ok(theEmployee);
    }

    // Delete

    @DeleteMapping("/delete")
    public ResponseEntity<?> removeEmployee(@RequestParam("id") int id) {
        if (id == 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Employee ID required for deletion of employee data.");
        }
        String body = service.removeEmployee(id);
        if (body == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No employee found with id : " + id);
        }
        return ResponseEntity.ok(body);
    }

    // Bulk Add
    @PostMapping("/upload")
    public ResponseEntity<String> bulkAddEmployees(@RequestParam("file") MultipartFile file) throws IOException {
//        if(file.getOriginalFilename().endsWith(".csv"))
        try {
            int message = service.bulkSaveEmployees(file);
            if (message == 0) {
                return new ResponseEntity<>("Please upload correct file format", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            else {
                return new ResponseEntity<>("Upload of records was started!", HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @ExceptionHandler(NoSuchElementException.class)
//    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
//        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//    }


}
