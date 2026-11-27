export const PARCELAS_SEM_JUROS = 10;
export const VALOR_MINIMO_PARA_PARCELAMENTO_MAXIMO = 79;
export const PARCELAS_ABAIXO_DO_VALOR_MINIMO = 2;
export const DESCONTO_NO_PIX = 0.05;

export interface Parcelamento {
  quantidade: number;
  valorDaParcela: number;
}

export function calculaParcelamento(preco: number): Parcelamento {
  const quantidade = preco > VALOR_MINIMO_PARA_PARCELAMENTO_MAXIMO ? PARCELAS_SEM_JUROS : PARCELAS_ABAIXO_DO_VALOR_MINIMO;
  return { quantidade, valorDaParcela: Math.floor((preco / quantidade) * 100) / 100 };
}

export function calculaPercentualDeDesconto(preco: number, precoOriginal: number | null): number | null {
  if (precoOriginal === null || precoOriginal <= preco) {
    return null;
  }
  return Math.round(((precoOriginal - preco) / precoOriginal) * 100);
}

export function calculaValorNoPix(preco: number): number {
  return Math.round(preco * (1 - DESCONTO_NO_PIX) * 100) / 100;
}
