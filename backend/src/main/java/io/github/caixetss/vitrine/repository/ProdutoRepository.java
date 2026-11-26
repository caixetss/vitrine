package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.Produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID>, JpaSpecificationExecutor<Produto> {
}
