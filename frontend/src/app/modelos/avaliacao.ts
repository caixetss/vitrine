import { Pagina } from './pagina';

export interface Avaliacao {
  nomeAutor: string;
  nota: number;
  titulo: string;
  comentario: string;
  ehCompraVerificada: boolean;
  criadoEm: string;
}

export interface ResumoAvaliacoes {
  media: number;
  total: number;
  distribuicao: Record<string, number>;
}

export interface AvaliacoesDoProduto {
  resumo: ResumoAvaliacoes;
  avaliacoes: Pagina<Avaliacao>;
}
