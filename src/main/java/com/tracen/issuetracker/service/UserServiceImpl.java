package com.tracen.issuetracker.service;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tracen.issuetracker.entity.User;
import com.tracen.issuetracker.entity.VerificationToken;
import com.tracen.issuetracker.model.UserModel;
import com.tracen.issuetracker.repository.UserRepository;
import com.tracen.issuetracker.repository.VerificationTokenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User registerUser(UserModel userModel) {
		log.info("Inside registerUser");
		log.info(userModel.toString());
		User user = new User();
		user.setEmail(userModel.getEmail());
		user.setFirstName(userModel.getFirstName());
		user.setLastName(userModel.getLastName());
		user.setPassword(passwordEncoder.encode(userModel.getPassword()));
		user.setRole("USER");
		userRepository.save(user);
		return user;
	}

	@Override
	public void saveVerificationToken(User user, String token) {
		VerificationToken verificationToken = new VerificationToken(user, token);
		verificationTokenRepository.save(verificationToken);
	}

	@Override
	public String validateVerificationToken(String token) {
		VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

		if (verificationToken == null)
			return "invalid";

		User user = verificationToken.getUser();

		Calendar calendar = Calendar.getInstance();

		if (verificationToken.getExpirarionDate().getTime() - calendar.getTime().getTime() <= 0)
			return "expired";
	
		user.setIsActive(true);
		userRepository.save(user);

		return "valid";
	}

	@Override
	public VerificationToken generateNewToken(String oldToken) {
		VerificationToken oldVerificationToken = verificationTokenRepository.findByToken(oldToken);
		VerificationToken newVerificationToken = new VerificationToken(oldVerificationToken.getUser(), UUID.randomUUID().toString());
		verificationTokenRepository.save(newVerificationToken);
		verificationTokenRepository.delete(oldVerificationToken);
		return newVerificationToken;
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public VerificationToken createPasswordResetToken(User user, String token) {
		VerificationToken passwordResetToken = new VerificationToken(user, token);
		verificationTokenRepository.save(passwordResetToken);
		return passwordResetToken;
	}

	@Override
	public void setNewPassword(String token, String newPassword) {
		log.info(token);
		VerificationToken resetPasswordToken = verificationTokenRepository.findByToken(token);
		User user = resetPasswordToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		verificationTokenRepository.delete(resetPasswordToken);
		userRepository.save(user);
	}
}
