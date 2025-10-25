package services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class LoginService {

	private final UserService userService;

	@Autowired
	public LoginService(UserService userService) {
		this.userService = userService;
	}

	public void login() {
		userService.authenticate();
	}

}
