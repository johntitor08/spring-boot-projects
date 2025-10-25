package services;

import java.util.List;
import entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class UserService {

	private List<User> userList;

	public void authenticate() {
		System.out.println("Authentication logic executed!");
		// Add your actual authentication logic here
		// For example: check username/password against userList

	}
}
