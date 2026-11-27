import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, input, linkedSignal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ProdutoService } from '../../../core/api/produto.service';
import { Avaliacao, ResumoAvaliacoes } from '../../../modelos/avaliacao';

const NOTAS_DA_MAIOR_PARA_A_MENOR = [5, 4, 3, 2, 1];

@Component({
  selector: 'app-avaliacoes-produto',
  imports: [DatePipe, DecimalPipe, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: './avaliacoes-produto.html',
  styleUrl: './avaliacoes-produto.scss',
})
export class AvaliacoesProduto {
  readonly slug = input.required<string>();

  protected readonly paginaDeAvaliacoes = linkedSignal({ source: this.slug, computation: () => 0 });
  protected readonly respostaDasAvaliacoes = inject(ProdutoService).buscaListaAvaliacao(() => ({ slug: this.slug(), pagina: this.paginaDeAvaliacoes() }));

  protected readonly avaliacoesCarregadas = linkedSignal<Avaliacao[], Avaliacao[]>({
    source: () => (this.respostaDasAvaliacoes.hasValue() ? this.respostaDasAvaliacoes.value().avaliacoes.itens : []),
    computation: (novas, anterior) => (this.paginaDeAvaliacoes() === 0 || !anterior ? novas : [...anterior.value, ...novas]),
  });

  protected readonly resumo = linkedSignal<ResumoAvaliacoes | null, ResumoAvaliacoes | null>({
    source: () => (this.respostaDasAvaliacoes.hasValue() ? this.respostaDasAvaliacoes.value().resumo : null),
    computation: (novo, anterior) => novo ?? anterior?.value ?? null,
  });
  protected readonly distribuicao = computed(() => {
    const resumo = this.resumo();
    if (!resumo) return [];
    return NOTAS_DA_MAIOR_PARA_A_MENOR.map((nota) => {
      const quantidade = resumo.distribuicao[nota] ?? 0;
      return { nota, percentual: resumo.total > 0 ? Math.round((quantidade / resumo.total) * 100) : 0 };
    });
  });
  protected readonly temMaisAvaliacoes = computed(
    () => this.respostaDasAvaliacoes.hasValue() && this.paginaDeAvaliacoes() + 1 < this.respostaDasAvaliacoes.value().avaliacoes.totalPaginas,
  );
  protected readonly posicoesDasEstrelas = [1, 2, 3, 4, 5];
}
