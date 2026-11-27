# Arquitetura e decisões

Arquitetura do MVP: simples, em camadas, sem abstração que o projeto ainda não precisa. Cada decisão abaixo diz o contexto, o que foi escolhido, o que ficou de fora e o custo.

## 1. Visão geral

```
Navegador (Angular)  --HTTP/JSON-->  API (Spring Boot)  --JPA-->  PostgreSQL (Neon)
```

- O front só fala com a API, nunca com o banco.
- A API é stateless e somente leitura no MVP. O carrinho fica no `localStorage`.
- O contrato entre os dois é o [docs/api.md](api.md).

## 2. Backend

### Pacotes

Pacote base `io.github.caixetss.vitrine`, organizado por camada:

```
io.github.caixetss.vitrine
├── VitrineApiApplication.java
├── config/        CorsConfig, ConversorOrdemProduto
├── controller/    CategoriaController, ProdutoController, ...
├── service/       CategoriaService, ProdutoService, ...
├── repository/    CategoriaRepository, ProdutoRepository, ProdutoSpecifications, ...
├── entidade/      Categoria, Marca, Produto, ProdutoVariacao, ProdutoImagem, ...
├── dto/           CategoriaDto, ProdutoResumoDto, ProdutoDetalheDto, PaginaDto, ErroDto, ...
└── excecao/       RecursoNaoEncontradoException, TratadorGlobalExcecoes
```

Os testes ficam em `_unit/` e `_integration/` (ver seção Testes).

### Camadas

| Camada | Faz | Não faz |
|---|---|---|
| Controller | recebe a requisição, valida parâmetros (`@Valid`), chama o service, devolve DTO | regra de negócio, acesso ao banco |
| Service | regra de negócio, monta o DTO | conhecer HTTP (status, headers) |
| Repository | consulta ao banco (Spring Data, Specifications) | regra de negócio |
| Entidade | espelha a tabela | sair da API (nunca vai no JSON) |
| DTO | formato da resposta, `record` imutável | lógica |

Fluxo de dependência só para baixo: `Controller → Service → Repository`. Um service pode chamar o service de outra funcionalidade, nunca o repository dela.

### Regras de código

- Entidades: `@NoArgsConstructor(access = PROTECTED)`, `@Getter`; sem `@Setter` público (a API é somente leitura). Relacionamentos `@ManyToOne(fetch = LAZY)`.
- Injeção por construtor com `@RequiredArgsConstructor` e campos `private final`.
- `@Transactional(readOnly = true)` nos métodos de leitura do service. Com `open-in-view=false`, o DTO precisa ser montado dentro da transação.
- Erros viram exceção (`RecursoNaoEncontradoException`) e o `TratadorGlobalExcecoes` (`@RestControllerAdvice`) transforma em `ErroDto`. Sem `try/catch` no controller.
- Nomes de métodos: `buscaLista...`, `buscaIndividual...`, `adicionar...`, `editar...`, `deletar...`, `eh...` (booleano).
- Código, classes e variáveis em português; palavras-chave do framework em inglês.

### Testes

| Tipo | Pacote e nome | Ferramenta | Testa |
|---|---|---|---|
| Unidade | `_unit/<camada>/<Classe>Test` | JUnit 5, Mockito, AssertJ | regra isolada de uma classe |
| Integração | `_integration/<Assunto>Test` | `AbstractMvcIntegrationTest` + Testcontainers (PostgreSQL) | a rota inteira, com as migrations reais |

Padrão (o mesmo do GBSys):

