# Arquitetura e decisões

Como o código está organizado e **por que** cada escolha foi feita. A regra geral: arquitetura simples, em camadas, sem abstração que o projeto ainda não precisa.

## 1. Visão geral

```
Navegador (Angular)  ──HTTP/JSON──►  API (Spring Boot)  ──JPA──►  PostgreSQL
                                                                  (Docker em dev, Neon em produção)
```

- O front só conversa com a API, nunca com o banco.
- No MVP a API é **somente leitura** e não guarda sessão. O carrinho fica no navegador (`localStorage`).
- O que a API recebe e devolve está combinado em [api.md](api.md).

## 2. Backend

### Pastas

```
io.github.caixetss.vitrine
├── VitrineApiApplication.java
├── config/        CORS, Swagger, conversor da ordenação
├── controller/    recebe as requisições
├── service/       regras de negócio
├── repository/    consultas ao banco
├── entidade/      classes que espelham as tabelas
├── dto/           formatos de entrada e saída da API
└── excecao/       exceções e tratamento global de erros
```

### Camadas

Uma requisição passa sempre pelo mesmo caminho, e cada camada tem uma responsabilidade só:

```
requisição → Controller → Service → Repository → banco
```

| Camada | Faz | Não faz |
|---|---|---|
| Controller | recebe a requisição, valida os parâmetros, chama o service | regra de negócio, acesso ao banco |
| Service | aplica a regra de negócio e monta a resposta (DTO) | saber de HTTP (status, headers) |
| Repository | consulta o banco | regra de negócio |
| Entidade | espelha uma tabela | sair da API: nunca vai no JSON |
| DTO | define o formato da resposta (`record` imutável) | lógica |

Um service pode chamar o service de outro assunto, mas nunca o repository dele.

### Regras de código

- **Nomes em português**, que dizem o que fazem: `buscaListaProduto`, `buscaIndividualProduto`, `contaProdutosPorMarca`. Palavras do framework continuam em inglês (`Controller`, `Service`, `@Query`).
- **Sem comentários no código:** se precisa de comentário, o nome está ruim. Comentário só para o "porquê" que o código não mostra.
- **Entidades só para leitura:** sem `setter`; relacionamentos carregados só quando usados (`LAZY`).
- **Injeção pelo construtor** (`@RequiredArgsConstructor`, campos `private final`).
- **Erros viram exceção** e o `TratadorGlobalExcecoes` transforma todas no mesmo formato (`ErroDto`). Nenhum `try/catch` no controller.

### Testes

Unitários em `_unit/`, de integração em `_integration/`, com gavetas por rota e dados criados pelo próprio teste. Explicado em [testes.md](testes.md).

## 3. Frontend (em desenvolvimento)

```
src/app/
├── core/       serviços únicos: chamadas à API, carrinho, interceptors
├── shared/     componentes reutilizáveis: card de produto, estrelas, preço
├── features/   uma pasta por página: home, listagem, produto
├── layout/     cabeçalho, rodapé, gaveta do carrinho
└── modelos/    interfaces TypeScript iguais aos DTOs da API
```

- **Componentes de página** (`features/`) buscam dados. **Componentes de exibição** (`shared/`) só recebem dados e avisam eventos.
- Estado com **Signals**, que já vêm no Angular.
- Filtros, ordenação e página ficam na **URL**: dá para compartilhar o link e voltar no navegador sem perder a busca.
- Cada página só é carregada quando o usuário entra nela (lazy loading).

## 4. Git

- Uma branch e um pull request por issue, com `closes #N`.
- Commits no padrão Conventional Commits: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`.
- O CI roda build e testes a cada push.

## 5. Registro de decisões

Cada linha é uma escolha: o que foi decidido, o que ficou de fora, por quê, e o custo de ter escolhido assim.

| # | Decisão | Alternativa descartada | Por quê | Custo |
|---|---|---|---|---|
| D1 | Monólito em camadas | microsserviços, arquitetura hexagonal | o MVP é uma API de leitura; mais camadas seriam código sem uso | se entrar integração externa (pagamento), essa parte pode pedir uma camada de isolamento |
| D2 | Pastas por camada (`controller/`, `service/`...) | pastas por assunto (`produto/`, `categoria/`) | padrão mais conhecido; com poucos assuntos, cada pasta continua pequena | mudar um assunto mexe em várias pastas; reavaliar se o projeto crescer muito |
| D3 | DTOs como `record`, convertidos por um construtor (`new ProdutoResumoDto(produto)`) | MapStruct, métodos `de`/`from` | poucos DTOs, sem dependência extra; o `new` já diz o que acontece | se os DTOs crescerem muito, reavaliar MapStruct |
| D4 | Consultas com `@Query` e nome em português (`buscaListaCategoriaAtiva`) | nomes que o Spring interpreta (`findByEhInativoFalseOrderBy...`) | o nome fica curto e diz a intenção; a consulta fica visível na anotação | escrever a consulta à mão |
| D5 | Filtros dinâmicos com `Specification` | uma consulta com vários `IS NULL OR` | cada filtro é uma peça; entram só os que o cliente escolheu; a mesma montagem serve para a lista e para as contagens | a API de critérios do JPA é mais verbosa que SQL |
| D6 | Ordenação também como `Specification` | `Sort` do Spring | o `Sort` não ordena por conta (percentual de desconto) | a ordenação fica fora do `Pageable` |
| D7 | Evitar N+1: capas buscadas de uma vez; cada coleção do produto numa consulta | deixar o Hibernate carregar item por item | sem isso, uma página de 20 produtos faria mais de 20 consultas | testes conferem o número de consultas |
| D8 | Busca sem acento com `translate` (nativo do PostgreSQL) | extensões `unaccent` e `pg_trgm` | não precisa instalar nada no banco; o catálogo é pequeno | sem índice de texto: com milhões de produtos, reavaliar |
| D9 | Regras e validações no Java (Bean Validation e service) | `CHECK` no banco | a regra fica num lugar só, testável, com mensagem clara | o banco aceita valor inválido se alguém gravar direto por SQL; ele guarda só a estrutura (tipos, chaves, `NOT NULL`, `UNIQUE`) |
| D10 | Erros sempre no formato `ErroDto` | o formato padrão do Spring | o front trata todo erro do mesmo jeito; mensagens em português; nada interno vaza no 500 | um tratador a mais para manter |
| D11 | Swagger desligado por padrão, ligado no perfil `dev` | ligado sempre | esquecer uma configuração no deploy não expõe a documentação | precisa do perfil `dev` (ou `SWAGGER_ATIVO=true`) para ver |
| D12 | Testes de integração com PostgreSQL no Docker (Testcontainers) | banco em memória (H2) | testa no mesmo banco de produção, com as migrations reais | precisa do Docker aberto |
| D13 | Estado do front com Signals | NgRx | o estado é pequeno (carrinho e filtros) | se crescer muito, reavaliar |
| D14 | Carrinho no `localStorage` | tabela no banco | o MVP não tem login | com login (#22), migrar o carrinho para a API |
| D15 | `/categorias` sem paginação | paginar | são poucas dezenas de itens | nenhum |

Ao tomar uma decisão nova, acrescente uma linha.
