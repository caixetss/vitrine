package io.github.caixetss.vitrine.service;

import io.github.caixetss.vitrine.dto.CategoriaDto;
import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;

	@Transactional(readOnly = true)
	public List<CategoriaDto> buscaListaCategoria() {
		List<Categoria> categoriasAtivas = categoriaRepository.findByEhInativoFalseOrderByOrdemExibicao();

		Map<UUID, List<CategoriaDto>> subcategoriasPorIdPai = categoriasAtivas.stream()
				.filter(categoria -> !categoria.ehPrincipal())
				.collect(Collectors.groupingBy(
						categoria -> categoria.getCategoriaPai().getId(),
						Collectors.mapping(subcategoria -> new CategoriaDto(subcategoria, List.of()), Collectors.toList())));

		return categoriasAtivas.stream()
				.filter(Categoria::ehPrincipal)
				.map(principal -> new CategoriaDto(principal, subcategoriasPorIdPai.getOrDefault(principal.getId(), List.of())))
				.toList();
	}

}
