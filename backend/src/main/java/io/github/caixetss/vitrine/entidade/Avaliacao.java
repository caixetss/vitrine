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
@Table(name = "avaliacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Avaliacao {

	public static final int NOTA_MINIMA = 1;
	public static final int NOTA_MAXIMA = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto")
	private Produto produto;

	private String nomeAutor;

	private Integer nota;

	private String titulo;

	private String comentario;

	private Boolean ehCompraVerificada;

	private Instant criadoEm;

	private Instant atualizadoEm;

}
