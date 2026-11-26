package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.dto.CategoriaDto;
import io.github.caixetss.vitrine.service.CategoriaService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaService categoriaService;

	@GetMapping
	public List<CategoriaDto> buscaListaCategoria() {
		return categoriaService.buscaListaCategoria();
	}

}
