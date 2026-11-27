import { TestBed } from '@angular/core/testing';
import { CarrinhoStore, ItemDoCarrinho } from './carrinho.store';

function montaItemDeTeste(alteracoes: Partial<ItemDoCarrinho> = {}): ItemDoCarrinho {
  return {
    idVariacao: 'fone-preto',
    slugProduto: 'fone',
    nome: 'Fone',
    nomeCor: 'Preto',
    imagem: null,
    preco: 50,
    precoOriginal: 80,
    quantidade: 1,
    estoque: 3,
    ...alteracoes,
  };
}

describe('CarrinhoStore', () => {
  let carrinho: CarrinhoStore;

  beforeEach(() => {
    localStorage.clear();
    carrinho = TestBed.inject(CarrinhoStore);
  });

  it('deve somar a quantidade quando a mesma variação é adicionada de novo', () => {
    carrinho.adicionar(montaItemDeTeste());
    carrinho.adicionar(montaItemDeTeste());

    expect(carrinho.itens().length).toBe(1);
    expect(carrinho.quantidadeTotal()).toBe(2);
  });

  it('não deve passar do estoque ao adicionar nem ao alterar a quantidade', () => {
    carrinho.adicionar(montaItemDeTeste({ quantidade: 2 }));
    carrinho.adicionar(montaItemDeTeste({ quantidade: 2 }));
    expect(carrinho.quantidadeTotal()).toBe(3);

    carrinho.alterarQuantidade('fone-preto', 10);
    expect(carrinho.quantidadeTotal()).toBe(3);

    carrinho.alterarQuantidade('fone-preto', 0);
    expect(carrinho.quantidadeTotal()).toBe(1);
  });

  it('deve calcular subtotal, economia, Pix e o que falta para o frete grátis', () => {
    carrinho.adicionar(montaItemDeTeste());
    carrinho.adicionar(montaItemDeTeste({ idVariacao: 'capa', preco: 19.9, precoOriginal: null }));

    expect(carrinho.subtotal()).toBe(69.9);
    expect(carrinho.economia()).toBe(30);
    expect(carrinho.valorNoPix()).toBe(66.41);
    expect(carrinho.faltaParaFreteGratis()).toBe(9.1);
    expect(carrinho.ehFreteGratis()).toBe(false);
  });

  it('deve liberar o frete grátis quando o subtotal chega no mínimo', () => {
    carrinho.adicionar(montaItemDeTeste({ quantidade: 2 }));

    expect(carrinho.faltaParaFreteGratis()).toBe(0);
    expect(carrinho.ehFreteGratis()).toBe(true);
  });

  it('deve salvar no navegador e carregar de volta', () => {
    carrinho.adicionar(montaItemDeTeste());
    TestBed.tick();

    TestBed.resetTestingModule();
    expect(TestBed.inject(CarrinhoStore).quantidadeTotal()).toBe(1);
  });

  it('deve remover e limpar itens', () => {
    carrinho.adicionar(montaItemDeTeste());
    carrinho.adicionar(montaItemDeTeste({ idVariacao: 'capa' }));

    carrinho.remover('capa');
    expect(carrinho.itens().map((item) => item.idVariacao)).toEqual(['fone-preto']);

    carrinho.limpar();
    expect(carrinho.itens()).toEqual([]);
  });
});
