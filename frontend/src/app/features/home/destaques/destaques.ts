import { Component, DestroyRef, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

const MILISSEGUNDOS_POR_SLIDE = 6000;

interface SlideDoBanner {
  selo: string;
  titulo: string;
  texto: string;
  link: string;
  queryParams: Record<string, string>;
  icones: string[];
}

@Component({
  selector: 'app-destaques',
  imports: [RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './destaques.html',
  styleUrl: './destaques.scss',
})
export class Destaques {
  protected readonly slides: SlideDoBanner[] = [
    {
      selo: 'Ofertas da semana',
      titulo: 'Tecnologia com até 40% off',
      texto: 'Celulares, fones e smartwatches com frete grátis e parcelamento em até 10x sem juros.',
      link: '/busca',
      queryParams: { ordem: 'maior-desconto' },
      icones: ['smartphone', 'headphones', 'watch'],
    },
    {
      selo: 'Novidade',
      titulo: 'Seu home office completo',
      texto: 'Notebooks, monitores e periféricos para trabalhar com conforto.',
      link: '/categoria/informatica',
      queryParams: {},
      icones: ['laptop', 'desktop_windows', 'keyboard'],
    },
    {
      selo: 'Mais vendidos',
      titulo: 'Os favoritos dos clientes',
      texto: 'Os produtos mais comprados da semana, com entrega para todo o Brasil.',
      link: '/busca',
      queryParams: { ordem: 'mais-vendidos' },
      icones: ['star', 'local_shipping', 'favorite'],
    },
  ];

  protected readonly indiceDoSlideAtual = signal(0);

  constructor() {
    if (matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    const intervaloDaTrocaDeSlide = setInterval(
      () => this.indiceDoSlideAtual.update((indice) => (indice + 1) % this.slides.length),
      MILISSEGUNDOS_POR_SLIDE,
    );
    inject(DestroyRef).onDestroy(() => clearInterval(intervaloDaTrocaDeSlide));
  }
}
