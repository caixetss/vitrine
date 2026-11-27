import { CurrencyPipe, DecimalPipe, NgTemplateOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, linkedSignal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterLink } from '@angular/router';
import { ProdutoService } from '../../core/api/produto.service';
import { CategoriaDoProduto, Variacao } from '../../modelos/produto-detalhe';
import { AvaliacaoEstrelas } from '../../shared/avaliacao-estrelas/avaliacao-estrelas';
import { Preco } from '../../shared/preco/preco';
import { ProdutoCard } from '../../shared/produto-card/produto-card';
import { SeloDesconto } from '../../shared/selo-desconto/selo-desconto';
import { calculaValorNoPix, DESCONTO_NO_PIX } from '../../util/regras-de-preco';
import { NaoEncontrado } from '../nao-encontrado/nao-encontrado';
import { AvaliacoesProduto } from './avaliacoes-produto/avaliacoes-produto';
import { Galeria } from './galeria/galeria';

@Component({
  selector: 'app-produto',
  imports: [
    CurrencyPipe,
    DecimalPipe,
    NgTemplateOutlet,
    RouterLink,
    MatButtonModule,
    MatExpansionModule,
    MatIconModule,
    MatTabsModule,
    AvaliacaoEstrelas,
    Preco,
    ProdutoCard,
    SeloDesconto,
    NaoEncontrado,
    AvaliacoesProduto,
    Galeria,
  ],
  templateUrl: './produto.html',
  styleUrl: './produto.scss',
})
export class Produto {
  private readonly produtoService = inject(ProdutoService);

  readonly slug = input.required<string>();

  protected readonly produto = this.produtoService.buscaIndividualProduto(() => this.slug());
  protected readonly relacionados = this.produtoService.buscaListaRelacionado(() => this.slug());

  protected readonly percentualDescontoNoPix = DESCONTO_NO_PIX * 100;
  protected readonly ehNaoEncontrado = computed(() => (this.produto.error() as HttpErrorResponse | undefined)?.status === 404);
  protected readonly caminhoDaCategoria = computed(() => (this.produto.hasValue() ? montaCaminhoDaCategoria(this.produto.value().categoria) : []));
  protected readonly valorNoPix = computed(() => (this.produto.hasValue() ? calculaValorNoPix(this.produto.value().preco) : 0));

  protected readonly variacaoSelecionada = linkedSignal<Variacao | null>(() => {
    const variacoes = this.produto.hasValue() ? this.produto.value().variacoes : [];
    return variacoes.find((variacao) => variacao.estoque > 0) ?? variacoes[0] ?? null;
  });
  protected readonly estoqueDisponivel = computed(() => this.variacaoSelecionada()?.estoque ?? 0);
  protected readonly ehIndisponivel = computed(() => this.estoqueDisponivel() === 0);
  protected readonly quantidade = linkedSignal({ source: this.variacaoSelecionada, computation: () => 1 });

  protected aoDiminuirQuantidade(): void {
    this.quantidade.update((quantidade) => Math.max(1, quantidade - 1));
  }

  protected aoAumentarQuantidade(): void {
    this.quantidade.update((quantidade) => Math.min(this.estoqueDisponivel(), quantidade + 1));
  }
}

function montaCaminhoDaCategoria(categoria: CategoriaDoProduto): CategoriaDoProduto[] {
  return categoria.categoriaPai ? [...montaCaminhoDaCategoria(categoria.categoriaPai), categoria] : [categoria];
}
