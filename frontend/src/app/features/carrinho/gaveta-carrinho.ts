import { CurrencyPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RouterLink } from '@angular/router';
import { CarrinhoStore } from '../../core/carrinho/carrinho.store';
import { VALOR_MINIMO_PARA_FRETE_GRATIS } from '../../util/regras-de-preco';

@Component({
  selector: 'app-gaveta-carrinho',
  imports: [CurrencyPipe, RouterLink, MatButtonModule, MatDividerModule, MatIconModule, MatProgressBarModule],
  templateUrl: './gaveta-carrinho.html',
  styleUrl: './gaveta-carrinho.scss',
})
export class GavetaCarrinho {
  protected readonly carrinho = inject(CarrinhoStore);

  protected readonly progressoDoFreteGratis = computed(() =>
    Math.min(100, Math.round((this.carrinho.subtotal() / VALOR_MINIMO_PARA_FRETE_GRATIS) * 100)),
  );
}
