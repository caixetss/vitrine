package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.dto.CategoriaDto;
import io.github.caixetss.vitrine.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categorias", description = "Categorias da loja, usadas no menu e na página inicial")
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaService categoriaService;

	@Operation(
			summary = "Lista as categorias em árvore",
			description = "Devolve as categorias principais ativas, cada uma com suas subcategorias, na ordem de exibição do menu.")
	@GetMapping
	public List<CategoriaDto> buscaListaCategoriaEmArvore() {
		return categoriaService.buscaListaCategoriaEmArvore();
	}

}
