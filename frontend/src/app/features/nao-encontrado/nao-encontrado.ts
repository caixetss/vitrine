import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-nao-encontrado',
  imports: [RouterLink, MatButtonModule, MatIconModule],
  template: `
    <section class="aviso">
      <mat-icon class="icone" aria-hidden="true">search_off</mat-icon>
      <h1>Página não encontrada</h1>
      <p>O endereço que você abriu não existe ou foi removido.</p>
      <a mat-flat-button routerLink="/">Voltar para a página inicial</a>
    </section>
  `,
  styles: `
    .aviso {
      padding: 48px 0;
      text-align: center;
      color: var(--mat-sys-on-surface-variant);
    }

    .icone {
      width: 48px;
      height: 48px;
      font-size: 48px;
      color: var(--mat-sys-primary);
    }

    h1 {
      color: var(--mat-sys-on-surface);
      font: var(--mat-sys-headline-small);
    }
  `,
})
export class NaoEncontrado {}
