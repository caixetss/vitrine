import { Component, computed, inject, input, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { CategoriaService } from '../../core/api/categoria.service';
import { ProdutoService } from '../../core/api/produto.service';
import { Categoria } from '../../modelos/categoria';
import { FiltroProduto, OrdemProduto } from '../../modelos/filtro-produto';
import { ProdutoCard } from '../../shared/produto-card/produto-card';
import { ProdutoCardEsqueleto } from '../../shared/produto-card-esqueleto/produto-card-esqueleto';
import { FiltrosListagem } from './filtros-listagem/filtros-listagem';
import { Paginacao } from './paginacao/paginacao';

const QUANTIDADE_DE_ESQUELETOS = 8;

interface FiltroAtivo {
  rotulo: string;
  filtroSemEsteItem: Partial<FiltroProduto>;
}

@Component({
  selector: 'app-listagem',
  imports: [
    RouterLink,
    MatButtonModule,
    MatButtonToggleModule,
    MatChipsModule,
    MatIconModule,
    MatSelectModule,
    ProdutoCard,
    ProdutoCardEsqueleto,
    FiltrosListagem,
    Paginacao,
  ],
  templateUrl: './listagem.html',
  styleUrl: './listagem.scss',
})
export class Listagem {
  private readonly router = inject(Router);
  private readonly parametrosDaUrl = toSignal(inject(ActivatedRoute).queryParamMap, { requireSync: true });
  private readonly categorias = inject(CategoriaService).categoriasEmArvore;

  readonly slug = input<string>();

  protected readonly opcoesDeOrdem: { valor: OrdemProduto; rotulo: string }[] = [
    { valor: 'relevancia', rotulo: 'Mais relevantes' },
    { valor: 'mais-vendidos', rotulo: 'Mais vendidos' },
    { valor: 'menor-preco', rotulo: 'Menor preço' },
    { valor: 'maior-preco', rotulo: 'Maior preço' },
    { valor: 'avaliacao', rotulo: 'Melhor avaliados' },
    { valor: 'maior-desconto', rotulo: 'Maior desconto' },
  ];
  protected readonly esqueletos = Array.from({ length: QUANTIDADE_DE_ESQUELETOS }, (_, indice) => indice);
  protected readonly modoDeExibicao = signal<'grade' | 'lista'>('grade');
  protected readonly ehFiltrosAbertos = signal(false);

  protected readonly filtro = computed<FiltroProduto>(() => {
    const parametros = this.parametrosDaUrl();
    return {
      categoria: this.slug() ?? null,
      busca: parametros.get('q'),
      precoMin: converteEmNumero(parametros.get('precoMin')),
      precoMax: converteEmNumero(parametros.get('precoMax')),
      avaliacaoMin: converteEmNumero(parametros.get('avaliacaoMin')),
      marcas: parametros.get('marcas')?.split(',').filter(Boolean) ?? [],
      freteGratis: parametros.get('freteGratis') === 'true',
    };
  });
  protected readonly ordem = computed(() => (this.parametrosDaUrl().get('ordem') as OrdemProduto | null) ?? 'relevancia');
  protected readonly pagina = computed(() => Math.max(1, converteEmNumero(this.parametrosDaUrl().get('pagina')) ?? 1));

  protected readonly produtos = inject(ProdutoService).buscaListaProduto(() => ({
    filtro: this.filtro(),
    ordem: this.ordem(),
    pagina: this.pagina() - 1,
  }));
  protected readonly filtrosDisponiveis = inject(ProdutoService).buscaFiltrosDisponiveis(() => this.filtro());

  protected readonly caminhoDaCategoria = computed(() => encontraCaminhoDaCategoria(this.categorias.hasValue() ? this.categorias.value() : [], this.slug()));
  protected readonly titulo = computed(() => {
    const busca = this.filtro().busca;
    if (busca) return `Resultados para "${busca}"`;
    return this.caminhoDaCategoria().at(-1)?.nome ?? (this.slug() ? '' : 'Todos os produtos');
  });

  protected readonly filtrosAtivos = computed<FiltroAtivo[]>(() => {
    const filtro = this.filtro();
    const marcasDisponiveis = this.filtrosDisponiveis.hasValue() ? this.filtrosDisponiveis.value().marcas : [];
    const nomesDasMarcas = new Map(marcasDisponiveis.map((marca) => [marca.slug, marca.nome]));
    const ativos: FiltroAtivo[] = [];
    if (filtro.precoMin !== null || filtro.precoMax !== null) {
      ativos.push({ rotulo: descreveFaixaDePreco(filtro.precoMin, filtro.precoMax), filtroSemEsteItem: { precoMin: null, precoMax: null } });
    }
    if (filtro.avaliacaoMin !== null) {
      ativos.push({ rotulo: `${filtro.avaliacaoMin} estrelas ou mais`, filtroSemEsteItem: { avaliacaoMin: null } });
    }
    for (const marca of filtro.marcas) {
      ativos.push({ rotulo: nomesDasMarcas.get(marca) ?? marca, filtroSemEsteItem: { marcas: filtro.marcas.filter((outra) => outra !== marca) } });
    }
    if (filtro.freteGratis) {
      ativos.push({ rotulo: 'Frete grátis', filtroSemEsteItem: { freteGratis: false } });
    }
    return ativos;
  });

  protected aoMudarFiltro(mudanca: Partial<FiltroProduto>): void {
    this.atualizaUrl({ ...converteEmParametrosDaUrl(mudanca), pagina: null });
  }

  protected aoLimparFiltros(): void {
    this.atualizaUrl({ precoMin: null, precoMax: null, avaliacaoMin: null, marcas: null, freteGratis: null, pagina: null });
  }

  protected aoMudarOrdem(ordem: OrdemProduto): void {
    this.atualizaUrl({ ordem: ordem === 'relevancia' ? null : ordem, pagina: null });
  }

  protected aoMudarPagina(pagina: number): void {
    this.atualizaUrl({ pagina: pagina === 1 ? null : pagina });
    document.getElementById('conteudo')?.scrollIntoView();
  }

  private atualizaUrl(parametros: Params): void {
    this.router.navigate([], { queryParams: parametros, queryParamsHandling: 'merge' });
  }
}

function converteEmNumero(valor: string | null): number | null {
  if (valor === null || valor.trim() === '') return null;
  const numero = Number(valor);
  return Number.isFinite(numero) ? numero : null;
}

function converteEmParametrosDaUrl(mudanca: Partial<FiltroProduto>): Params {
  const parametros: Params = {};
  if ('precoMin' in mudanca) parametros['precoMin'] = mudanca.precoMin;
  if ('precoMax' in mudanca) parametros['precoMax'] = mudanca.precoMax;
  if ('avaliacaoMin' in mudanca) parametros['avaliacaoMin'] = mudanca.avaliacaoMin;
  if ('marcas' in mudanca) parametros['marcas'] = mudanca.marcas?.length ? mudanca.marcas.join(',') : null;
  if ('freteGratis' in mudanca) parametros['freteGratis'] = mudanca.freteGratis || null;
  return parametros;
}

function descreveFaixaDePreco(minimo: number | null, maximo: number | null): string {
  if (minimo === null) return `Até R$ ${maximo}`;
  if (maximo === null) return `Acima de R$ ${minimo}`;
  return `R$ ${minimo} a R$ ${maximo}`;
}

function encontraCaminhoDaCategoria(categorias: Categoria[], slug: string | undefined): Categoria[] {
  for (const categoria of categorias) {
    if (categoria.slug === slug) return [categoria];
    const caminho = encontraCaminhoDaCategoria(categoria.subcategorias, slug);
    if (caminho.length > 0) return [categoria, ...caminho];
  }
  return [];
}
