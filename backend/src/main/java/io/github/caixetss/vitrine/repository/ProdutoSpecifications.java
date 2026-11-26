package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.dto.OrdemProduto;
import io.github.caixetss.vitrine.dto.ProdutoFiltro;
import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.entidade.Produto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProdutoSpecifications {

	private static final String LETRAS_COM_ACENTO = "áàâãäéèêëíìîïóòôõöúùûüç";
	private static final String LETRAS_SEM_ACENTO = "aaaaaeeeeiiiiooooouuuuc";
	private static final char CARACTERE_DE_ESCAPE = '\\';

	private ProdutoSpecifications() {
	}

	public static Specification<Produto> combinaFiltrosEscolhidos(ProdutoFiltro filtro) {
		List<Specification<Produto>> condicoes = new ArrayList<>();
		condicoes.add(filtraProdutosAtivos());
		if (filtro.categoria() != null) {
			condicoes.add(filtraPorCategoria(filtro.categoria()));
		}
		if (filtro.busca() != null) {
			condicoes.add(filtraPorBusca(filtro.busca()));
		}
		if (filtro.precoMin() != null || filtro.precoMax() != null) {
			condicoes.add(filtraPorFaixaDePreco(filtro.precoMin(), filtro.precoMax()));
		}
		if (filtro.avaliacaoMin() != null) {
			condicoes.add(filtraPorAvaliacaoMinima(filtro.avaliacaoMin()));
		}
		if (filtro.marcas() != null && !filtro.marcas().isEmpty()) {
			condicoes.add(filtraPorMarcas(filtro.marcas()));
		}
		if (Boolean.TRUE.equals(filtro.freteGratis())) {
			condicoes.add(filtraPorFreteGratis());
		}
		return Specification.allOf(condicoes);
	}

	public static Specification<Produto> filtraProdutosAtivos() {
		return (produto, consulta, cb) -> cb.isFalse(produto.get("ehInativo"));
	}

	public static Specification<Produto> filtraPorCategoria(String slugCategoria) {
		return (produto, consulta, cb) -> {
			Join<Produto, Categoria> categoria = produto.join("categoria");
			Join<Categoria, Categoria> categoriaPai = categoria.join("categoriaPai", JoinType.LEFT);
			return cb.or(
					cb.equal(categoria.get("slug"), slugCategoria),
					cb.equal(categoriaPai.get("slug"), slugCategoria));
		};
	}

	public static Specification<Produto> filtraPorBusca(String termo) {
		return (produto, consulta, cb) -> cb.like(
				removeAcentosEMaiusculas(cb, produto.get("nome")),
				removeAcentosEMaiusculas(cb, cb.literal("%" + trataCaracteresEspeciaisDaBusca(termo) + "%")),
				CARACTERE_DE_ESCAPE);
	}

	public static Specification<Produto> filtraPorFaixaDePreco(BigDecimal precoMin, BigDecimal precoMax) {
		return (produto, consulta, cb) -> {
			if (precoMin == null) {
				return cb.lessThanOrEqualTo(produto.get("preco"), precoMax);
			}
			if (precoMax == null) {
				return cb.greaterThanOrEqualTo(produto.get("preco"), precoMin);
			}
			return cb.between(produto.get("preco"), precoMin, precoMax);
		};
	}

	public static Specification<Produto> filtraPorAvaliacaoMinima(int notaMinima) {
		return (produto, consulta, cb) -> cb.greaterThanOrEqualTo(produto.get("avaliacaoMedia"), BigDecimal.valueOf(notaMinima));
	}

	public static Specification<Produto> filtraPorMarcas(List<String> slugsMarca) {
		return (produto, consulta, cb) -> produto.join("marca").get("slug").in(slugsMarca);
	}

	public static Specification<Produto> filtraPorFreteGratis() {
		return (produto, consulta, cb) -> cb.isTrue(produto.get("ehFreteGratis"));
	}

	public static Specification<Produto> ordenaPor(OrdemProduto ordem) {
		return (produto, consulta, cb) -> {
			if (!Long.class.equals(consulta.getResultType())) {
				List<Order> ordenacao = new ArrayList<>(montaCriteriosDeOrdenacao(ordem, produto, cb));
				ordenacao.add(cb.asc(produto.get("nome")));
				consulta.orderBy(ordenacao);
			}
			return cb.conjunction();
		};
	}

	private static List<Order> montaCriteriosDeOrdenacao(OrdemProduto ordem, Root<Produto> produto, CriteriaBuilder cb) {
		return switch (ordem) {
			case RELEVANCIA, MAIS_VENDIDOS -> List.of(cb.desc(produto.get("totalVendidos")));
			case MENOR_PRECO -> List.of(cb.asc(produto.get("preco")));
			case MAIOR_PRECO -> List.of(cb.desc(produto.get("preco")));
			case AVALIACAO -> List.of(cb.desc(produto.get("avaliacaoMedia")), cb.desc(produto.get("totalAvaliacoes")));
			case MAIOR_DESCONTO -> List.of(cb.desc(calculaPercentualDeDesconto(produto, cb)));
		};
	}

	private static Expression<Number> calculaPercentualDeDesconto(Root<Produto> produto, CriteriaBuilder cb) {
		Expression<BigDecimal> preco = produto.get("preco");
		Expression<BigDecimal> precoOriginal = produto.get("precoOriginal");
		return cb.coalesce(cb.quot(cb.diff(precoOriginal, preco), precoOriginal), (Number) BigDecimal.ZERO);
	}

	private static Expression<String> removeAcentosEMaiusculas(CriteriaBuilder cb, Expression<String> texto) {
		return cb.function("translate", String.class, cb.lower(texto), cb.literal(LETRAS_COM_ACENTO), cb.literal(LETRAS_SEM_ACENTO));
	}

	private static String trataCaracteresEspeciaisDaBusca(String termo) {
		String escape = String.valueOf(CARACTERE_DE_ESCAPE);
		return termo
				.replace(escape, escape + escape)
				.replace("%", escape + "%")
				.replace("_", escape + "_");
	}

}
