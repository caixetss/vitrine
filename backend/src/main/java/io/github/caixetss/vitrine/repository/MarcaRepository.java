package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.Marca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MarcaRepository extends JpaRepository<Marca, UUID> {
}
