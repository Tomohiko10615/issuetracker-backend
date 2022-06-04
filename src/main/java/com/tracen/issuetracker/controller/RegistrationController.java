package com.tracen.issuetracker.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tracen.issuetracker.entity.User;
import com.tracen.issuetracker.entity.VerificationToken;
import com.tracen.issuetracker.event.RegistrationCompleteEvent;
import com.tracen.issuetracker.model.PasswordModel;
import com.tracen.issuetracker.model.UserModel;
import com.tracen.issuetracker.service.UserService;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ApplicationEventPublisher publisher;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
		if (!userModel.getPassword().equals(userModel.getMatchingPassword())) {
			return ResponseEntity.badRequest().build();
		}
		User user = userService.registerUser(userModel);
		String url = applicationUrl(request);
		publisher.publishEvent(new RegistrationCompleteEvent(user, url));
		return ResponseEntity.ok("Success");
	}
	
	@GetMapping("/verifyRegistration")
	public String verifyRegistration(@RequestParam("token") String token) {
		return userService.validateVerificationToken(token);
	}
	
	@GetMapping("/resendVerificationToken")
	public String resendVerificationToken(
			@RequestParam("token") String oldToken,
			HttpServletRequest request) {
		VerificationToken newVerificationToken = userService.generateNewToken(oldToken);
		sendTokenEmail(
				newVerificationToken.getToken(),
				applicationUrl(request),
				"verifyRegistration"
				);
		return "Success";
	}
	
	@PostMapping("/resetPassword")
	public String resetPassword(
			@RequestParam("email") String email,
			HttpServletRequest request) {
		User user = userService.findUserByEmail(email);
		if (user != null) {
			String token = UUID.randomUUID().toString();
			userService.createPasswordResetToken(user, token);
			sendTokenEmail(token, applicationUrl(request), "setNewPassword");
			return "Success";
		}
		return "Invalid user";
	}
	
	@PostMapping("/setNewPassword")
	public String setNewPassword(
			@RequestParam("token") String passwordResetToken,
			@RequestBody PasswordModel passwordModel
			) {
		String result = userService.validateVerificationToken(passwordResetToken);
		if (result == "valid") {
			if (passwordModel.getNewPassword().equals(passwordModel.getNewMatchingPassword())) {
				userService.setNewPassword(passwordResetToken,
						passwordModel.getNewPassword());
				return "Success";
			}
			return "No matching passwords";
		}
		return result;
	}

	private void sendTokenEmail(String token, String applicationUrl, String action) {
		String url = applicationUrl +
				"/" + action + "?token=" +
				token;
		
		// Send email
		log.info(url);
	}

	private String applicationUrl(HttpServletRequest request) {
		return "http://" +
				request.getServerName() +
				":" +
				request.getServerPort() +
				request.getContextPath();
		
	}
	
}
