package io.github.caixetss.vitrine.excecao;

import io.github.caixetss.vitrine.controller.CategoriaController;
import io.github.caixetss.vitrine.controller.ProdutoController;
import io.github.caixetss.vitrine.service.CategoriaService;
import io.github.caixetss.vitrine.service.ProdutoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CategoriaController.class, ProdutoController.class})
class TratadorGlobalExcecoesTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CategoriaService categoriaService;

	@MockitoBean
	private ProdutoService produtoService;

	@Test
	void recursoNaoEncontrado_devolve404ComAMensagemDaExcecao() throws Exception {
		when(produtoService.buscaIndividualProduto("nao-existe"))
				.thenThrow(new RecursoNaoEncontradoException("Produto não encontrado: nao-existe"));

		mockMvc.perform(get("/api/produtos/nao-existe"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.erro").value("Não encontrado"))
				.andExpect(jsonPath("$.mensagem").value("Produto não encontrado: nao-existe"))
				.andExpect(jsonPath("$.campos", empty()));
	}

	@Test
	void rotaInexistente_devolve404NoMesmoFormato() throws Exception {
		mockMvc.perform(get("/api/rota-que-nao-existe"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.campos", empty()));
	}

	@Test
	void validacaoDoFiltro_devolve400ComOCampoInvalido() throws Exception {
		mockMvc.perform(get("/api/produtos").param("precoMin", "500").param("precoMax", "100"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.erro").value("Requisição inválida"))
				.andExpect(jsonPath("$.mensagem").value("Parâmetros inválidos"))
				.andExpect(jsonPath("$.campos[0].campo").value("faixaDePrecoValida"))
				.andExpect(jsonPath("$.campos[0].mensagem").value("precoMin deve ser menor ou igual a precoMax"));
	}

	@Test
	void textoNoLugarDeNumeroNoFiltro_devolve400ComValorInvalido() throws Exception {
		mockMvc.perform(get("/api/produtos").param("precoMin", "abc"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos[0].campo").value("precoMin"))
				.andExpect(jsonPath("$.campos[0].mensagem").value("valor inválido"));
	}

	@Test
	void ordenacaoDesconhecida_devolve400ComOCampoOrdem() throws Exception {
		mockMvc.perform(get("/api/produtos").param("ordem", "qualquer"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos[0].campo").value("ordem"))
				.andExpect(jsonPath("$.campos[0].mensagem").value("valor inválido"));
	}

	@Test
	void tamanhoForaDoLimite_devolve400ComMensagemEmPortugues() throws Exception {
		mockMvc.perform(get("/api/produtos").param("tamanho", "61"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos[0].campo").value("tamanho"))
				.andExpect(jsonPath("$.campos[0].mensagem").value("deve ser entre 1 e 60"));
	}

	@Test
	void regraDeNegocio_devolve422() throws Exception {
		when(categoriaService.buscaListaCategoriaEmArvore()).thenThrow(new RegraNegocioException("Operação não permitida"));

		mockMvc.perform(get("/api/categorias"))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.mensagem").value("Operação não permitida"));
	}

	@Test
	void erroInesperado_devolve500SemExporDetalhesInternos() throws Exception {
		when(categoriaService.buscaListaCategoriaEmArvore()).thenThrow(new IllegalStateException("senha do banco: 123"));

		mockMvc.perform(get("/api/categorias"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value(500))
				.andExpect(jsonPath("$.erro").value("Erro interno"))
				.andExpect(jsonPath("$.mensagem").value("Ocorreu um erro inesperado. Tente novamente mais tarde."))
				.andExpect(content().string(not(containsString("senha"))))
				.andExpect(content().string(not(containsString("IllegalStateException"))));
	}

}
