import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ProdutoResumo } from '../../modelos/produto-resumo';
import { AvaliacaoEstrelas } from '../avaliacao-estrelas/avaliacao-estrelas';
import { Preco } from '../preco/preco';
import { SeloDesconto } from '../selo-desconto/selo-desconto';

@Component({
  selector: 'app-produto-card',
  imports: [RouterLink, MatButtonModule, MatCardModule, MatIconModule, AvaliacaoEstrelas, Preco, SeloDesconto],
  templateUrl: './produto-card.html',
  styleUrl: './produto-card.scss',
})
export class ProdutoCard {
  readonly produto = input.required<ProdutoResumo>();
}
