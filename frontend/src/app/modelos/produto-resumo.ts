export interface ProdutoResumo {
  id: string;
  slug: string;
  nome: string;
  imagemCapa: string | null;
  preco: number;
  precoOriginal: number | null;
  avaliacaoMedia: number;
  totalAvaliacoes: number;
  ehFreteGratis: boolean;
}
