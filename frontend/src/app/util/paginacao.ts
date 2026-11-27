export type ItemPaginacao = number | 'reticencias';

export function calculaPaginasVisiveis(paginaAtual: number, totalPaginas: number): ItemPaginacao[] {
  if (totalPaginas <= 7) {
    return Array.from({ length: totalPaginas }, (_, indice) => indice + 1);
  }
  const vizinhas = [paginaAtual - 1, paginaAtual, paginaAtual + 1].filter((pagina) => pagina > 1 && pagina < totalPaginas);
  const paginas = [1, ...vizinhas, totalPaginas];
  if (paginaAtual <= 3) paginas.splice(1, vizinhas.length, 2, 3, 4);
  if (paginaAtual >= totalPaginas - 2) paginas.splice(1, vizinhas.length, totalPaginas - 3, totalPaginas - 2, totalPaginas - 1);

  return paginas.flatMap((pagina, indice): ItemPaginacao[] =>
    indice > 0 && pagina - paginas[indice - 1] > 1 ? ['reticencias', pagina] : [pagina],
  );
}
