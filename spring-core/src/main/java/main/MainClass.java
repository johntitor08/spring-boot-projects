package main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import config.AppConfig;
import services.LoginService;
import services.UserService;

public class MainClass {

	public static void main(String[] args) {

		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

			// Test UserService
			UserService userService = context.getBean(UserService.class);
			System.out.println("=== User List ===");
			userService.getUserList().forEach(System.out::println);

			// Get LoginService from Spring context (FIXED)
			System.out.println("\n=== Login Test ===");
			LoginService loginService = context.getBean(LoginService.class);
			loginService.login();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
