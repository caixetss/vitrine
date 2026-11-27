import { Component, ElementRef, input, linkedSignal, viewChild } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ImagemDoProduto } from '../../../modelos/produto-detalhe';

@Component({
  selector: 'app-galeria',
  imports: [MatIconModule],
  templateUrl: './galeria.html',
  styleUrl: './galeria.scss',
})
export class Galeria {
  readonly imagens = input.required<ImagemDoProduto[]>();

  private readonly trilhoDeImagens = viewChild<ElementRef<HTMLElement>>('trilho');

  protected readonly indiceDaImagemSelecionada = linkedSignal({ source: this.imagens, computation: () => 0 });

  protected aoDeslizar(): void {
    const trilho = this.trilhoDeImagens()?.nativeElement;
    if (trilho) {
      this.indiceDaImagemSelecionada.set(Math.round(trilho.scrollLeft / trilho.clientWidth));
    }
  }
}
