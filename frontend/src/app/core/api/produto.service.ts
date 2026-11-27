import { HttpParams, httpResource } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { FiltroProduto, FiltrosDisponiveis, OrdemProduto } from '../../modelos/filtro-produto';
import { AvaliacoesDoProduto } from '../../modelos/avaliacao';
import { Pagina } from '../../modelos/pagina';
import { ProdutoDetalhe } from '../../modelos/produto-detalhe';
import { ProdutoResumo } from '../../modelos/produto-resumo';

@Injectable({ providedIn: 'root' })
export class ProdutoService {
  buscaListaProduto(parametrosDaBusca: () => { filtro: FiltroProduto; ordem: OrdemProduto; pagina: number }) {
    return httpResource<Pagina<ProdutoResumo>>(() => {
      const { filtro, ordem, pagina } = parametrosDaBusca();
      return {
        url: `${environment.apiUrl}/produtos`,
        params: montaParametrosDaApi(filtro).set('ordem', ordem).set('pagina', pagina),
      };
    });
  }

  buscaListaProdutoDaVitrine(ordem: OrdemProduto, tamanho: number) {
    return httpResource<Pagina<ProdutoResumo>>(() => ({ url: `${environment.apiUrl}/produtos`, params: { ordem, tamanho } }));
  }

  buscaIndividualProduto(slug: () => string) {
    return httpResource<ProdutoDetalhe>(() => `${environment.apiUrl}/produtos/${slug()}`);
  }

  buscaListaAvaliacao(parametrosDaBusca: () => { slug: string; pagina: number }) {
    return httpResource<AvaliacoesDoProduto>(() => {
      const { slug, pagina } = parametrosDaBusca();
      return { url: `${environment.apiUrl}/produtos/${slug}/avaliacoes`, params: { pagina } };
    });
  }

  buscaListaRelacionado(slug: () => string) {
    return httpResource<ProdutoResumo[]>(() => `${environment.apiUrl}/produtos/${slug()}/relacionados`, { defaultValue: [] });
  }

  buscaFiltrosDisponiveis(filtro: () => FiltroProduto) {
    return httpResource<FiltrosDisponiveis>(() => ({
      url: `${environment.apiUrl}/produtos/filtros`,
      params: montaParametrosDaApi(filtro()),
    }));
  }
}

function montaParametrosDaApi(filtro: FiltroProduto): HttpParams {
  let parametros = new HttpParams();
  if (filtro.categoria) parametros = parametros.set('categoria', filtro.categoria);
  if (filtro.busca) parametros = parametros.set('busca', filtro.busca);
  if (filtro.precoMin !== null) parametros = parametros.set('precoMin', filtro.precoMin);
  if (filtro.precoMax !== null) parametros = parametros.set('precoMax', filtro.precoMax);
  if (filtro.avaliacaoMin !== null) parametros = parametros.set('avaliacaoMin', filtro.avaliacaoMin);
  if (filtro.marcas.length > 0) parametros = parametros.set('marcas', filtro.marcas.join(','));
  if (filtro.freteGratis) parametros = parametros.set('freteGratis', true);
  return parametros;
}
