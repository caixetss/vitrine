import { Component, ElementRef, inject, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CategoriaService } from '../../core/api/categoria.service';
import { Cabecalho } from '../cabecalho/cabecalho';
import { MenuCategorias } from '../menu-categorias/menu-categorias';
import { Rodape } from '../rodape/rodape';

@Component({
  selector: 'app-layout-principal',
  imports: [RouterOutlet, RouterLink, MatButtonModule, MatIconModule, MatListModule, MatSidenavModule, Cabecalho, MenuCategorias, Rodape],
  templateUrl: './layout-principal.html',
  styleUrl: './layout-principal.scss',
})
export class LayoutPrincipal {
  protected readonly categorias = inject(CategoriaService).categoriasEmArvore;

  private readonly conteudo = viewChild.required<ElementRef<HTMLElement>>('conteudo');

  protected aoPularParaConteudo(evento: Event): void {
    evento.preventDefault();
    this.conteudo().nativeElement.focus();
  }
}
