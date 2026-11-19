<div align="center">

# Vitrine

**E-commerce multicategoria desenvolvido como projeto de portfólio**

![Java](https://img.shields.io/badge/Java-21-0f172a?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-0f172a?style=flat-square&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-22-0f172a?style=flat-square&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-0f172a?style=flat-square&logo=postgresql&logoColor=white)
![Status](https://img.shields.io/badge/status-em_desenvolvimento-4f46e5?style=flat-square)

</div>

---

## Sobre

A Vitrine é uma loja virtual fictícia que reproduz os fluxos principais de um marketplace: vitrine de ofertas, busca com filtros, página de produto e carrinho. O objetivo é demonstrar uma aplicação full-stack com API REST em Spring Boot e SPA em Angular, com foco em código limpo, layout responsivo e boa experiência de uso.

> Loja fictícia, sem venda real de produtos.

## Funcionalidades

| Módulo | Escopo |
|---|---|
| Home | Banner de campanhas, cupons, categorias, ofertas do dia com contador e mais vendidos |
| Busca e listagem | Filtros por categoria, preço, avaliação, marca e frete; ordenação, grade/lista e paginação |
| Produto | Galeria, variações, quantidade, parcelamento, desconto no Pix, frete por CEP, especificações e avaliações |
| Carrinho | Gaveta lateral, cupom de desconto, progresso de frete grátis e resumo do pedido |
| Interface | Layout responsivo (mobile e desktop) e modo escuro |

## Escopo do MVP

A primeira versão cobre o fluxo de **descobrir um produto e montá-lo no carrinho**. O cliente navega sem login e o carrinho fica salvo no navegador.

**Entra no MVP**

- Catálogo de categorias e marcas
- Listagem por categoria e busca por nome
- Filtros por preço, avaliação, marca e frete grátis, com ordenação e paginação
- Página de produto com galeria, variação de cor, preço, parcelamento, valor no Pix, especificações e avaliações
- Carrinho: adicionar, remover, alterar quantidade, total e economia
- Home com categorias, ofertas e mais vendidos
- Layout responsivo

**Fica para depois**

- Login, cadastro e área "Minha conta"
- Favoritos
- Cupons de desconto
- Cálculo de frete por CEP e prazo de entrega
- Checkout, pagamento e pedidos
- Escrever avaliação
- Newsletter
- Modo escuro
- Painel administrativo: dashboard de vendas, cadastro de produtos, categorias, pedidos, clientes e cupons

No MVP, o catálogo é populado por dados de exemplo nas migrations, sem tela de cadastro. Nos mockups, elementos fora do MVP (cupom, CEP, favoritos) podem aparecer na tela sem funcionar ainda.

## Tecnologias

**Backend:** Java 21, Spring Boot 4, Spring Web MVC, Spring Data JPA, Bean Validation, Flyway

**Frontend:** Angular 22 (standalone components), TypeScript, SCSS

**Banco de dados:** PostgreSQL hospedado no Neon

## Estrutura do repositório

```
vitrine/
├── backend/     API REST (vitrine-api)
└── frontend/    Aplicação Angular
```

## Execução local

### Pré-requisitos

- Java 21
- Node.js LTS
- PostgreSQL (local ou Neon)

### Backend

Configure as variáveis de ambiente de conexão:

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC, ex.: `jdbc:postgresql://localhost:5432/vitrine` |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |

```bash
cd backend
./mvnw spring-boot:run
```

As migrations do Flyway são aplicadas na inicialização e o Hibernate apenas valida o schema. A API fica disponível em `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm start
```

A aplicação fica disponível em `http://localhost:4200`.

## Testes

```bash
cd backend && ./mvnw test
cd frontend && npm test
```

## Roadmap

- [x] Estrutura inicial do backend e do frontend
- [ ] Catálogo de produtos e categorias
- [ ] Home e vitrine de ofertas
- [ ] Busca com filtros e paginação
- [ ] Página de produto e avaliações
- [ ] Carrinho e cupons
- [ ] Modo escuro e versão mobile
- [ ] Deploy

## Autoria

Desenvolvido por [Gabrielle Caixeta](https://github.com/caixetss).
