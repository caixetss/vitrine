# Testes do backend

## Como rodar

O Docker precisa estar aberto. Dentro de `backend`:

```bash
mvn verify
```

No fim deve aparecer `BUILD SUCCESS`. Cada teste de integração sobe um PostgreSQL temporário no Docker, roda as migrations e apaga tudo ao terminar. O banco do seu `docker compose` não é tocado.

## Os dois tipos

| | Unitário | Integração |
|---|---|---|
| O que testa | uma classe sozinha | a rota inteira, como o front chama |
| Banco | nenhum (o repositório é simulado com Mockito) | PostgreSQL de verdade |
| Velocidade | milissegundos | alguns segundos para subir |
| Exemplo | o service monta a árvore de categorias certo? | `GET /api/produtos?precoMax=500` devolve só os produtos até R$ 500? |

Regra para escolher: se o comportamento só aparece quando as peças conversam (consulta no banco, conversão para JSON, validação dos parâmetros), é integração. Se cabe dentro de uma classe, é unitário.

## Onde fica cada coisa

```
backend/src/test/java/io/github/caixetss/vitrine/
├── _unit/
│   ├── helpers/        cria objetos em memória para os testes
│   ├── service/        CategoriaServiceTest
│   └── excecao/        TratadorGlobalExcecoesTest
└── _integration/
    ├── AbstractMvcIntegrationTest    base de todos: sobe a aplicação e o banco
    ├── helpers/        salva categorias, marcas, produtos... no banco do teste
    ├── CategoriaTest
    ├── ProdutoTest
    ├── AvaliacaoTest
    ├── DocumentacaoApiTest
    └── ...
```

## Como ler um teste de integração

Cada arquivo é dividido em **gavetas**, uma por rota. Dentro de cada rota há até duas gavetas, na ordem em que a requisição passa:

```
ProdutoTest
└── GET /api/produtos
    ├── Payload    os parâmetros estão certos?  (preço negativo, ordem desconhecida → 400)
    └── Service    o resultado está certo?       (filtros, ordenação, paginação → 200)
```

Dentro de cada gaveta, os cenários seguem sempre a mesma ordem: **entrada inválida → não encontrado → regra de negócio → sucesso por último**.

Um teste lido do começo ao fim:

```java
@Test
@DisplayName("Deve ordenar por menor preço")
void deveOrdenarPorMenorPreco() throws Exception {
    Produto caro = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("300.00").salva();
    Produto barato = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("10.00").salva();

    mockMvc.perform(get("/api/produtos")
                    .param("categoria", categoriaPrincipal.getSlug())
                    .param("ordem", "menor-preco"))
            .andExpect(jsonPath("$.itens[*].slug").value(contains(barato.getSlug(), caro.getSlug())));
}
```

Todo teste tem três passos:

1. **Prepara:** as duas primeiras linhas salvam um produto caro e um barato no banco do teste.
2. **Chama:** o `mockMvc.perform(get(...))` faz a requisição, como o front faria.
3. **Confere:** o `andExpect` verifica que o barato veio antes do caro.

O `@DisplayName` diz em português o que se espera, e é essa frase que aparece no relatório quando o teste falha.

## Regras do projeto

- **Cada teste cria os próprios dados.** Os helpers salvam uma categoria, marca e produtos novos, com nomes gerados pelo [Datafaker](https://www.datafaker.net/). Nenhum teste depende dos dados de exemplo da migration V3, então mudar esses dados não quebra teste.
- **O nome diz o que se espera.** `@DisplayName` em português e o método com a mesma frase (`deveOrdenarPorMenorPreco`, `naoDeveAceitarPaginaNegativa`). Se o teste precisa de comentário para ser entendido, o nome está ruim.
- **A rota fica escrita na própria linha** (`get("/api/produtos")`), para se ver na hora qual endpoint está sendo testado.
- **Nos testes unitários, a classe é criada à mão** no `@BeforeEach`, recebendo os mocks pelo construtor. Se o construtor mudar, o teste deixa de compilar e o problema aparece na hora.
- **Teste novo vai em `_unit/` ou `_integration/`**, nunca solto em outra pasta.

## Criando um teste novo

1. Escolha o tipo (unitário ou integração) pela regra acima.
2. Se a rota já tem arquivo (`ProdutoTest`), entre na gaveta dela; senão, crie `<Assunto>Test` estendendo `AbstractMvcIntegrationTest`.
3. Coloque o teste na gaveta certa (`Payload` ou `Service`), na posição certa da ordem.
4. Use os helpers para criar os dados. Se faltar um helper para uma entidade, crie em `_integration/helpers` e registre em `HelpersConfiguration`.
5. Rode `mvn verify`.
