import { Routes } from '@angular/router';

const carregaEmConstrucao = () => import('./features/em-construcao/em-construcao').then((m) => m.EmConstrucao);

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/layout-principal/layout-principal').then((m) => m.LayoutPrincipal),
    children: [
      { path: '', loadComponent: carregaEmConstrucao, data: { titulo: 'Página inicial' } },
      { path: 'categoria/:slug', loadComponent: carregaEmConstrucao, data: { titulo: 'Produtos da categoria' } },
      { path: 'busca', loadComponent: carregaEmConstrucao, data: { titulo: 'Resultado da busca' } },
      { path: 'produto/:slug', loadComponent: carregaEmConstrucao, data: { titulo: 'Página do produto' } },
      { path: 'design-system', loadComponent: () => import('./features/design-system/design-system').then((m) => m.DesignSystem) },
      { path: '**', loadComponent: () => import('./features/nao-encontrado/nao-encontrado').then((m) => m.NaoEncontrado) },
    ],
  },
];
