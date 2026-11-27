package io.github.caixetss.vitrine._integration;

import io.github.caixetss.vitrine.TestcontainersConfiguration;
import io.github.caixetss.vitrine._integration.helpers.HelpersConfiguration;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, HelpersConfiguration.class})
public abstract class AbstractMvcIntegrationTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	protected Statistics zeraContagemDeConsultas() {
		Statistics estatisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		estatisticas.clear();
		return estatisticas;
	}

}
