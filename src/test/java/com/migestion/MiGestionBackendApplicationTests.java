package com.migestion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci1jb250ZXh0LWxvYWRzLXRlc3QtdXNlLW9ubHk=",
		"stripe.api-key=sk_test_mock",
		"stripe.webhook-secret=whsec_mock"
})
class MiGestionBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
