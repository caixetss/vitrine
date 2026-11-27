import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CategoriaService } from '../../core/api/categoria.service';

@Component({
  selector: 'app-menu-categorias',
  imports: [RouterLink, RouterLinkActive, MatButtonModule, MatIconModule, MatMenuModule],
  templateUrl: './menu-categorias.html',
  styleUrl: './menu-categorias.scss',
})
export class MenuCategorias {
  protected readonly categorias = inject(CategoriaService).categoriasEmArvore;
}
