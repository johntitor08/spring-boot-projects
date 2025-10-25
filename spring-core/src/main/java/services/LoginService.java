package services;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.AppConfig;

public class LoginService {
	
	public void login() {
		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		UserService userService = context.getBean(UserService.class);
		
	}

}
