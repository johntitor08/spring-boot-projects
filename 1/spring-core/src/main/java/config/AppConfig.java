package config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import entity.User;
import services.UserService;

@Configuration
public class AppConfig {

    @Bean
    UserService userService() {
		
		UserService userService = new UserService();
		List<User> userList = new ArrayList<>();
		userList.add(new User("a"));
		userList.add(new User("a"));
		userService.setUserList(userList);
		return userService;
		
	}
	
}
