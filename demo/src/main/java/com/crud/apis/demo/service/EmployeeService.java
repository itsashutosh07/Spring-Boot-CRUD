package com.crud.apis.demo.service;

import com.crud.apis.demo.batch.EmployeeBatchProcessor;
import com.crud.apis.demo.dao.EmployeeDao;
import com.crud.apis.demo.entity.Employee;
import com.crud.apis.demo.repository.EmployeeRepository;
import jakarta.persistence.PersistenceException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private EmployeeDao dao;

    // Get
    @Transactional(readOnly = true)
    public List<Employee> getEmployees() {
        long startTime = System.nanoTime();
        List<Employee> employees = dao.fetchAllEmployees();
        System.out.println("No. of employee(s) found in database: " + employees.size());

        if (employees.isEmpty()) return employees;
        else {
            for (Employee theEmployee : employees) {
                String maskedEmail = maskEMailAddress(theEmployee.getEmail());
                theEmployee.setEmail(maskedEmail);
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double seconds = (double) duration / 1_000_000_000.0;
        System.out.println(" took " + seconds + " seconds.");
        return employees;
    }

    public Employee getEmployeeById(int id) {
        Employee theEmployee = dao.fetchEmployeeById(id);
        if (theEmployee == null) {
            System.out.println("Employee with id '" + id + "' NOT found!");
            return null;
        }
        String maskedEmail = maskEMailAddress(theEmployee.getEmail());
        theEmployee.setEmail(maskedEmail);

        System.out.println("Employee with id '" + id + "' found!");
        return dao.fetchEmployeeById(id);
    }
    public Employee getEmployeeByEmail(String email){
        Employee theEmployee = dao.fetchEmployeeByEmail(email);
        if (theEmployee == null) {
            System.out.println("No unique employee found with the given email-id '" + email + "'.");
            return null;
        }
        String maskedEmail = maskEMailAddress(theEmployee.getEmail());
        theEmployee.setEmail(maskedEmail);
        System.out.println("Employee with email-id: '" + maskedEmail + "' found!");
        return theEmployee;
    }

    // Post
    public Employee saveEmployee(Employee employee) {
        System.out.println("Data of an employee added!");
        return dao.commitEmployee(employee);
    }
    public List<Employee> saveEmployees(List<Employee> employees) {
        return dao.commitEmployees(employees);
    }

    // Update
    public Employee updateEmployee(Employee employee) {
        Employee theEmployee = dao.changeEmployee(employee);
        if (theEmployee == null) {
//            System.out.println("Employee id '" + employee.getId() + "' NOT found in DB for updation!");
            return null;
        }
        String maskedEmail = maskEMailAddress(theEmployee.getEmail());
        theEmployee.setEmail(maskedEmail);
//        System.out.println("Employee details changed successfully for id : " + theEmployee.getId());
        return theEmployee;
    }

    // Delete
    public String removeEmployee(int id) {
        return dao.deleteEmployee(id);
    }

    // Bulk Add
    public int bulkSaveEmployees(MultipartFile file) {
//        return dao.bulkCommitEmployees(file);
        int i = 0, threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("firstName", "lastName", "gender", "email")
                    .withFirstRecordAsHeader()
                    .parse(reader);

            List<Employee> batch = new ArrayList<>();
            for (CSVRecord record : records) {
                Employee employee = new Employee();
                employee.setFirstName(record.get("firstName"));
                employee.setLastName(record.get("lastName"));
                employee.setGender(Employee.Gender.valueOf(record.get("gender")));
                employee.setEmail(record.get("email"));
//                repository.save(employee); // For sequential saves to DB
                batch.add(employee); // For adding employee to batches and then saving to DB in parallel
                i++;

                if (batch.size() == 10) { // When 10 records are ready then we submit the task to executor
                    executor.submit(new EmployeeBatchProcessor(batch, dao));
                    batch = new ArrayList<>();
                }
            }

            // Last batch processing if it has less than 10 records
            if (!batch.isEmpty()) {
                executor.submit(new EmployeeBatchProcessor(batch, dao));
            }
            executor.shutdown(); // Shutdown executor when all tasks have been submitted.

            System.out.println("Bulk upload of employee records started.");
            return 1;
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid file format. Please upload a CSV File.");
            return 0;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String maskEMailAddress(String email_id) {
        String id = email_id.substring(0, email_id.lastIndexOf("@"));
        String domain = email_id.substring(email_id.lastIndexOf("@"));
        if (id.length() == 0) {
            return email_id;
        }
        else if (id.length() == 1) {
            return "*"+domain;
        }
        switch (id.length()) {
            case 2 -> id = id.substring(0, 1) + "*"; // ab -> a*
            case 3 -> id = id.substring(0, 1) + "*" + id.substring(2); // abc -> a*c
            case 4 -> id = id.substring(0, 1) + "**" + id.substring(3); // abcd -> a**d
            default -> { //abcdefg@gmail.com -> ab***fg@gmail.com
                String masks = String.join("", java.util.Collections.nCopies(id.length() - 4, "*"));
                id = id.substring(0, 2) + masks + id.substring(id.length() - 2);
            }
        }
        return id + domain;
    }

}