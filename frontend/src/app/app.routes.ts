import { Routes } from '@angular/router';

const carregaListagem = () => import('./features/listagem/listagem').then((m) => m.Listagem);

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/layout-principal/layout-principal').then((m) => m.LayoutPrincipal),
    children: [
      { path: '', loadComponent: () => import('./features/home/home').then((m) => m.Home) },
      { path: 'categoria/:slug', loadComponent: carregaListagem },
      { path: 'busca', loadComponent: carregaListagem },
      { path: 'produto/:slug', loadComponent: () => import('./features/produto/produto').then((m) => m.Produto) },
      { path: 'design-system', loadComponent: () => import('./features/design-system/design-system').then((m) => m.DesignSystem) },
      { path: '**', loadComponent: () => import('./features/nao-encontrado/nao-encontrado').then((m) => m.NaoEncontrado) },
    ],
  },
];
