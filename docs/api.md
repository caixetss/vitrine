# Contrato da API

O que a API recebe e devolve, endpoint por endpoint. Foi escrito **antes do código**, como um acordo entre front e back, a partir das [histórias de usuário](historias-de-usuario.md) e do [modelo de dados](modelo-dados.md).

No MVP todos os endpoints são `GET`, públicos e somente leitura. Para testar ao vivo, use o Swagger (`/swagger-ui.html`, com o backend no perfil `dev`).

## 1. Convenções

| Item | Regra | Exemplo |
|---|---|---|
| Prefixo | todas as rotas começam com `/api` | `/api/produtos` |
| Recursos | plural, minúsculas, com hífen | `/api/categorias` |
| Campos do JSON | camelCase | `precoOriginal`, `ehFreteGratis` |
| Dinheiro | número com 2 casas; o front formata em R$ | `279.90` |
| Datas | ISO 8601 em UTC | `2026-11-02T14:32:00Z` |
| Identificação na URL | `slug`, não `id` | `/api/produtos/fone-bluetooth-jbl-tune-520` |
| Campo sem valor | `null`, nunca omitido | `"precoOriginal": null` |

## 2. Paginação

Parâmetros:

| Parâmetro | Padrão | Regra |
|---|---|---|
| `pagina` | `0` | começa em 0; `>= 0` |
| `tamanho` | `20` | entre 1 e 60 |

Resposta (`PaginaDto`):

```json
{
  "itens": [],
  "pagina": 0,
  "tamanho": 20,
  "totalItens": 134,
  "totalPaginas": 7
}
```

## 3. Erros

Corpo padrão (`ErroDto`):

```json
{
  "status": 400,
  "erro": "Requisição inválida",
  "mensagem": "Parâmetros inválidos",
  "campos": [
    { "campo": "precoMin", "mensagem": "deve ser menor ou igual a precoMax" }
  ]
}
```

| Status | Quando | `campos` |
|---|---|---|
| 400 | parâmetro inválido (tipo errado, fora do intervalo, valor de ordenação desconhecido) | lista dos campos com erro |
| 404 | slug inexistente ou recurso inativo | `[]` |
| 422 | regra de negócio impede a operação (pós-MVP: checkout, cupom) | `[]` |
| 500 | erro inesperado; mensagem genérica, sem stack trace | `[]` |

## 4. `GET /api/categorias`

Árvore de categorias ativas, ordenada por `ordem_exibicao`. Subcategorias inativas também ficam de fora.

```json
[
  {
    "id": "6f1c...",
    "nome": "Áudio",
    "slug": "audio",
    "icone": "headphones",
    "subcategorias": [
      { "id": "a2b9...", "nome": "Fones de ouvido", "slug": "fones-de-ouvido", "icone": null, "subcategorias": [] }
    ]
  }
]
```

Sem paginação: a lista é pequena.

## 5. `GET /api/produtos`

Listagem e busca. Usado pela página de categoria, pela busca e pelas vitrines da home.

| Parâmetro | Tipo | Regra |
|---|---|---|
| `categoria` | slug | inclui produtos das subcategorias |
| `busca` | texto | ignora maiúsculas e acentos; mínimo 2 caracteres |
| `precoMin`, `precoMax` | número | `>= 0`; `precoMin <= precoMax` |
| `avaliacaoMin` | inteiro | entre 1 e 5 |
| `marcas` | lista de slugs | `marcas=jbl,sony` |
| `freteGratis` | boolean | `true` filtra; ausente ou `false` não filtra |
| `ordem` | enum | ver tabela abaixo; padrão `relevancia` |
| `pagina`, `tamanho` | | seção 2 |

Só produtos ativos. Filtros combinam com E; várias marcas combinam com OU.

| `ordem` | Ordenação | Uso |
|---|---|---|
| `relevancia` | `total_vendidos` desc | padrão da listagem |
| `menor-preco` | `preco` asc | |
| `maior-preco` | `preco` desc | |
| `avaliacao` | `avaliacao_media` desc, depois `total_avaliacoes` desc | |
| `mais-vendidos` | `total_vendidos` desc | "Mais vendidos da semana" |
| `maior-desconto` | `(preco_original - preco) / preco_original` desc, sem desconto por último | "Ofertas do dia" |

Desempate em todas as ordens: `nome` asc, para a paginação não repetir itens.

Item (`ProdutoResumoDto`):

```json
{
  "id": "c41d...",
  "slug": "fone-bluetooth-jbl-tune-520",
  "nome": "Fone de Ouvido Bluetooth JBL Tune 520BT",
  "imagemCapa": "https://.../jbl-520-preto-0.jpg",
  "preco": 279.90,
  "precoOriginal": 399.90,
  "avaliacaoMedia": 4.7,
  "totalAvaliacoes": 1284,
  "ehFreteGratis": true
}
```

