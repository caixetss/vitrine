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
	public List<CategoriaDto> buscaListaCategoriaEmArvore() {
		List<Categoria> categoriasAtivas = categoriaRepository.buscaListaCategoriaAtiva();

		Map<UUID, List<CategoriaDto>> subcategoriasAgrupadasPorPai = categoriasAtivas.stream()
				.filter(categoria -> !categoria.ehPrincipal())
				.collect(Collectors.groupingBy(
						subcategoria -> subcategoria.getCategoriaPai().getId(),
						Collectors.mapping(subcategoria -> new CategoriaDto(subcategoria, List.of()), Collectors.toList())));

		return categoriasAtivas.stream()
				.filter(Categoria::ehPrincipal)
				.map(categoriaPrincipal -> new CategoriaDto(categoriaPrincipal, subcategoriasAgrupadasPorPai.getOrDefault(categoriaPrincipal.getId(), List.of())))
				.toList();
	}

}
