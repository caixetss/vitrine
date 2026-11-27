<div align="center">

# Vitrine

**E-commerce multicategoria desenvolvido como projeto de portfólio**

![Java](https://img.shields.io/badge/Java-21-0f172a?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-0f172a?style=flat-square&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-22-0f172a?style=flat-square&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-0f172a?style=flat-square&logo=postgresql&logoColor=white)
[![Backend](https://github.com/caixetss/vitrine/actions/workflows/backend.yml/badge.svg)](https://github.com/caixetss/vitrine/actions/workflows/backend.yml)
[![Frontend](https://github.com/caixetss/vitrine/actions/workflows/frontend.yml/badge.svg)](https://github.com/caixetss/vitrine/actions/workflows/frontend.yml)
![Status](https://img.shields.io/badge/status-em_desenvolvimento-4f46e5?style=flat-square)

</div>

---

## Sobre

A Vitrine é uma loja virtual fictícia com os fluxos principais de um marketplace: categorias, busca com filtros, página de produto, avaliações e carrinho. É uma aplicação full-stack com API REST em **Spring Boot** e front em **Angular**.

O projeto foi feito como um produto real: começou pela engenharia (histórias de usuário, modelo de dados, contrato da API e decisões de arquitetura) e só depois foi para o código, com testes e CI desde o início.

> **Situação atual:** o backend do MVP está pronto e testado. O frontend está em desenvolvimento.

## Destaques técnicos

| O quê | Como | Onde ver |
|---|---|---|
| Filtros combináveis sem SQL repetido | cada filtro é uma peça (`Specification`) e a consulta junta só as que o cliente escolheu | [`ProdutoSpecifications`](backend/src/main/java/io/github/caixetss/vitrine/repository/ProdutoSpecifications.java) |
| Barra de filtros com contagens certas | "Sonora (6)": cada grupo conta ignorando o próprio filtro, para dar para marcar mais de uma marca | [`ContagemFiltrosRepository`](backend/src/main/java/io/github/caixetss/vitrine/repository/ContagemFiltrosRepository.java) |
| Sem o problema de N+1 consultas | a listagem busca as capas de todos os produtos de uma vez; a página do produto faz sempre 4 consultas. Testes garantem os números | [`ProdutoTest`](backend/src/test/java/io/github/caixetss/vitrine/_integration/ProdutoTest.java) |
| Busca sem acento e sem extensão no banco | "camera" encontra "Câmera" usando uma função nativa do PostgreSQL | [decisões](docs/decisoes.md) |
| Erros sempre no mesmo formato | qualquer erro (400, 404, 500) volta como `ErroDto`, com mensagens em português e sem expor detalhes internos | [`TratadorGlobalExcecoes`](backend/src/main/java/io/github/caixetss/vitrine/excecao/TratadorGlobalExcecoes.java) |
| Banco versionado | o schema é criado por migrations (Flyway) e o Hibernate só confere se as classes batem com as tabelas | [migrations](backend/src/main/resources/db/migration) |
| Testes com banco de verdade | 58 testes; os de integração sobem um PostgreSQL no Docker (Testcontainers) e cada teste cria os próprios dados | [docs/testes.md](docs/testes.md) |
| API documentada | Swagger em português, com exemplos prontos para testar; desligado em produção por padrão | [Swagger](#documentação-da-api-swagger) |
| Integração contínua | todo push roda build e testes no GitHub Actions | [workflows](.github/workflows) |

## Arquitetura

```
Navegador (Angular)  ──HTTP/JSON──►  API (Spring Boot)  ──JPA──►  PostgreSQL
                                     controller → service → repository
```

- O front só conversa com a API, nunca com o banco.
- A API é organizada em camadas: o **controller** recebe o pedido, o **service** aplica as regras e o **repository** fala com o banco. As entidades nunca saem na resposta; a API devolve só DTOs.
- Cada decisão (e o motivo dela) está em [docs/decisoes.md](docs/decisoes.md).

## Avalie em 5 minutos

Com Java 21, Maven e Docker instalados:

```bash
docker compose up -d
```

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Abra **http://localhost:8080/swagger-ui.html**, escolha um endpoint e clique em **Execute**. Os campos já vêm com exemplos.

Para rodar os testes:

```bash
mvn verify
```

## O que a loja faz

| Módulo | MVP (primeira versão) | Depois do MVP |
|---|---|---|
| Catálogo | categorias em árvore, marcas | painel administrativo |
| Busca e listagem | busca por nome, filtros por categoria, preço, avaliação, marca e frete grátis, 6 ordenações, paginação | — |
| Produto | galeria, cores, preço, parcelamento, valor no Pix, especificações, avaliações, relacionados | frete por CEP, escrever avaliação |
| Carrinho | adicionar, remover, alterar quantidade, total e economia (salvo no navegador) | cupom, checkout e pedidos |
| Conta | navegação sem login | login, cadastro, favoritos ([#22](https://github.com/caixetss/vitrine/issues/22)) |
| Interface | layout responsivo | modo escuro |

No MVP, o catálogo vem de dados de exemplo nas migrations: 41 produtos em 10 categorias.

## Tecnologias

| Parte | Tecnologias |
|---|---|
| Backend | Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation), Hibernate, Lombok |
| Banco | PostgreSQL 17, Flyway (migrations) |
| Documentação da API | springdoc-openapi (Swagger) |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers, Datafaker |
| Frontend | Angular 22 (standalone components, signals), TypeScript, SCSS |
| Infra | Docker Compose, GitHub Actions |

## Documentação

| Documento | O que tem |
|---|---|
| [Histórias de usuário](docs/historias-de-usuario.md) | o que o cliente precisa conseguir fazer, com critérios de aceite |
| [Modelo de dados](docs/modelo-dados.md) | as tabelas, como se relacionam e por que foram desenhadas assim |
| [Contrato da API](docs/api.md) | cada endpoint, com parâmetros, exemplos de resposta e erros |
| [Arquitetura e decisões](docs/decisoes.md) | como o código está organizado e o motivo de cada escolha |
| [Testes](docs/testes.md) | como os testes estão organizados e como ler e escrever um |

## Estrutura do repositório

```
vitrine/
├── backend/            API REST em Spring Boot
├── frontend/           aplicação Angular
├── docs/               documentação do projeto
├── docker-compose.yml  banco local
└── .github/workflows/  CI
```

## Execução local

### Pré-requisitos

- Java 21
- Maven 3.9+
- Node.js LTS
- Docker (banco local e testes do backend)

### Banco

```bash
docker compose up -d
```

Sobe um PostgreSQL 17 na porta `5433`, com banco, usuário e senha `vitrine`.

### Backend

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

O perfil `dev` já aponta para o banco do Docker, mostra o SQL no log e liga o Swagger. Na primeira vez, o Flyway cria as tabelas e os dados de exemplo. A API fica em `http://localhost:8080`.

Para usar outro banco (como o Neon), defina as variáveis listadas em [`backend/.env.example`](backend/.env.example):

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC, ex.: `jdbc:postgresql://localhost:5433/vitrine` |
| `DB_USER` | usuário do banco |
| `DB_PASSWORD` | senha do banco |
| `CORS_ORIGENS_PERMITIDAS` | origens liberadas para o front, separadas por vírgula. Padrão: `http://localhost:4200` |
| `SWAGGER_ATIVO` | `true` liga a documentação (Swagger). Padrão: desligada; o perfil `dev` liga sozinho |

### Documentação da API (Swagger)

Com o backend rodando no perfil `dev`:

| O quê | Endereço |
|---|---|
| Tela para testar os endpoints | http://localhost:8080/swagger-ui.html |
| Contrato OpenAPI em JSON (dá para importar no Insomnia ou Postman) | http://localhost:8080/v3/api-docs |

### Frontend

```bash
cd frontend
npm install
npm start
```

A aplicação fica em `http://localhost:4200`.

## Testes

```bash
cd backend && mvn verify
cd frontend && npm test
```

Os testes do backend precisam do Docker aberto. Como eles são organizados: [docs/testes.md](docs/testes.md).

## Roadmap

- [x] Engenharia: histórias de usuário, modelo de dados, contrato da API e arquitetura
- [x] Ambiente: Docker, perfil de desenvolvimento e CI
- [x] API de categorias
- [x] API de produtos com filtros, ordenação e paginação
- [x] API de página de produto, avaliações e relacionados
- [x] Tratamento de erros padronizado e documentação Swagger
- [ ] Frontend: layout, listagem, página de produto, carrinho e home
- [ ] Deploy
- [ ] Pós-MVP: autenticação com Spring Security, checkout, painel administrativo

Acompanhe pelo [quadro do projeto](https://github.com/users/caixetss/projects/4).

## Autoria

Desenvolvido por [Gabrielle Caixeta](https://github.com/caixetss).
