package io.github.caixetss.vitrine.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class SpaConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/static/")
				.resourceChain(true)
				.addResolver(new ResolvedorDeRotaDoAngular());
	}

	private static class ResolvedorDeRotaDoAngular extends PathResourceResolver {

		@Override
		protected Resource getResource(String caminho, Resource pasta) throws IOException {
			Resource arquivo = pasta.createRelative(caminho);
			if (arquivo.exists() && arquivo.isReadable()) {
				return arquivo;
			}
			if (ehRotaDoBackend(caminho) || caminho.contains(".")) {
				return null;
			}
			Resource paginaDoAngular = pasta.createRelative("index.html");
			return paginaDoAngular.exists() ? paginaDoAngular : null;
		}

		private static boolean ehRotaDoBackend(String caminho) {
			return caminho.startsWith("api/") || caminho.startsWith("actuator/") || caminho.startsWith("v3/");
		}

	}

}
