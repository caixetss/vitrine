package io.github.caixetss.vitrine.entidade;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "produto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String nome;

	private String slug;

	private String descricao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_marca")
	private Marca marca;

	private BigDecimal preco;

	private BigDecimal precoOriginal;

	private Boolean ehFreteGratis;

	private BigDecimal avaliacaoMedia;

	private Integer totalAvaliacoes;

	private Integer totalVendidos;

	private Boolean ehInativo;

	private Instant criadoEm;

	private Instant atualizadoEm;

}
