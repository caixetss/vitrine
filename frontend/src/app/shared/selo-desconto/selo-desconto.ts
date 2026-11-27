import { Component, computed, input } from '@angular/core';
import { calculaPercentualDeDesconto } from '../../util/regras-de-preco';

@Component({
  selector: 'app-selo-desconto',
  template: `
    @if (percentual(); as percentual) {
      <span class="selo" [attr.aria-label]="percentual + '% de desconto'">-{{ percentual }}%</span>
    }
  `,
  styleUrl: './selo-desconto.scss',
})
export class SeloDesconto {
  readonly preco = input.required<number>();
  readonly precoOriginal = input<number | null>(null);

  protected readonly percentual = computed(() => calculaPercentualDeDesconto(this.preco(), this.precoOriginal()));
}
