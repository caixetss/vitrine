export interface CategoriaDoProduto {
  nome: string;
  slug: string;
  categoriaPai: CategoriaDoProduto | null;
}

export interface ImagemDoProduto {
  url: string;
  textoAlternativo: string;
  posicaoGaleria: number;
}

export interface Variacao {
  id: string;
  nomeCor: string;
  corHex: string;
  codigoEstoque: string;
  estoque: number;
}

export interface Especificacao {
  nome: string;
  valor: string;
}

export interface ProdutoDetalhe {
  id: string;
  slug: string;
  nome: string;
  descricao: string;
  preco: number;
  precoOriginal: number | null;
  avaliacaoMedia: number;
  totalAvaliacoes: number;
  totalVendidos: number;
  ehFreteGratis: boolean;
  marca: { nome: string; slug: string };
  categoria: CategoriaDoProduto;
  imagens: ImagemDoProduto[];
  variacoes: Variacao[];
  especificacoes: Especificacao[];
}
