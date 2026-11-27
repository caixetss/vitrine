import { HttpParams, httpResource } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { FiltroProduto, FiltrosDisponiveis, OrdemProduto } from '../../modelos/filtro-produto';
import { Pagina } from '../../modelos/pagina';
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
