package config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import entity.User;
import services.UserService;

@Configuration
@ComponentScan(basePackages = { "services", "entity" })
public class AppConfig {

	@Bean
	public UserService userService() {
		UserService userService = new UserService();
		List<User> userList = new ArrayList<>();

		userList.add(new User("John"));
		userList.add(new User("Jane"));
		userList.add(new User("Bob"));

		userService.setUserList(userList);
		return userService;
	}

}
