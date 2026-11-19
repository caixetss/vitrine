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

Pacote base `io.github.caixetss.vitrine`, organizado por funcionalidade:

```
io.github.caixetss.vitrine
├── VitrineApiApplication.java
├── categoria/
│   ├── CategoriaController.java
│   ├── CategoriaService.java
│   ├── CategoriaRepository.java
│   ├── Categoria.java              (entidade)
│   └── CategoriaDto.java           (record)
├── marca/
├── produto/
│   ├── ProdutoController.java
│   ├── ProdutoService.java
│   ├── ProdutoRepository.java
│   ├── ProdutoSpecifications.java  (filtros dinâmicos)
│   ├── Produto.java
│   ├── ProdutoVariacao.java
│   ├── ProdutoImagem.java
│   ├── ProdutoEspecificacao.java
│   ├── ProdutoResumoDto.java
│   ├── ProdutoDetalheDto.java
│   └── FiltrosProdutoDto.java
├── avaliacao/
└── compartilhado/
    ├── PaginaDto.java
    ├── ErroDto.java
    ├── RecursoNaoEncontradoException.java
    └── TratadorGlobalExcecoes.java
```

Sem subpastas `controller/`, `service/` dentro de cada funcionalidade: com 5 a 10 arquivos por pacote, a pasta plana é mais fácil de navegar. Se um pacote passar de ~15 arquivos, aí sim dividir.

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

| Tipo | Ferramenta | Testa |
|---|---|---|
| Unidade | JUnit 5, Mockito, AssertJ | regras do service |
| Integração | `@SpringBootTest` + Testcontainers (PostgreSQL) | endpoint de ponta a ponta, com as migrations reais |

Testcontainers em vez de H2: a busca usa `unaccent` e `pg_trgm`, que o H2 não tem.

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
| D2 | Pacote por funcionalidade | pacote por camada (`controllers/`, `services/`) | tudo de "produto" fica junto; apagar ou mudar uma funcionalidade mexe em uma pasta | código compartilhado precisa de um lugar explícito (`compartilhado/`) |
| D3 | Pacote base `io.github.caixetss.vitrine` | `vitrine` | segue o `groupId` do `pom.xml` e alinha com o pacote dos testes | nenhum |
| D4 | Conversão entidade → DTO com método estático no record (`ProdutoResumoDto.de(produto)`) | MapStruct | poucos DTOs; evita dependência e geração de código | se os DTOs crescerem muito, reavaliar MapStruct |
| D5 | DTOs como `record` | classes com Lombok | imutáveis e sem boilerplate, nativo do Java 21 | nenhum |
| D6 | Filtros dinâmicos com Specifications | `@Query` com vários `IS NULL OR` | combina só os filtros enviados e mantém a consulta legível | a busca sem acento precisa de função SQL (`unaccent`) via `criteriaBuilder.function` |
| D7 | Testes de integração com Testcontainers | H2 | banco igual ao de produção, com as extensões | precisa do Docker rodando |
| D8 | Estado no front com Signals | NgRx, só RxJS | o estado é pequeno (carrinho e filtros); Signals já vem no Angular | se o estado global crescer, reavaliar |
| D9 | Carrinho no `localStorage` | tabela no banco | o MVP não tem login | ao criar login, migrar o carrinho para a API |
| D10 | Sem paginação em `/categorias` | paginar | a lista tem poucas dezenas de itens | nenhum |

Ao tomar uma decisão nova, adicionar uma linha nesta tabela.
