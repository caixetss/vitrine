# Histórias de usuário do MVP

O ponto de partida do projeto: o que o cliente precisa conseguir fazer na loja. Cada história descreve uma funcionalidade do ponto de vista dele e lista os critérios que precisam ser verdadeiros para ela estar pronta.

A coluna **Issues** liga cada história às tarefas do GitHub onde ela é implementada.

## Resumo

| ID | História | Issues |
|---|---|---|
| HU01 | Navegar por categorias | #9, #14, #19 |
| HU02 | Ver produtos de uma categoria | #10, #15, #16 |
| HU03 | Buscar produto por nome | #10, #14, #16 |
| HU04 | Filtrar e ordenar resultados | #10, #16 |
| HU05 | Ver detalhes do produto | #11, #17 |
| HU06 | Ver avaliações do produto | #11, #17 |
| HU07 | Adicionar produto ao carrinho | #17, #18 |
| HU08 | Gerenciar o carrinho | #18 |
| HU09 | Descobrir ofertas na página inicial | #15, #19 |

---

### HU01 — Navegar por categorias

**Como** cliente, **quero** ver as categorias da loja **para** chegar rápido ao tipo de produto que procuro.

**Critérios de aceite**
- O menu do topo lista as categorias e está presente em todas as páginas.
- A página inicial exibe atalhos com ícone e nome de cada categoria.
- Clicar em uma categoria abre a listagem dela.
- Apenas categorias ativas são exibidas.

### HU02 — Ver produtos de uma categoria

**Como** cliente, **quero** ver todos os produtos de uma categoria **para** comparar opções.

**Critérios de aceite**
- A página exibe o caminho de navegação (Início / Categoria), o nome da categoria e o total de resultados.
- Cada produto aparece em um card com imagem, nome, avaliação média e quantidade de avaliações, preço atual, preço anterior riscado e percentual de desconto (quando houver), parcelamento e selo de frete grátis (quando houver).
- Valores são exibidos em reais (R$ 1.599,00).
- Os resultados são paginados.
- Clicar no card abre a página do produto.
- Uma categoria sem produtos exibe uma mensagem de lista vazia.

### HU03 — Buscar produto por nome

**Como** cliente, **quero** buscar pelo nome **para** encontrar um produto específico sem navegar pelas categorias.

**Critérios de aceite**
- A barra de busca está no topo de todas as páginas.
- A busca ignora maiúsculas, minúsculas e acentos.
- Os resultados usam a mesma tela, filtros e ordenação da HU02.
- Uma busca sem resultados exibe mensagem e sugere limpar o termo.

### HU04 — Filtrar e ordenar resultados

**Como** cliente, **quero** filtrar e ordenar a lista **para** reduzir as opções ao que me interessa.

**Critérios de aceite**
- Filtros disponíveis: subcategoria, faixa de preço (mínimo e máximo ou faixas prontas), avaliação mínima, marca e frete grátis.
- Cada opção de filtro mostra quantos produtos ela retorna.
- Filtros ativos aparecem como chips removíveis, com a opção "Limpar filtros".
- Ordenação: mais relevantes, menor preço, maior preço e mais bem avaliados.
- Filtros, ordenação e página ficam na URL, permitindo compartilhar e voltar no navegador sem perder o estado.
- No celular, os filtros abrem em uma gaveta.

### HU05 — Ver detalhes do produto

**Como** cliente, **quero** ver todas as informações de um produto **para** decidir se vou comprá-lo.

**Critérios de aceite**
- A página exibe galeria de imagens com miniaturas e contador (1 / 5).
- Exibe marca, nome completo, avaliação média e total de avaliações.
- Exibe preço anterior, preço atual, percentual de desconto, parcelamento em até 10x sem juros e valor no Pix com 5% de desconto.
- Permite escolher a variação de cor; a cor selecionada fica destacada.
- Permite escolher a quantidade, limitada ao estoque disponível da variação.
- Exibe descrição e tabela de especificações técnicas.
- Exibe a seção "Quem viu também viu" com produtos da mesma categoria.
- Um produto inexistente exibe a página de não encontrado.
- Um produto sem estoque exibe "Indisponível" e desabilita a compra.

### HU06 — Ver avaliações do produto

**Como** cliente, **quero** ler a opinião de outros compradores **para** confiar na compra.

**Critérios de aceite**
- Exibe a nota média, o total de avaliações e a distribuição percentual de 1 a 5 estrelas.
- Cada avaliação mostra nome, data, nota, título e texto.
- As avaliações mais recentes aparecem primeiro, com opção de ver todas.

### HU07 — Adicionar produto ao carrinho

**Como** cliente, **quero** adicionar um produto ao carrinho **para** comprá-lo depois.

**Critérios de aceite**
- "Adicionar ao carrinho" inclui o produto com a variação e a quantidade escolhidas e abre a gaveta do carrinho.
- Adicionar um item que já está no carrinho soma a quantidade, sem duplicar a linha.
- O contador do ícone do carrinho é atualizado na hora.
- "Comprar agora" adiciona o item e leva direto ao carrinho.
- Não é possível adicionar sem escolher a variação.

### HU08 — Gerenciar o carrinho

**Como** cliente, **quero** revisar e ajustar o carrinho **para** saber quanto vou pagar.

**Critérios de aceite**
- A gaveta lista cada item com imagem, nome, variação, quantidade, preço anterior e preço atual.
- É possível alterar a quantidade (mínimo 1, máximo o estoque) e remover itens.
- Exibe subtotal, frete, total, parcelamento, valor no Pix e quanto o cliente está economizando.
- Exibe uma barra de progresso até o frete grátis (acima de R$ 79) e uma mensagem quando o valor é atingido.
- O carrinho é mantido ao recarregar a página ou fechar o navegador.
- Carrinho vazio exibe mensagem e um botão para continuar comprando.

### HU09 — Descobrir ofertas na página inicial

**Como** cliente, **quero** ver destaques e ofertas logo ao entrar **para** aproveitar promoções.

**Critérios de aceite**
- Exibe banner de campanha, faixa de benefícios (frete, parcelamento, Pix, troca) e atalhos de categorias.
- Exibe "Ofertas do dia" com contador regressivo até o fim do dia.
- Exibe "Mais vendidos da semana".
- Cada vitrine tem link para a listagem completa.

---

## Requisitos não funcionais

| Tipo | Requisito |
|---|---|
| Responsividade | Todas as telas funcionam de 360 px a 1440 px de largura, sem rolagem horizontal. |
| Acessibilidade | Navegação completa por teclado, foco visível, imagens com texto alternativo, contraste mínimo WCAG AA e botões de ícone com rótulo acessível. |
| Desempenho | Endpoints de listagem respondem em até 500 ms com o catálogo de exemplo; a primeira página carrega em até 3 s em conexão 4G. |
| Feedback | Toda tela que busca dados exibe estados de carregamento, vazio e erro. |
| Compatibilidade | Últimas duas versões de Chrome, Firefox, Safari e Edge. |
| Qualidade | Cada endpoint tem teste automatizado e o CI bloqueia merge com falha. |
