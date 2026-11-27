import { Component, inject, output, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-cabecalho',
  imports: [ReactiveFormsModule, RouterLink, MatBadgeModule, MatButtonModule, MatIconModule, MatTooltipModule],
  templateUrl: './cabecalho.html',
  styleUrl: './cabecalho.scss',
})
export class Cabecalho {
  private readonly router = inject(Router);

  readonly aoAbrirMenu = output<void>();

  protected readonly quantidadeNoCarrinho = signal(0);
  protected readonly campoBusca = new FormControl('', { nonNullable: true });

  protected aoBuscar(evento: SubmitEvent): void {
    evento.preventDefault();
    const termo = this.campoBusca.value.trim();
    if (termo.length >= 2) {
      this.router.navigate(['/busca'], { queryParams: { q: termo } });
    }
  }
}
