package io.github.caixetss.vitrine.entidade;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "marca")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Marca {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String nome;

	private String slug;

	private Boolean ehInativo;

	private Instant criadoEm;

	private Instant atualizadoEm;

}
