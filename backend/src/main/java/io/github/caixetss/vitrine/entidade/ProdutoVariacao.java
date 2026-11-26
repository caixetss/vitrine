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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "produto_variacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProdutoVariacao {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto")
	private Produto produto;

	private String nomeCor;

	@JdbcTypeCode(SqlTypes.CHAR)
	private String corHex;

	private String codigoEstoque;

	private Integer estoque;

	private Boolean ehInativo;

	private Instant criadoEm;

	private Instant atualizadoEm;

}
