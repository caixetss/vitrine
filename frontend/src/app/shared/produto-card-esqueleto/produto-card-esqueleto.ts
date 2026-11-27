import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-produto-card-esqueleto',
  imports: [MatCardModule],
  template: `
    <mat-card appearance="outlined" class="card" aria-hidden="true">
      <div class="bloco capa"></div>
      <div class="informacoes">
        <div class="bloco linha"></div>
        <div class="bloco linha curta"></div>
        <div class="bloco linha preco"></div>
        <div class="bloco linha curta"></div>
      </div>
    </mat-card>
  `,
  styleUrl: './produto-card-esqueleto.scss',
})
export class ProdutoCardEsqueleto {}
