package com.crud.apis.demo.dao;

//import com.crud.apis.demo.batch.EmployeeBatchProcessor;
import com.crud.apis.demo.entity.Employee;
import com.crud.apis.demo.repository.EmployeeRepository;
//import jakarta.persistence.PersistenceException;
//import jakarta.transaction.Transactional;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;
//import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

@Component
public class EmployeeDao {
    @Autowired
    private EmployeeRepository repository;

    // Get
    public List<Employee> fetchAllEmployees() {
        return repository.findAll();
    }

    public Employee fetchEmployeeById(int id) {
        Employee daoEmployee = repository.findById(id).orElse(null);
//        if (daoEmployee == null) {
//            throw new NoSuchElementException("Employee not found with id: " + id);
//        }
        return daoEmployee;
    }

    public Employee fetchEmployeeByEmail(String email) {
        try {
            return repository.findByEmail(email);
        }
        catch (Exception e) {
            System.out.println("No unique employee found with the given email-id '" + email + "'.");
            return null;
        }
    }

    //Post
    private boolean employeeAlreadyPresent(Employee checkEmployee) {
        List<Employee> savedEmployees = repository.findAll();
        for (Employee theEmployee : savedEmployees) {
            if (checkEmployee.getId() == theEmployee.getId()) continue;
            if (checkEmployee.getEmail().equals(theEmployee.getEmail()))
                return true;
        }
        return false;
    }

    public Employee commitEmployee(Employee employee) {
        if (employeeAlreadyPresent(employee)) {
            System.out.println("Employee already exists in the database!");
            return null;
        }
        return repository.save(employee);
    }

    public List<Employee> commitEmployees(List<Employee> employees) {
        List<Employee> uniqueEmployees = new ArrayList<>();
        for (Employee theEmployee : employees) {
            if (employeeAlreadyPresent(theEmployee)) continue;
            uniqueEmployees.add(commitEmployee(theEmployee));
        }
        System.out.println("Details of (" + uniqueEmployees.size() + "/" + employees.size() + ") employees added!");
        if (uniqueEmployees.size() == 0) return null;
        return repository.saveAll(uniqueEmployees);
    }

    // Update
//    @Transactional
    public Employee changeEmployee(Employee newEmployee) {
        Employee oldEmployee = repository.findById(newEmployee.getId()).orElse(null);
        System.out.println("Employee Record fetched!");
        if (oldEmployee == null) {
            System.out.println("No employee exists with id '" + newEmployee.getId() + "'. Please enter a valid id.");
            return null;
//            throw new NoSuchElementException("Employee id: " + newEmployee.getId() + " not found! Please enter valid id.");
        }
        if (employeeAlreadyPresent(newEmployee)) {
            System.out.println("Please enter different email as it's not unique!");
            return null;
        }
        oldEmployee.setFirstName(newEmployee.getFirstName());
        oldEmployee.setLastName(newEmployee.getLastName());
        oldEmployee.setEmail(newEmployee.getEmail());
        oldEmployee.setGender(newEmployee.getGender());
        System.out.println("Employee details changed successfully for id : " + newEmployee.getId());
        return repository.save(oldEmployee);
    }

    // Delete
    public String deleteEmployee(int id) {
        Employee theEmployee = repository.findById(id).orElse(null);
        if (theEmployee == null) {
            System.out.println("Cannot delete employee id-" + id + " as not found!");
            return null;
//            throw new NoSuchElementException("Cannot delete employee id-" + id + " as not found!");
        }
        else {
            repository.deleteById(id);
            System.out.println("Employee of id-" + id + " removed!");
            return "Employee of id-" + id + " removed!";
        }
    }

    // Bulk save employees
//    public String bulkCommitEmployees(MultipartFile file) {
//        int i = 0, threadCount = 10;
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//        try (Reader reader = new InputStreamReader(file.getInputStream())) {
//            Iterable<CSVRecord> records = CSVFormat.DEFAULT
//                    .withHeader("firstName", "lastName", "gender", "email")
//                    .withFirstRecordAsHeader()
//                    .parse(reader);
//            EmployeeDao dao = new EmployeeDao();
//
//            List<Employee> batch = new ArrayList<>();
//            for (CSVRecord record : records) {
//                Employee employee = new Employee();
//                employee.setFirstName(record.get("firstName"));
//                employee.setLastName(record.get("lastName"));
//                employee.setGender(Employee.Gender.valueOf(record.get("gender")));
//                employee.setEmail(record.get("email"));
////                repository.save(employee); // For sequential saves to DB
//                batch.add(employee); // For adding employee to batches and then saving to DB in parallel
//                i++;
//
//                if (batch.size() == 10) { // When 10 records are ready then we submit the task to executor
//                    executor.submit(new EmployeeBatchProcessor(batch, dao));
//                    batch = new ArrayList<>();
//                }
//            }
//
//            // Last batch processing if it has less than 10 records
//            if (!batch.isEmpty()) {
//                executor.submit(new EmployeeBatchProcessor(batch, dao));
//            }
//            executor.shutdown(); // Shutdown executor when all tasks have been submitted.
//
//            System.out.println("Bulk upload of employee records started.");
//            return "Bulk upload of records was started!";
//        }
//        catch (PersistenceException e) {
//            System.out.println("Invalid file format. Please upload a CSV File.");
//            return "Invalid file format. Please upload a CSV File.";
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
