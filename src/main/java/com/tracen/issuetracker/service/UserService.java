package com.tracen.issuetracker.service;

import com.tracen.issuetracker.entity.User;
import com.tracen.issuetracker.entity.VerificationToken;
import com.tracen.issuetracker.model.UserModel;

public interface UserService {
	
	User registerUser(UserModel userModel);

	void saveVerificationToken(User user, String token);

	String validateVerificationToken(String token);

	VerificationToken generateNewToken(String oldToken);

	User findUserByEmail(String email);

	VerificationToken createPasswordResetToken(User user, String token);

	void setNewPassword(String token, String newPassword);

}
