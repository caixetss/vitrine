import { calculaParcelamento, calculaPercentualDeDesconto, calculaValorNoPix } from './regras-de-preco';

describe('regras-de-preco', () => {
  describe('calculaParcelamento', () => {
    it('deve parcelar em 2x quando o preço não passa de R$ 79', () => {
      expect(calculaParcelamento(59.9)).toEqual({ quantidade: 2, valorDaParcela: 29.95 });
    });

    it('deve parcelar em 10x quando o preço passa de R$ 79', () => {
      expect(calculaParcelamento(279.9)).toEqual({ quantidade: 10, valorDaParcela: 27.99 });
    });

    it('deve arredondar a parcela para baixo para não cobrar a mais', () => {
      expect(calculaParcelamento(89.95).valorDaParcela).toBe(8.99);
    });
  });

  describe('calculaPercentualDeDesconto', () => {
    it('não deve ter desconto quando não há preço original', () => {
      expect(calculaPercentualDeDesconto(100, null)).toBeNull();
    });

    it('não deve ter desconto quando o preço original não é maior que o preço', () => {
      expect(calculaPercentualDeDesconto(100, 100)).toBeNull();
    });

    it('deve calcular o percentual arredondado', () => {
      expect(calculaPercentualDeDesconto(279.9, 449.9)).toBe(38);
    });
  });

  describe('calculaValorNoPix', () => {
    it('deve aplicar 5% de desconto', () => {
      expect(calculaValorNoPix(1599)).toBe(1519.05);
    });

    it('deve arredondar meio centavo para cima, como na conta feita à mão', () => {
      expect(calculaValorNoPix(59.9)).toBe(56.91);
      expect(calculaValorNoPix(279.9)).toBe(265.91);
    });
  });
});
