package io.github.caixetss.vitrine.config;

import io.github.caixetss.vitrine.dto.ErroDto;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	private static final String NOME_DO_ESQUEMA_DE_ERRO = "ErroDto";

	@Bean
	public OpenAPI documentacaoDaApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Vitrine API")
						.description("API do e-commerce Vitrine: catálogo, listagem com filtros, página de produto e avaliações. "
								+ "Todo erro volta no formato ErroDto.")
						.version("v1")
						.contact(new Contact().name("Repositório da Vitrine").url("https://github.com/caixetss/vitrine")));
	}

	@Bean
	public OpenApiCustomizer adicionaRespostasDeErroEmTodosOsEndpoints() {
		return documentacao -> {
			documentacao.getComponents().getSchemas().putAll(ModelConverters.getInstance().readAll(ErroDto.class));
			documentacao.getPaths().forEach((caminho, endpoint) ->
				endpoint.readOperations().forEach(operacao -> {
					operacao.getResponses().addApiResponse("400", montaRespostaDeErro("Parâmetro inválido"));
					if (caminho.contains("{slug}")) {
						operacao.getResponses().addApiResponse("404", montaRespostaDeErro("Produto não encontrado ou inativo"));
					}
					operacao.getResponses().addApiResponse("500", montaRespostaDeErro("Erro inesperado"));
				}));
		};
	}

	private ApiResponse montaRespostaDeErro(String descricao) {
		Schema<?> esquemaDoErro = new Schema<>().$ref("#/components/schemas/" + NOME_DO_ESQUEMA_DE_ERRO);
		return new ApiResponse()
				.description(descricao)
				.content(new Content().addMediaType("application/json", new MediaType().schema(esquemaDoErro)));
	}

}
