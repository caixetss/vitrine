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
@Table(name = "categoria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String nome;

	private String slug;

	private String icone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria_pai")
	private Categoria categoriaPai;

	private Integer ordemExibicao;

	private Boolean ehInativo;

	private Instant criadoEm;

	private Instant atualizadoEm;

	public boolean ehPrincipal() {
		return categoriaPai == null;
	}

}
