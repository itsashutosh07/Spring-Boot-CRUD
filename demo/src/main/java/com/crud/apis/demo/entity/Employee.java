package com.crud.apis.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Employee_Table")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName, lastName, email;

    public enum Gender {
        M, F
    }
    @Enumerated(EnumType.STRING)
    private Gender gender;

}

