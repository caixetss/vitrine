package io.github.caixetss.vitrine.config;

import io.github.caixetss.vitrine.dto.OrdemProduto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConversorOrdemProduto implements Converter<String, OrdemProduto> {

	@Override
	public OrdemProduto convert(String valorNaUrl) {
		return OrdemProduto.buscaPorValorNaUrl(valorNaUrl);
	}

}
