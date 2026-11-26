CREATE TABLE categoria (
    id               uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    nome             varchar(80)  NOT NULL,
    slug             varchar(80)  NOT NULL UNIQUE,
    icone            varchar(40),
    id_categoria_pai uuid         REFERENCES categoria (id),
    ordem_exibicao   integer      NOT NULL DEFAULT 0,
    eh_inativo       boolean      NOT NULL DEFAULT false,
    criado_em        timestamptz  NOT NULL DEFAULT now(),
    atualizado_em    timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE marca (
    id            uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    nome          varchar(80)  NOT NULL,
    slug          varchar(80)  NOT NULL UNIQUE,
    eh_inativo    boolean      NOT NULL DEFAULT false,
    criado_em     timestamptz  NOT NULL DEFAULT now(),
    atualizado_em timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE produto (
    id               uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    nome             varchar(150)  NOT NULL,
    slug             varchar(160)  NOT NULL UNIQUE,
    descricao        text          NOT NULL,
    id_categoria     uuid          NOT NULL REFERENCES categoria (id),
    id_marca         uuid          NOT NULL REFERENCES marca (id),
    preco            numeric(10,2) NOT NULL,
    preco_original   numeric(10,2),
    eh_frete_gratis  boolean       NOT NULL DEFAULT false,
    avaliacao_media  numeric(2,1)  NOT NULL DEFAULT 0,
    total_avaliacoes integer       NOT NULL DEFAULT 0,
    total_vendidos   integer       NOT NULL DEFAULT 0,
    eh_inativo       boolean       NOT NULL DEFAULT false,
    criado_em        timestamptz   NOT NULL DEFAULT now(),
    atualizado_em    timestamptz   NOT NULL DEFAULT now()
);

CREATE TABLE produto_variacao (
    id            uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    id_produto    uuid         NOT NULL REFERENCES produto (id) ON DELETE CASCADE,
    nome_cor      varchar(40)  NOT NULL,
    cor_hex       char(7)      NOT NULL,
    codigo_estoque varchar(40) NOT NULL UNIQUE,
    estoque       integer      NOT NULL,
    eh_inativo    boolean      NOT NULL DEFAULT false,
    criado_em     timestamptz  NOT NULL DEFAULT now(),
    atualizado_em timestamptz  NOT NULL DEFAULT now(),
    UNIQUE (id_produto, nome_cor)
);

CREATE TABLE produto_imagem (
    id                uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    id_produto        uuid          NOT NULL REFERENCES produto (id) ON DELETE CASCADE,
    url               varchar(500)  NOT NULL,
    texto_alternativo varchar(200)  NOT NULL,
    posicao_galeria   integer       NOT NULL,
    criado_em         timestamptz   NOT NULL DEFAULT now(),
    atualizado_em     timestamptz   NOT NULL DEFAULT now(),
    UNIQUE (id_produto, posicao_galeria)
);

CREATE TABLE produto_especificacao (
    id            uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    id_produto    uuid          NOT NULL REFERENCES produto (id) ON DELETE CASCADE,
    nome          varchar(60)   NOT NULL,
    valor         varchar(200)  NOT NULL,
    ordem_exibicao integer      NOT NULL,
    criado_em     timestamptz   NOT NULL DEFAULT now(),
    atualizado_em timestamptz   NOT NULL DEFAULT now(),
    UNIQUE (id_produto, nome)
);

CREATE TABLE avaliacao (
    id                   uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    id_produto           uuid          NOT NULL REFERENCES produto (id) ON DELETE CASCADE,
    nome_autor           varchar(80)   NOT NULL,
    nota                 integer       NOT NULL,
    titulo               varchar(120)  NOT NULL,
    comentario           text,
    eh_compra_verificada boolean       NOT NULL DEFAULT false,
    criado_em            timestamptz   NOT NULL DEFAULT now(),
    atualizado_em        timestamptz   NOT NULL DEFAULT now()
);
