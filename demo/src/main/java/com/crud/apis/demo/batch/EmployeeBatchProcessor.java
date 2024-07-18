package com.crud.apis.demo.batch;

import com.crud.apis.demo.dao.EmployeeDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.crud.apis.demo.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import com.crud.apis.demo.repository.EmployeeRepository;

import java.util.List;

//@Component
public class EmployeeBatchProcessor implements Runnable {

    private final List<Employee> batch;
    private static final Logger logger = LogManager.getLogger(EmployeeBatchProcessor.class);
    @Autowired
    private EmployeeDao dao;

    public EmployeeBatchProcessor(List<Employee> batch, EmployeeDao dao) {
        this.batch = batch;
        this.dao = dao;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        long startTime = System.nanoTime();
        System.out.println("Thread '" + threadName + "' started processing a batch of '" + batch.size() + "' records @ " + startTime);
        for (Employee employee : batch) {
            dao.commitEmployee(employee);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double seconds = (double) duration / 1_000_000_000.0;
        System.out.println("Thread " + threadName + " finished processing a batch of " + batch.size() + " records @ " + endTime);
        System.out.println("Thread " + threadName + " took " + seconds + " seconds.");
    }
}
