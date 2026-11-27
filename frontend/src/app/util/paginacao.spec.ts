import { calculaPaginasVisiveis } from './paginacao';

describe('calculaPaginasVisiveis', () => {
  it('deve mostrar todas as páginas quando são poucas', () => {
    expect(calculaPaginasVisiveis(1, 5)).toEqual([1, 2, 3, 4, 5]);
  });

  it('deve mostrar o começo e a última página quando está no início', () => {
    expect(calculaPaginasVisiveis(1, 26)).toEqual([1, 2, 3, 4, 'reticencias', 26]);
  });

  it('deve mostrar as vizinhas da página atual quando está no meio', () => {
    expect(calculaPaginasVisiveis(10, 26)).toEqual([1, 'reticencias', 9, 10, 11, 'reticencias', 26]);
  });

  it('deve mostrar o fim e a primeira página quando está no final', () => {
    expect(calculaPaginasVisiveis(26, 26)).toEqual([1, 'reticencias', 23, 24, 25, 26]);
  });
});
