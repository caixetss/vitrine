import { computed, effect, Injectable, signal } from '@angular/core';
import { calculaParcelamento, calculaValorNoPix, VALOR_MINIMO_PARA_FRETE_GRATIS } from '../../util/regras-de-preco';

const CHAVE_DO_CARRINHO_NO_NAVEGADOR = 'vitrine.carrinho';

export interface ItemDoCarrinho {
  idVariacao: string;
  slugProduto: string;
  nome: string;
  nomeCor: string;
  imagem: string | null;
  preco: number;
  precoOriginal: number | null;
  quantidade: number;
  estoque: number;
}

@Injectable({ providedIn: 'root' })
export class CarrinhoStore {
  readonly itens = signal<ItemDoCarrinho[]>(carregaCarrinhoDoNavegador());
  readonly ehGavetaAberta = signal(false);

  readonly quantidadeTotal = computed(() => this.itens().reduce((soma, item) => soma + item.quantidade, 0));
  readonly subtotal = computed(() => arredondaParaCentavos(this.itens().reduce((soma, item) => soma + item.preco * item.quantidade, 0)));
  readonly economia = computed(() =>
    arredondaParaCentavos(
      this.itens().reduce((soma, item) => soma + Math.max(0, (item.precoOriginal ?? item.preco) - item.preco) * item.quantidade, 0),
    ),
  );
  readonly total = this.subtotal;
  readonly valorNoPix = computed(() => calculaValorNoPix(this.total()));
  readonly parcelamento = computed(() => calculaParcelamento(this.total()));
  readonly faltaParaFreteGratis = computed(() => arredondaParaCentavos(Math.max(0, VALOR_MINIMO_PARA_FRETE_GRATIS - this.subtotal())));
  readonly ehFreteGratis = computed(() => this.itens().length > 0 && this.faltaParaFreteGratis() === 0);

  constructor() {
    effect(() => salvaCarrinhoNoNavegador(this.itens()));
  }

  adicionar(novoItem: ItemDoCarrinho): void {
    this.itens.update((itens) => {
      const itemJaNoCarrinho = itens.find((item) => item.idVariacao === novoItem.idVariacao);
      if (!itemJaNoCarrinho) {
        return [...itens, { ...novoItem, quantidade: Math.min(novoItem.quantidade, novoItem.estoque) }];
      }
      return itens.map((item) =>
        item === itemJaNoCarrinho ? { ...item, estoque: novoItem.estoque, quantidade: Math.min(item.quantidade + novoItem.quantidade, novoItem.estoque) } : item,
      );
    });
  }

  alterarQuantidade(idVariacao: string, quantidade: number): void {
    this.itens.update((itens) =>
      itens.map((item) => (item.idVariacao === idVariacao ? { ...item, quantidade: Math.min(Math.max(1, quantidade), item.estoque) } : item)),
    );
  }

  remover(idVariacao: string): void {
    this.itens.update((itens) => itens.filter((item) => item.idVariacao !== idVariacao));
  }

  limpar(): void {
    this.itens.set([]);
  }
}

function arredondaParaCentavos(valor: number): number {
  return Math.round(valor * 100) / 100;
}

function carregaCarrinhoDoNavegador(): ItemDoCarrinho[] {
  try {
    const salvo = JSON.parse(localStorage.getItem(CHAVE_DO_CARRINHO_NO_NAVEGADOR) ?? '[]');
    return Array.isArray(salvo) ? salvo : [];
  } catch {
    return [];
  }
}

function salvaCarrinhoNoNavegador(itens: ItemDoCarrinho[]): void {
  try {
    localStorage.setItem(CHAVE_DO_CARRINHO_NO_NAVEGADOR, JSON.stringify(itens));
  } catch {
    return;
  }
}
