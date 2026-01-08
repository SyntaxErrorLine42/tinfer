package hr.fer.tinfer.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("SUPABASE_URL", dotenv.get("SUPABASE_URL"));
		System.setProperty("SUPABASE_JWT_SECRET", dotenv.get("SUPABASE_JWT_SECRET"));
		System.setProperty("SUPABASE_ANON_KEY", dotenv.get("SUPABASE_ANON_KEY"));
		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
		System.setProperty("DATABASE_USERNAME", dotenv.get("DATABASE_USERNAME"));
		System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));
		System.setProperty("SMTP_MAIL", dotenv.get("SMTP_MAIL"));
		System.setProperty("SMTP_PASSWORD", dotenv.get("SMTP_PASSWORD"));
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
