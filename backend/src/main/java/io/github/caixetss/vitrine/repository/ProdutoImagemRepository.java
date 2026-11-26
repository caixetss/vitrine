package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.ProdutoImagem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, UUID> {

	@Query("SELECT i FROM ProdutoImagem i WHERE i.produto.id IN :idsProduto AND i.posicaoGaleria = " + ProdutoImagem.POSICAO_DA_CAPA)
	List<ProdutoImagem> buscaListaCapaDosProdutos(Collection<UUID> idsProduto);

}
