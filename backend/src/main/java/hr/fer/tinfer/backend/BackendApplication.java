package hr.fer.tinfer.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendApplication {

	public static void main(String[] args) {
		// Load .env file only in development (when file exists)
		// In production (Render), environment variables are set directly
		try {
			Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // Don't fail if .env doesn't exist
				.load();
			
			// Set system properties from .env if they're not already set
			setPropertyIfNotExists("SUPABASE_URL", dotenv.get("SUPABASE_URL"));
			setPropertyIfNotExists("SUPABASE_JWT_SECRET", dotenv.get("SUPABASE_JWT_SECRET"));
			setPropertyIfNotExists("SUPABASE_ANON_KEY", dotenv.get("SUPABASE_ANON_KEY"));
			setPropertyIfNotExists("DATABASE_URL", dotenv.get("DATABASE_URL"));
			setPropertyIfNotExists("DATABASE_USERNAME", dotenv.get("DATABASE_USERNAME"));
			setPropertyIfNotExists("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));
			setPropertyIfNotExists("SMTP_MAIL", dotenv.get("SMTP_MAIL"));
			setPropertyIfNotExists("SMTP_PASSWORD", dotenv.get("SMTP_PASSWORD"));
		} catch (Exception e) {
			// In production, .env won't exist - this is expected
			System.out.println("No .env file found, using environment variables from system");
		}
		
		SpringApplication.run(BackendApplication.class, args);
	}
	
	private static void setPropertyIfNotExists(String key, String value) {
		if (System.getProperty(key) == null && value != null) {
			System.setProperty(key, value);
		}
	}

}