- **Gavetas `@Nested`:** no unitário, uma por método público; na integração, uma por rota (`"GET /api/produtos"`) com `Payload` (parâmetros inválidos) e `Service` (regra e resultado) dentro. `SpringSecurity` entra quando houver rotas protegidas (#22). Gaveta vazia não se cria.
- **Nomes:** `@DisplayName` com a frase em português e método igual em camelCase (`deve...`, `naoDeve...`). Ordem dos cenários: entrada inválida → não encontrado → regra → sucesso por último.
- **Sem `@InjectMocks`:** a classe testada é criada à mão no `@BeforeEach`, para o teste não compilar se o construtor mudar.
- **Dados próprios:** cada teste cria o que precisa pelos helpers (`_unit/helpers` estáticos; `_integration/helpers` como beans que salvam no banco), com Faker. Nenhum teste depende dos dados de exemplo da V3.
- **Rota escrita na própria chamada** (`get("/api/produtos")`), sem constante.

Testcontainers em vez de H2: os testes rodam no mesmo PostgreSQL de produção.

## 3. Frontend

```
src/app/
├── core/                  serviços únicos e configuração
│   ├── api/               categoria.service.ts, produto.service.ts
│   ├── carrinho/          carrinho.store.ts
│   └── interceptors/      erro.interceptor.ts
├── shared/                componentes de exibição reutilizáveis
│   ├── card-produto/
│   ├── avaliacao-estrelas/
│   ├── preco/
│   └── estado-vazio/
├── features/              uma pasta por página (carregada sob demanda)
│   ├── home/
│   ├── listagem/
│   └── produto/
├── layout/                cabeçalho, rodapé, gaveta do carrinho
└── modelos/               interfaces TypeScript iguais aos DTOs da API
```

- Componentes standalone; rotas das `features/` com `loadComponent` (lazy).
- **Página** (em `features/`): busca dados e guarda estado. **Exibição** (em `shared/`): só recebe `input()` e emite `output()`, sem chamar serviço.
- Estado com Signals. Dados da API com `HttpClient` tipado pelas interfaces de `modelos/`.
- Filtros, ordenação e página ficam na URL (query params), que é a fonte da verdade da listagem.
- URL da API em `environments/environment.ts`.
- Estilo: SCSS por componente; cores, espaçamentos e fontes como variáveis CSS em `styles.scss`.

## 4. Git

- Branch por issue a partir da `main`: `feat/9-endpoint-categorias`, `fix/...`, `docs/...`.
- Commits no padrão Conventional Commits: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`.
- Um pull request por issue, com `closes #N` na descrição.
- `main` protegida: merge só por PR com CI verde (configurado na #7).

## 5. Registro de decisões

| # | Decisão | Alternativas | Motivo | Custo |
|---|---|---|---|---|
| D1 | Monólito em camadas (controller, service, repository) | hexagonal, microsserviços | o MVP é um CRUD de leitura; mais camadas seriam código sem uso | se surgir integração externa (pagamento), pode pedir uma camada de porta/adaptador |
| D2 | Pacote por camada (`controller/`, `service/`, `repository/`, `entidade/`, `dto/`) | pacote por funcionalidade (`categoria/`, `produto/`) | padrão mais conhecido; com poucas funcionalidades, cada pasta continua pequena | mudar uma funcionalidade mexe em várias pastas; se o projeto crescer muito, reavaliar a divisão por funcionalidade |
| D3 | Pacote base `io.github.caixetss.vitrine` | `vitrine` | segue o `groupId` do `pom.xml` e alinha com o pacote dos testes | nenhum |
| D4 | Conversão entidade → DTO com construtor extra no record (`new ProdutoResumoDto(produto)`) | MapStruct, método estático (`de`, `from`) | poucos DTOs; evita dependência; o `new` já diz o que acontece, sem nome para inventar | se os DTOs crescerem muito, reavaliar MapStruct |
| D5 | DTOs como `record` | classes com Lombok | imutáveis e sem boilerplate, nativo do Java 21 | nenhum |
| D6 | Filtros dinâmicos com Specifications | `@Query` com vários `IS NULL OR` | combina só os filtros enviados e mantém a consulta legível | a busca sem acento usa a função `translate` do PostgreSQL via `criteriaBuilder.function` |
| D7 | Testes de integração com Testcontainers | H2 | banco igual ao de produção | precisa do Docker rodando |
| D8 | Estado no front com Signals | NgRx, só RxJS | o estado é pequeno (carrinho e filtros); Signals já vem no Angular | se o estado global crescer, reavaliar |
| D9 | Carrinho no `localStorage` | tabela no banco | o MVP não tem login | ao criar login, migrar o carrinho para a API |
| D10 | Sem paginação em `/categorias` | paginar | a lista tem poucas dezenas de itens | nenhum |
| D11 | Regras de negócio e validações (preço > 0, nota de 1 a 5, estoque >= 0, formato da cor) ficam no Java, com Bean Validation e no service | `CHECK` no banco | a regra fica num lugar só, testável e com mensagem de erro clara | o banco aceita valor inválido se alguém gravar direto por SQL. O banco guarda só a estrutura: tipo, `NOT NULL`, chaves, `UNIQUE` e valores padrão |

Ao tomar uma decisão nova, adicionar uma linha nesta tabela.
