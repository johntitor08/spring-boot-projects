package main;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.AppConfig;
import entity.User;
import services.LoginService;
import services.UserService;

public class MainClass {
	
	public static void main(String[] args) {
		
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
			UserService userService = context.getBean(UserService.class);
			
			for (User user : userService.getUserList()) {
				
				System.out.println(user);
				
			}
			
		} catch (BeansException e) {

			e.printStackTrace();
			
		}
		
		LoginService loginService = new LoginService();
		loginService.login();
		
	}

}
