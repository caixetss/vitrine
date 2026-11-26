INSERT INTO categoria (nome, slug, icone, ordem_exibicao) VALUES
    ('Celulares',      'celulares',      'smartphone', 1),
    ('Informática',    'informatica',    'laptop',     2),
    ('TVs e vídeo',    'tvs-e-video',    'tv',         3),
    ('Áudio',          'audio',          'headphones', 4),
    ('Casa e cozinha', 'casa-e-cozinha', 'coffee',     5),
    ('Moda',           'moda',           'shirt',      6),
    ('Esportes',       'esportes',       'dumbbell',   7),
    ('Beleza',         'beleza',         'sparkles',   8),
    ('Games',          'games',          'gamepad-2',  9),
    ('Livros',         'livros',         'book',       10);

INSERT INTO categoria (nome, slug, id_categoria_pai, ordem_exibicao)
SELECT s.nome, s.slug, pai.id, s.ordem_exibicao
FROM (VALUES
    ('celulares',      'Smartphones',          'smartphones',          1),
    ('celulares',      'Smartwatches',         'smartwatches',         2),
    ('informatica',    'Notebooks',            'notebooks',            1),
    ('informatica',    'Monitores',            'monitores',            2),
    ('informatica',    'Periféricos',          'perifericos',          3),
    ('tvs-e-video',    'Smart TVs',            'smart-tvs',            1),
    ('tvs-e-video',    'Projetores',           'projetores',           2),
    ('audio',          'Fones de ouvido',      'fones-de-ouvido',      1),
    ('audio',          'Caixas de som',        'caixas-de-som',        2),
    ('audio',          'Headsets gamer',       'headsets-gamer',       3),
    ('audio',          'Soundbars',            'soundbars',            4),
    ('casa-e-cozinha', 'Eletroportáteis',      'eletroportateis',      1),
    ('casa-e-cozinha', 'Panelas e utensílios', 'panelas-e-utensilios', 2),
    ('moda',           'Camisetas',            'camisetas',            1),
    ('moda',           'Mochilas e bolsas',    'mochilas-e-bolsas',    2),
    ('esportes',       'Fitness',              'fitness',              1),
    ('esportes',       'Corrida',              'corrida',              2),
    ('beleza',         'Cabelos',              'cabelos',              1),
    ('beleza',         'Skincare',             'skincare',             2),
    ('games',          'Consoles',             'consoles',             1),
    ('games',          'Acessórios gamer',     'acessorios-gamer',     2),
    ('livros',         'Tecnologia',           'tecnologia',           1),
    ('livros',         'Ficção',               'ficcao',               2)
) AS s (slug_pai, nome, slug, ordem_exibicao)
JOIN categoria pai ON pai.slug = s.slug_pai;

INSERT INTO marca (nome, slug) VALUES
    ('Sonora', 'sonora'), ('Vibe', 'vibe'), ('Kairo', 'kairo'), ('Arcade', 'arcade'),
    ('Lumi', 'lumi'), ('Nexa', 'nexa'), ('Orion', 'orion'), ('Aroma', 'aroma'),
    ('Visora', 'visora'), ('Trek', 'trek'), ('Página Editora', 'pagina-editora');

CREATE TEMP TABLE carga_produto (
    nome           varchar(150),
    slug_categoria varchar(80),
    slug_marca     varchar(80),
    preco          numeric(10,2),
    preco_original numeric(10,2),
    frete_gratis   boolean,
    media_alvo                numeric(2,1),
    total_avaliacoes_alvo     integer
) ON COMMIT DROP;

