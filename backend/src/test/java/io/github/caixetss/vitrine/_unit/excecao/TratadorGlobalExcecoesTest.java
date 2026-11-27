package io.github.caixetss.vitrine._unit.excecao;

import io.github.caixetss.vitrine.dto.ErroDto;
import io.github.caixetss.vitrine.excecao.RecursoNaoEncontradoException;
import io.github.caixetss.vitrine.excecao.RegraNegocioException;
import io.github.caixetss.vitrine.excecao.TratadorGlobalExcecoes;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Automatizados Unitários | TratadorGlobalExcecoes")
class TratadorGlobalExcecoesTest {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private TratadorGlobalExcecoes tratadorGlobalExcecoes;

	@BeforeEach
	void setUp() {
		this.tratadorGlobalExcecoes = new TratadorGlobalExcecoes();
	}

	@Nested
	@DisplayName("trataRecursoNaoEncontrado")
	class TrataRecursoNaoEncontrado {

		@Test
		@DisplayName("Deve devolver 404 com a mensagem da exceção e sem campos")
		void deveDevolver404ComAMensagemDaExcecaoESemCampos() {
			String mensagem = FAKER.lorem().sentence();

			ResponseEntity<ErroDto> resposta = tratadorGlobalExcecoes.trataRecursoNaoEncontrado(new RecursoNaoEncontradoException(mensagem));

			assertThat(resposta.getStatusCode().value()).isEqualTo(404);
			assertThat(resposta.getBody()).isEqualTo(new ErroDto(404, "Não encontrado", mensagem, List.of()));
		}

	}

	@Nested
	@DisplayName("trataRegraNegocio")
	class TrataRegraNegocio {

		@Test
		@DisplayName("Deve devolver 422 com a mensagem da exceção")
		void deveDevolver422ComAMensagemDaExcecao() {
			String mensagem = FAKER.lorem().sentence();

			ResponseEntity<ErroDto> resposta = tratadorGlobalExcecoes.trataRegraNegocio(new RegraNegocioException(mensagem));

			assertThat(resposta.getStatusCode().value()).isEqualTo(422);
			assertThat(resposta.getBody().erro()).isEqualTo("Regra de negócio violada");
			assertThat(resposta.getBody().mensagem()).isEqualTo(mensagem);
		}

	}

	@Nested
	@DisplayName("trataErroInesperado")
	class TrataErroInesperado {

		@Test
		@DisplayName("Não deve expor a mensagem nem o tipo da exceção original")
		void naoDeveExporAMensagemNemOTipoDaExcecaoOriginal() {
			String segredoInterno = "senha do banco: " + FAKER.number().digits(6);

			ResponseEntity<ErroDto> resposta = tratadorGlobalExcecoes.trataErroInesperado(new IllegalStateException(segredoInterno));

			assertThat(resposta.getBody().mensagem()).doesNotContain(segredoInterno).doesNotContain("IllegalStateException");
		}

		@Test
		@DisplayName("Deve devolver 500 com mensagem genérica")
		void deveDevolver500ComMensagemGenerica() {
			ResponseEntity<ErroDto> resposta = tratadorGlobalExcecoes.trataErroInesperado(new RuntimeException());

			assertThat(resposta.getStatusCode().value()).isEqualTo(500);
			assertThat(resposta.getBody().erro()).isEqualTo("Erro interno");
			assertThat(resposta.getBody().mensagem()).isEqualTo("Ocorreu um erro inesperado. Tente novamente mais tarde.");
		}

	}

}
