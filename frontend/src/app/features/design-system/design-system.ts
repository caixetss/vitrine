import { Component, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AvaliacaoEstrelas } from '../../shared/avaliacao-estrelas/avaliacao-estrelas';
import { TemaStore } from '../../core/tema/tema.store';
import { ProdutoResumo } from '../../modelos/produto-resumo';
import { Preco } from '../../shared/preco/preco';
import { ProdutoCard } from '../../shared/produto-card/produto-card';
import { ProdutoCardEsqueleto } from '../../shared/produto-card-esqueleto/produto-card-esqueleto';
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
    ProdutoCard,
    ProdutoCardEsqueleto,
    SeloDesconto,
  ],
  templateUrl: './design-system.html',
  styleUrl: './design-system.scss',
})
export class DesignSystem {
  protected readonly tema = inject(TemaStore);
  protected readonly ehCarregando = signal(false);
  protected readonly filtrosAtivos = signal(['Até R$ 500', '4 estrelas ou mais', 'Frete grátis']);
  protected readonly produtosDeExemplo: ProdutoResumo[] = [
    {
      id: '1',
      slug: 'fone-sonora-pulse-anc',
      nome: 'Fone de Ouvido Bluetooth Sonora Pulse ANC com Cancelamento de Ruído',
      imagemCapa: 'https://placehold.co/800x800/EEF2FF/4F46E5?text=Sonora',
      preco: 279.9,
      precoOriginal: 449.9,
      avaliacaoMedia: 4.7,
      totalAvaliacoes: 2384,
      ehFreteGratis: true,
    },
    {
      id: '2',
      slug: 'fone-sonora-basic-p2',
      nome: 'Fone com Fio Sonora Basic P2 com Microfone',
      imagemCapa: null,
      preco: 39.9,
      precoOriginal: null,
      avaliacaoMedia: 4.3,
      totalAvaliacoes: 6881,
      ehFreteGratis: false,
    },
  ];
  protected readonly campoBusca = new FormControl('', [Validators.required, Validators.minLength(2)]);

  protected aoSimularCarregamento(): void {
    this.ehCarregando.set(true);
    setTimeout(() => this.ehCarregando.set(false), 2000);
  }

  protected aoRemoverFiltro(filtro: string): void {
    this.filtrosAtivos.update((filtros) => filtros.filter((item) => item !== filtro));
  }
}
