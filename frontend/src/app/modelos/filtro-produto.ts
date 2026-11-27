export type OrdemProduto = 'relevancia' | 'menor-preco' | 'maior-preco' | 'avaliacao' | 'mais-vendidos' | 'maior-desconto';

export interface FiltroProduto {
  categoria: string | null;
  busca: string | null;
  precoMin: number | null;
  precoMax: number | null;
  avaliacaoMin: number | null;
  marcas: string[];
  freteGratis: boolean;
}

export interface OpcaoFiltro {
  slug: string;
  nome: string;
  total: number;
}

export interface FiltrosDisponiveis {
  subcategorias: OpcaoFiltro[];
  marcas: OpcaoFiltro[];
  precoMinimo: number | null;
  precoMaximo: number | null;
  totalFreteGratis: number;
}

export interface GrupoDeCategoria {
  slug: string;
  nome: string;
  total: number;
  subcategorias: OpcaoFiltro[];
}
