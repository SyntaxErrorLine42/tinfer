package hr.fer.tinfer.backend;

import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.model.User;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import hr.fer.tinfer.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

@DataJpaTest
class ProfileRepositoryTest {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void testFindProfileByEmail() {
		// given: create a user first
		User user = new User();
		user.setId(UUID.randomUUID());
		user.setEmail("john@fer.hr");
		userRepository.save(user);

		// and then create a profile referencing that user
		Profile profile = new Profile();
		profile.setId(user.getId()); // FK = same UUID
		profile.setEmail("john@fer.hr");
		profile.setFirstName("John");
		profile.setLastName("Doe");
		profileRepository.save(profile);

		// when: we fetch by email
		var found = profileRepository.findByEmail("john@fer.hr");

		// then: verify it works
		assertNotNull(found);
		assertEquals("John", found.getFirstName());
		assertEquals("Doe", found.getLastName());
	}
}





