import { DecimalPipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-avaliacao-estrelas',
  imports: [DecimalPipe, MatIconModule],
  template: `
    <span class="avaliacao" [attr.aria-label]="'Nota ' + (nota() | number: '1.1-1') + ' de 5, ' + total() + ' avaliações'">
      <mat-icon class="estrela" aria-hidden="true">star</mat-icon>
      <strong>{{ nota() | number: '1.1-1' }}</strong>
      <span class="total">({{ total() | number }})</span>
    </span>
  `,
  styleUrl: './avaliacao-estrelas.scss',
})
export class AvaliacaoEstrelas {
  readonly nota = input.required<number>();
  readonly total = input.required<number>();
}
