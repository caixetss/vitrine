import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-em-construcao',
  imports: [MatIconModule],
  template: `
    <section class="aviso">
      <mat-icon class="icone" aria-hidden="true">construction</mat-icon>
      <h1>{{ titulo() }}</h1>
      <p>Esta página está em desenvolvimento.</p>
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
export class EmConstrucao {
  readonly titulo = input.required<string>();
}
