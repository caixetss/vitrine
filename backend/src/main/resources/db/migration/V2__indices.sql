CREATE INDEX idx_produto_categoria ON produto (id_categoria);
CREATE INDEX idx_produto_marca ON produto (id_marca);
CREATE INDEX idx_produto_preco ON produto (preco);
CREATE INDEX idx_produto_avaliacao_media ON produto (avaliacao_media);
CREATE INDEX idx_produto_total_vendidos ON produto (total_vendidos);

CREATE INDEX idx_categoria_pai ON categoria (id_categoria_pai);
CREATE INDEX idx_produto_variacao_produto ON produto_variacao (id_produto);
CREATE INDEX idx_produto_especificacao_produto ON produto_especificacao (id_produto);
CREATE INDEX idx_avaliacao_produto_recentes ON avaliacao (id_produto, criado_em DESC);
