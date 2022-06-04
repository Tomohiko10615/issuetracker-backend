package com.tracen.issuetracker.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String role;
	private Boolean isActive = false;
}
