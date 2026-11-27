import { Component, computed, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { calculaPaginasVisiveis } from '../../../util/paginacao';

@Component({
  selector: 'app-paginacao',
  imports: [MatButtonModule, MatIconModule],
  templateUrl: './paginacao.html',
  styleUrl: './paginacao.scss',
})
export class Paginacao {
  readonly paginaAtual = input.required<number>();
  readonly totalPaginas = input.required<number>();
  readonly aoMudarPagina = output<number>();

  protected readonly paginas = computed(() => calculaPaginasVisiveis(this.paginaAtual(), this.totalPaginas()));
}
