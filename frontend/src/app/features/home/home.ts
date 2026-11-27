import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { CategoriaService } from '../../core/api/categoria.service';
import { ProdutoService } from '../../core/api/produto.service';
import { formataTempoAteFimDoDia } from '../../util/tempo-ate-fim-do-dia';
import { Destaques } from './destaques/destaques';
import { VitrineProdutos } from './vitrine-produtos/vitrine-produtos';

const PRODUTOS_POR_VITRINE = 5;

@Component({
  selector: 'app-home',
  imports: [RouterLink, MatButtonModule, MatIconModule, Destaques, VitrineProdutos],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private readonly produtoService = inject(ProdutoService);

  protected readonly categorias = inject(CategoriaService).categoriasEmArvore;
  protected readonly ofertasDoDia = this.produtoService.buscaListaProdutoDaVitrine('maior-desconto', PRODUTOS_POR_VITRINE);
  protected readonly maisVendidos = this.produtoService.buscaListaProdutoDaVitrine('mais-vendidos', PRODUTOS_POR_VITRINE);

  protected readonly beneficios = [
    { icone: 'local_shipping', titulo: 'Frete grátis acima de R$ 79', texto: 'Para todo o Brasil em itens selecionados' },
    { icone: 'credit_card', titulo: 'Até 10x sem juros', texto: 'Em todos os cartões de crédito' },
    { icone: 'bolt', titulo: '5% off no Pix', texto: 'Desconto aplicado no checkout' },
    { icone: 'autorenew', titulo: 'Troca grátis em 7 dias', texto: 'Devolução simples e sem custo' },
  ];

  private readonly agora = signal(new Date());
  protected readonly tempoAteFimDasOfertas = computed(() => formataTempoAteFimDoDia(this.agora()));
  protected readonly ehEmailInscrito = signal(false);

  constructor() {
    const relogio = setInterval(() => this.agora.set(new Date()), 1000);
    inject(DestroyRef).onDestroy(() => clearInterval(relogio));
  }

  protected aoInscreverNaNewsletter(evento: SubmitEvent): void {
    evento.preventDefault();
    this.ehEmailInscrito.set(true);
  }
}
