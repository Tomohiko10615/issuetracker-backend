package com.tracen.issuetracker.listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.tracen.issuetracker.entity.User;
import com.tracen.issuetracker.event.RegistrationCompleteEvent;
import com.tracen.issuetracker.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

	@Autowired
	private UserService userService;
	
	@Override
	public void onApplicationEvent(RegistrationCompleteEvent event) {
		
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.saveVerificationToken(user, token);
		
		String url = event.getApplicationUrl() +
				"/verifyRegistration?token=" +
				token;
		
		// Send email
		log.info(url);
	}

}