INSERT INTO carga_produto VALUES
    ('Fone de Ouvido Bluetooth Sonora Pulse ANC com Cancelamento de Ruído', 'fones-de-ouvido', 'sonora', 279.90, 449.90, true, 4.7, 2384),
    ('Fone sem Fio TWS Sonora Buds Lite com Estojo de Carga', 'fones-de-ouvido', 'sonora', 129.90, 179.90, true, 4.5, 4019),
    ('Fone sem Fio TWS Sonora Buds Pro ANC', 'fones-de-ouvido', 'sonora', 349.90, 499.90, true, 4.6, 987),
    ('Fone Bluetooth Esportivo Kairo Run à Prova de Suor', 'fones-de-ouvido', 'kairo', 149.90, NULL, true, 4.4, 652),
    ('Headphone Monitor Pro X1 para Estúdio e Gravação', 'fones-de-ouvido', 'sonora', 449.90, 529.90, true, 4.8, 418),
    ('Fone Over-ear Sonora Studio com 40h de Bateria', 'fones-de-ouvido', 'sonora', 329.90, NULL, true, 4.5, 1043),
    ('Fone com Fio Sonora Basic P2 com Microfone', 'fones-de-ouvido', 'sonora', 39.90, NULL, false, 4.3, 6881),
    ('Fone Infantil Tico com Limitador de Volume', 'fones-de-ouvido', 'vibe', 89.90, 109.90, true, 4.7, 512),
    ('Caixa de Som Bluetooth Vibe Go 20W à Prova d''Água', 'caixas-de-som', 'vibe', 199.90, 259.90, true, 4.7, 3562),
    ('Caixa de Som Vibe Party 60W com Luzes RGB', 'caixas-de-som', 'vibe', 449.00, 549.00, true, 4.5, 734),
    ('Caixa de Som Smart Lumi Mini com Assistente de Voz', 'caixas-de-som', 'lumi', 299.00, 399.00, true, 4.6, 2107),
    ('Headset Gamer Arcade Storm 7.1 com Microfone', 'headsets-gamer', 'arcade', 239.90, NULL, true, 4.6, 1288),
    ('Soundbar Visora 2.1 com Subwoofer Bluetooth', 'soundbars', 'visora', 899.00, 1099.00, true, 4.5, 376),
    ('Smartphone Nexa S24 128GB, 8GB RAM, Tela 6,6"', 'smartphones', 'nexa', 1599.00, 1999.00, true, 4.8, 5102),
    ('Smartphone Nexa A15 64GB, 4GB RAM, Tela 6,5"', 'smartphones', 'nexa', 899.00, 1099.00, true, 4.4, 2210),
    ('Smartphone Orion X Pro 256GB, 12GB RAM, Câmera 108MP', 'smartphones', 'orion', 2799.00, 3299.00, true, 4.7, 830),
    ('Smartwatch Kairo Fit 3 com GPS e Monitor Cardíaco', 'smartwatches', 'kairo', 349.90, 499.90, true, 4.6, 1877),
    ('Smartwatch Lumi Band 2 com Monitor de Sono', 'smartwatches', 'lumi', 199.90, NULL, true, 4.3, 964),
    ('Notebook Orion 15 Ryzen 5, 16GB RAM, SSD 512GB', 'notebooks', 'orion', 3299.00, 3899.00, true, 4.7, 612),
    ('Notebook Nexa Book 14 Intel Core i5, 8GB RAM, SSD 256GB', 'notebooks', 'nexa', 2599.00, 2999.00, true, 4.5, 447),
    ('Monitor Orion 24" Full HD IPS 75Hz', 'monitores', 'orion', 749.90, 899.90, true, 4.6, 1320),
    ('Mouse sem Fio Arcade Glide 1600 DPI', 'perifericos', 'arcade', 79.90, 99.90, true, 4.5, 3105),
    ('Teclado Mecânico Arcade Strike RGB ABNT2', 'perifericos', 'arcade', 299.90, 379.90, true, 4.7, 1544),
    ('Smart TV 50" 4K Visora UHD com Wi-Fi', 'smart-tvs', 'visora', 2149.00, 2699.00, true, 4.7, 2031),
    ('Smart TV 32" HD Visora com Android TV', 'smart-tvs', 'visora', 1099.00, 1299.00, true, 4.4, 2760),
    ('Projetor Visora Cine Mini Full HD', 'projetores', 'visora', 1299.00, NULL, true, 4.2, 298),
    ('Cafeteira Elétrica Aroma Plus 30 Xícaras Inox', 'eletroportateis', 'aroma', 159.90, 219.90, true, 4.8, 943),
    ('Airfryer Aroma Crisp 4L Antiaderente', 'eletroportateis', 'aroma', 349.90, 449.90, true, 4.7, 4380),
    ('Jogo de Panelas Aroma Chef 5 Peças Antiaderente', 'panelas-e-utensilios', 'aroma', 279.90, 359.90, true, 4.6, 1208),
    ('Camiseta Básica Algodão Pima Unissex', 'camisetas', 'trek', 59.90, 79.90, false, 4.8, 8774),
    ('Mochila Urbana Trek 25L Impermeável com Porta-notebook', 'mochilas-e-bolsas', 'trek', 129.90, 189.90, true, 4.7, 3210),
    ('Kit Halteres Emborrachados 2 x 5 kg', 'fitness', 'trek', 119.90, NULL, true, 4.7, 1120),
    ('Tapete de Yoga Lumi 6mm Antiderrapante', 'fitness', 'lumi', 89.90, 119.90, true, 4.5, 870),
    ('Tênis de Corrida Trek Run Leve com Amortecimento', 'corrida', 'trek', 299.90, 399.90, true, 4.6, 1655),
    ('Garrafa Térmica Trek 750ml Inox', 'corrida', 'trek', 69.90, NULL, false, 4.8, 2290),
    ('Secador de Cabelo Lumi Pro 2000W com Íons', 'cabelos', 'lumi', 189.90, 249.90, true, 4.5, 1432),
    ('Kit Skincare Lumi Glow Vitamina C com 3 Itens', 'skincare', 'lumi', 119.90, 159.90, true, 4.6, 2018),
    ('Controle sem Fio Arcade Pro para PC e Console', 'acessorios-gamer', 'arcade', 219.90, 279.90, true, 4.6, 1455),
    ('Console Portátil Arcade Go 32GB com 20 Jogos', 'consoles', 'arcade', 1199.00, 1399.00, true, 4.4, 512),
    ('Livro Algoritmos para Iniciantes: Edição Ilustrada', 'tecnologia', 'pagina-editora', 89.90, 119.90, false, 4.9, 640),
    ('Livro Contos da Cidade Vitrine', 'ficcao', 'pagina-editora', 49.90, NULL, false, 4.5, 215);

