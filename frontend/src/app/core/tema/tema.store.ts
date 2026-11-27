import { effect, Injectable, signal } from '@angular/core';

const CHAVE_DO_TEMA_NO_NAVEGADOR = 'vitrine.tema';

@Injectable({ providedIn: 'root' })
export class TemaStore {
  readonly ehTemaEscuro = signal(document.documentElement.dataset['tema'] === 'escuro');

  constructor() {
    effect(() => {
      const tema = this.ehTemaEscuro() ? 'escuro' : 'claro';
      document.documentElement.dataset['tema'] = tema;
      try {
        localStorage.setItem(CHAVE_DO_TEMA_NO_NAVEGADOR, tema);
      } catch {
        return;
      }
    });
  }

  alternar(): void {
    this.ehTemaEscuro.update((ehEscuro) => !ehEscuro);
  }
}