`imagemCapa` é a imagem de `posicaoGaleria = 0`. Desconto, parcela e valor no Pix são calculados no front.

Exemplo: `GET /api/produtos?categoria=audio&precoMax=500&marcas=jbl&ordem=menor-preco&pagina=0` devolve um `PaginaDto` de `ProdutoResumoDto`.

## 6. `GET /api/produtos/filtros`

Contagens da barra de filtros. Recebe os mesmos filtros de `/api/produtos`, sem `ordem`, `pagina` e `tamanho`.

```json
{
  "subcategorias": [ { "slug": "fones-de-ouvido", "nome": "Fones de ouvido", "total": 42 } ],
  "marcas": [ { "slug": "jbl", "nome": "JBL", "total": 12 } ],
  "precoMinimo": 49.90,
  "precoMaximo": 2499.00,
  "totalFreteGratis": 30
}
```

Regra: cada grupo conta ignorando o próprio filtro. A contagem de marcas ignora `marcas`, senão as outras marcas sempre mostrariam 0 e não daria para marcar mais de uma.

## 7. `GET /api/produtos/{slug}`

Página do produto (`ProdutoDetalheDto`). Campos do card mais:

```json
{
  "id": "c41d...",
  "slug": "fone-bluetooth-jbl-tune-520",
  "nome": "Fone de Ouvido Bluetooth JBL Tune 520BT",
  "descricao": "Som JBL Pure Bass...",
  "preco": 279.90,
  "precoOriginal": 399.90,
  "avaliacaoMedia": 4.7,
  "totalAvaliacoes": 1284,
  "totalVendidos": 5230,
  "ehFreteGratis": true,
  "marca": { "nome": "JBL", "slug": "jbl" },
  "categoria": {
    "nome": "Fones de ouvido",
    "slug": "fones-de-ouvido",
    "categoriaPai": { "nome": "Áudio", "slug": "audio" }
  },
  "imagens": [ { "url": "https://...", "textoAlternativo": "Fone JBL preto, vista frontal", "posicaoGaleria": 0 } ],
  "variacoes": [ { "id": "9e7a...", "nomeCor": "Preto", "corHex": "#111111", "codigoEstoque": "JBL520-PRT", "estoque": 12 } ],
  "especificacoes": [ { "nome": "Bateria", "valor": "Até 57 h" } ]
}
```

- `imagens` por `posicaoGaleria`; `especificacoes` por `ordem_exibicao`; só variações ativas.
- `categoriaPai` é `null` em categoria principal.
- 404 se o slug não existir ou o produto estiver inativo.

## 8. `GET /api/produtos/{slug}/avaliacoes`

Paginado (padrão `tamanho=5`), mais recentes primeiro.

```json
{
  "resumo": {
    "media": 4.7,
    "total": 1284,
    "distribuicao": { "5": 980, "4": 210, "3": 60, "2": 20, "1": 14 }
  },
  "avaliacoes": {
    "itens": [
      {
        "nomeAutor": "Mariana S.",
        "nota": 5,
        "titulo": "Bateria dura muito",
        "comentario": "Uso o dia todo...",
        "ehCompraVerificada": true,
        "criadoEm": "2026-11-02T14:32:00Z"
      }
    ],
    "pagina": 0, "tamanho": 5, "totalItens": 1284, "totalPaginas": 257
  }
}
```

`distribuicao` sempre traz as 5 notas, com `0` quando não houver. O percentual é calculado no front. 404 se o produto não existir.

## 9. `GET /api/produtos/{slug}/relacionados`

"Quem viu também viu": até 5 `ProdutoResumoDto` ativos da mesma categoria, exceto o próprio, por `total_vendidos` desc. Lista vazia é válida. 404 se o produto não existir.

## 10. Decisões

| Pergunta | Decisão | Motivo |
|---|---|---|
| O que é "relevância"? | `total_vendidos` desc | é o melhor sinal disponível sem um motor de busca |
| Slug ou id na URL? | slug | URL legível e boa para SEO; o id continua no JSON para o carrinho |
| Endpoint separado para filtros? | sim | a listagem fica simples e as contagens podem ser carregadas em paralelo |
| Avaliações dentro do produto? | não, endpoint próprio | são paginadas e podem ser muitas |
| Swagger/OpenAPI | `springdoc-openapi` em `/swagger-ui.html`, só no perfil `dev` | gera a documentação a partir do código; este arquivo continua como o contrato de referência |