INSERT INTO produto (nome, slug, descricao, id_categoria, id_marca, preco, preco_original, eh_frete_gratis)
SELECT c.nome,
       trim(BOTH '-' FROM regexp_replace(translate(lower(c.nome), 'áàâãéêíóôõúç', 'aaaaeeiooouc'), '[^a-z0-9]+', '-', 'g')),
       c.nome || '. Produto de exemplo da loja fictícia Vitrine, com garantia de 12 meses e troca grátis em até 7 dias.',
       cat.id, m.id, c.preco, c.preco_original, c.frete_gratis
FROM carga_produto c
JOIN categoria cat ON cat.slug = c.slug_categoria
JOIN marca m ON m.slug = c.slug_marca;

INSERT INTO produto_variacao (id_produto, nome_cor, cor_hex, codigo_estoque, estoque)
SELECT p.id, cor.nome, cor.hex,
       'VT-' || upper(left(md5(p.slug), 8)) || '-' || cor.sigla,
       CASE WHEN p.slug LIKE 'projetor-%' THEN 0 ELSE 3 + abs(hashtext(p.slug || cor.nome)) % 48 END
FROM produto p
JOIN (VALUES
    ('Preto', '#111827', 'PRT', 1),
    ('Branco', '#F9FAFB', 'BRC', 2),
    ('Azul', '#4F46E5', 'AZL', 3)
) AS cor (nome, hex, sigla, posicao)
    ON cor.posicao = 1 OR abs(hashtext(p.slug)) % 3 >= cor.posicao - 1;

INSERT INTO produto_imagem (id_produto, url, texto_alternativo, posicao_galeria)
SELECT p.id,
       'https://placehold.co/800x800/EEF2FF/4F46E5?text=' || replace(m.nome, ' ', '+') || '+' || (numero_imagem + 1),
       p.nome || ', imagem ' || (numero_imagem + 1),
       numero_imagem
FROM produto p
JOIN marca m ON m.id = p.id_marca
CROSS JOIN generate_series(0, 3) AS numero_imagem;

