package com.redit.clone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;//so that we can access the method in the static way

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy =IDENTITY)//used by DB to rank as per DB
    private Long userId;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "password is required")
    private String password;
    @Email
    @NotBlank(message = "Email is required")
    private String email;
    private Instant created;
    private boolean enabled;
}
