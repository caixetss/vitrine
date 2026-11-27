import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { ProdutoResumo } from '../../../modelos/produto-resumo';
import { ProdutoCard } from '../../../shared/produto-card/produto-card';
import { ProdutoCardEsqueleto } from '../../../shared/produto-card-esqueleto/produto-card-esqueleto';

const QUANTIDADE_DE_ESQUELETOS = 5;

@Component({
  selector: 'app-vitrine-produtos',
  imports: [RouterLink, MatButtonModule, ProdutoCard, ProdutoCardEsqueleto],
  templateUrl: './vitrine-produtos.html',
  styleUrl: './vitrine-produtos.scss',
})
export class VitrineProdutos {
  readonly titulo = input.required<string>();
  readonly ordemDoLinkVerTodos = input.required<string>();
  readonly produtos = input<ProdutoResumo[]>([]);
  readonly ehCarregando = input(false);
  readonly temErro = input(false);

  protected readonly esqueletos = Array.from({ length: QUANTIDADE_DE_ESQUELETOS }, (_, indice) => indice);
}
