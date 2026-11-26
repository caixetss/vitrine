package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

	@Query("SELECT c FROM Categoria c WHERE c.ehInativo = false ORDER BY c.ordemExibicao")
	List<Categoria> buscaListaCategoriaAtiva();

}
