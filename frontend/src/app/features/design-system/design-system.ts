import { Component, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AvaliacaoEstrelas } from '../../shared/avaliacao-estrelas/avaliacao-estrelas';
import { Preco } from '../../shared/preco/preco';
import { SeloDesconto } from '../../shared/selo-desconto/selo-desconto';

@Component({
  selector: 'app-design-system',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    AvaliacaoEstrelas,
    Preco,
    SeloDesconto,
  ],
  templateUrl: './design-system.html',
  styleUrl: './design-system.scss',
})
export class DesignSystem {
  protected readonly ehTemaEscuro = signal(false);
  protected readonly ehCarregando = signal(false);
  protected readonly filtrosAtivos = signal(['Até R$ 500', '4 estrelas ou mais', 'Frete grátis']);
  protected readonly campoBusca = new FormControl('', [Validators.required, Validators.minLength(2)]);

  protected aoAlternarTema(): void {
    this.ehTemaEscuro.update((ehEscuro) => !ehEscuro);
    document.documentElement.dataset['tema'] = this.ehTemaEscuro() ? 'escuro' : 'claro';
  }

  protected aoSimularCarregamento(): void {
    this.ehCarregando.set(true);
    setTimeout(() => this.ehCarregando.set(false), 2000);
  }

  protected aoRemoverFiltro(filtro: string): void {
    this.filtrosAtivos.update((filtros) => filtros.filter((item) => item !== filtro));
  }
}
