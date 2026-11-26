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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "produto_imagem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProdutoImagem {

	public static final int POSICAO_DA_CAPA = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto")
	private Produto produto;

	private String url;

	private String textoAlternativo;

	private Integer posicaoGaleria;

	private Instant criadoEm;

	private Instant atualizadoEm;

}
