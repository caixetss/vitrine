import { Component, inject, output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { CarrinhoStore } from '../../core/carrinho/carrinho.store';

@Component({
  selector: 'app-cabecalho',
  imports: [ReactiveFormsModule, RouterLink, MatBadgeModule, MatButtonModule, MatIconModule],
  templateUrl: './cabecalho.html',
  styleUrl: './cabecalho.scss',
})
export class Cabecalho {
  private readonly router = inject(Router);

  readonly aoAbrirMenu = output<void>();

  protected readonly carrinho = inject(CarrinhoStore);
  protected readonly campoBusca = new FormControl('', { nonNullable: true });

  protected aoBuscar(evento: SubmitEvent): void {
    evento.preventDefault();
    const termo = this.campoBusca.value.trim();
    if (termo.length >= 2) {
      this.router.navigate(['/busca'], { queryParams: { q: termo } });
    }
  }
}