INSERT INTO produto_especificacao (id_produto, nome, valor, ordem_exibicao)
SELECT p.id, e.nome, e.valor, e.ordem_exibicao
FROM produto p
JOIN categoria sub ON sub.id = p.id_categoria
JOIN categoria pai ON pai.id = sub.id_categoria_pai
JOIN (VALUES
    ('celulares',      'Conectividade', 'Wi-Fi, Bluetooth 5.3 e 5G', 1),
    ('celulares',      'Bateria',       'Até 2 dias de uso',         2),
    ('informatica',    'Conexão',       'USB-C e Bluetooth',         1),
    ('informatica',    'Voltagem',      'Bivolt',                    2),
    ('tvs-e-video',    'Resolução',     'Varia conforme o modelo',   1),
    ('tvs-e-video',    'Voltagem',      'Bivolt',                    2),
    ('audio',          'Conexão',       'Bluetooth 5.3',             1),
    ('audio',          'Bateria',       'Até 40 h',                  2),
    ('casa-e-cozinha', 'Voltagem',      '127 V',                     1),
    ('casa-e-cozinha', 'Material',      'Aço inox',                  2),
    ('moda',           'Material',      'Algodão e poliéster',       1),
    ('moda',           'Lavagem',       'Máquina, água fria',        2),
    ('esportes',       'Material',      'Borracha e aço',            1),
    ('esportes',       'Indicação',     'Uso doméstico e academia',  2),
    ('beleza',         'Indicação',     'Todos os tipos de pele e cabelo', 1),
    ('beleza',         'Testado',       'Dermatologicamente',        2),
    ('games',          'Compatível com', 'PC e consoles',            1),
    ('games',          'Conexão',       'Sem fio 2.4 GHz',           2),
    ('livros',         'Páginas',       '320',                       1),
    ('livros',         'Idioma',        'Português',                 2)
) AS e (slug_pai, nome, valor, ordem_exibicao) ON e.slug_pai = pai.slug
UNION ALL
SELECT p.id, 'Garantia', '12 meses', 3 FROM produto p;

-- notas 2 e 3 em 10% das avaliações; o resto entre 4 e 5, com a proporção de 5 calculada para chegar perto da média alvo
INSERT INTO avaliacao (id_produto, nome_autor, nota, titulo, comentario, eh_compra_verificada, criado_em)
SELECT p.id,
       (ARRAY['Mariana S.', 'João P.', 'Ana L.', 'Carlos M.', 'Beatriz R.', 'Rafael T.', 'Juliana C.', 'Pedro H.', 'Camila F.', 'Lucas A.'])[1 + numero_avaliacao % 10],
       nota_gerada.nota,
       (ARRAY['Não gostei', 'Deixou a desejar', 'Razoável', 'Bom custo-benefício', 'Excelente, recomendo'])[nota_gerada.nota],
       CASE WHEN numero_avaliacao % 3 = 0 THEN NULL
            ELSE (ARRAY['Chegou com defeito e a troca demorou.',
                        'Funciona, mas esperava mais pelo preço.',
                        'Atende o básico, nada além disso.',
                        'Chegou rápido e funciona bem. Vale o que custa.',
                        'Superou minhas expectativas. Qualidade ótima e entrega antes do prazo.'])[nota_gerada.nota]
       END,
       numero_avaliacao % 5 <> 0,
       now() - (numero_avaliacao % 365) * interval '1 day' - (numero_avaliacao % 24) * interval '1 hour'
FROM carga_produto c
JOIN produto p ON p.nome = c.nome
CROSS JOIN generate_series(1, c.total_avaliacoes_alvo) AS numero_avaliacao
CROSS JOIN LATERAL (
    SELECT CASE
               WHEN numero_avaliacao % 20 = 0 THEN 2
               WHEN numero_avaliacao % 20 = 1 THEN 3
               WHEN (numero_avaliacao * 37) % 100 < (c.media_alvo - 3.85) / 0.9 * 100 THEN 5
               ELSE 4
           END AS nota
) AS nota_gerada;

UPDATE produto p
SET avaliacao_media  = resumo.media,
    total_avaliacoes = resumo.total,
    total_vendidos   = resumo.total * 3 + abs(hashtext(p.slug)) % 500
FROM (
    SELECT id_produto, round(avg(nota), 1) AS media, count(*) AS total
    FROM avaliacao
    GROUP BY id_produto
) resumo
WHERE resumo.id_produto = p.id;
