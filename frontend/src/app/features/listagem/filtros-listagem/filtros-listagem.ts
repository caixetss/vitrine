import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { RouterLink } from '@angular/router';
import { FiltroProduto, FiltrosDisponiveis } from '../../../modelos/filtro-produto';

const QUANTIDADE_DE_MARCAS_VISIVEIS = 5;

interface FaixaDePreco {
  rotulo: string;
  precoMin: number | null;
  precoMax: number | null;
}

@Component({
  selector: 'app-filtros-listagem',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCheckboxModule,
    MatDividerModule,
    MatIconModule,
    MatRadioModule,
    MatSlideToggleModule,
  ],
  templateUrl: './filtros-listagem.html',
  styleUrl: './filtros-listagem.scss',
})
export class FiltrosListagem {
  readonly filtro = input.required<FiltroProduto>();
  readonly filtrosDisponiveis = input<FiltrosDisponiveis | undefined>();
  readonly aoMudarFiltro = output<Partial<FiltroProduto>>();
  readonly aoLimparFiltros = output<void>();

  protected readonly faixasDePreco: FaixaDePreco[] = [
    { rotulo: 'Até R$ 100', precoMin: null, precoMax: 100 },
    { rotulo: 'R$ 100 a R$ 300', precoMin: 100, precoMax: 300 },
    { rotulo: 'Até R$ 500', precoMin: null, precoMax: 500 },
    { rotulo: 'Acima de R$ 500', precoMin: 500, precoMax: null },
  ];
  protected readonly notasMinimas = [4, 3];

  protected readonly campoPrecoMin = new FormControl<number | null>(null);
  protected readonly campoPrecoMax = new FormControl<number | null>(null);
  protected readonly ehMostrandoTodasAsMarcas = signal(false);

  protected readonly marcasVisiveis = computed(() => {
    const marcas = this.filtrosDisponiveis()?.marcas ?? [];
    return this.ehMostrandoTodasAsMarcas() ? marcas : marcas.slice(0, QUANTIDADE_DE_MARCAS_VISIVEIS);
  });
  protected readonly temMarcasEscondidas = computed(
    () => !this.ehMostrandoTodasAsMarcas() && (this.filtrosDisponiveis()?.marcas.length ?? 0) > QUANTIDADE_DE_MARCAS_VISIVEIS,
  );

  constructor() {
    effect(() => {
      this.campoPrecoMin.setValue(this.filtro().precoMin);
      this.campoPrecoMax.setValue(this.filtro().precoMax);
    });
  }

  protected ehFaixaSelecionada(faixa: FaixaDePreco): boolean {
    return faixa.precoMin === this.filtro().precoMin && faixa.precoMax === this.filtro().precoMax;
  }

  protected aoAplicarPreco(evento: SubmitEvent): void {
    evento.preventDefault();
    const minimo = this.campoPrecoMin.value;
    const maximo = this.campoPrecoMax.value;
    const ehFaixaInvertida = minimo !== null && maximo !== null && minimo > maximo;
    this.aoMudarFiltro.emit(ehFaixaInvertida ? { precoMin: maximo, precoMax: minimo } : { precoMin: minimo, precoMax: maximo });
  }

  protected aoEscolherFaixa(faixa: FaixaDePreco): void {
    this.aoMudarFiltro.emit({ precoMin: faixa.precoMin, precoMax: faixa.precoMax });
  }

  protected aoMarcarMarca(slug: string, ehMarcada: boolean): void {
    const marcas = this.filtro().marcas.filter((marca) => marca !== slug);
    this.aoMudarFiltro.emit({ marcas: ehMarcada ? [...marcas, slug] : marcas });
  }
}
