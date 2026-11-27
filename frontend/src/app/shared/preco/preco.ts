import { CurrencyPipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { calculaParcelamento } from '../../util/regras-de-preco';

@Component({
  selector: 'app-preco',
  imports: [CurrencyPipe],
  templateUrl: './preco.html',
  styleUrl: './preco.scss',
})
export class Preco {
  readonly preco = input.required<number>();
  readonly precoOriginal = input<number | null>(null);

  protected readonly parcelamento = computed(() => calculaParcelamento(this.preco()));
}
