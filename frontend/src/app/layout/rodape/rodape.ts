import { Component } from '@angular/core';

interface ColunaDoRodape {
  titulo: string;
  itens: string[];
}

@Component({
  selector: 'app-rodape',
  imports: [],
  templateUrl: './rodape.html',
  styleUrl: './rodape.scss',
})
export class Rodape {
  protected readonly anoAtual = new Date().getFullYear();

  protected readonly colunas: ColunaDoRodape[] = [
    { titulo: 'Institucional', itens: ['Sobre a Vitrine', 'Trabalhe conosco', 'Fale conosco', 'Imprensa'] },
    { titulo: 'Ajuda', itens: ['Central de ajuda', 'Trocas e devoluções', 'Prazos de entrega', 'Formas de pagamento'] },
    { titulo: 'Minha conta', itens: ['Meus pedidos', 'Favoritos', 'Endereços', 'Cupons'] },
  ];

  protected readonly formasDePagamento = ['Pix', 'Cartão de crédito', 'Boleto', 'Cartão de débito'];
}
