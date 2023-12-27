INSERT INTO public.tb_category (name, description, dh_insert)
VALUES ('Bebidas', 'Consumível líquido que possa ser comprado para consumo, como Água, Refrigerante, Cerveja entre outros.', now())
ON CONFLICT DO NOTHING;

INSERT INTO public.tb_category (name, description, dh_insert)
VALUES ('Sobremesa', 'Algum doce, pode incluir bolos, tortas, sorvetes, pudins, entre outros.', now())
ON CONFLICT DO NOTHING;

INSERT INTO public.tb_category (name, description, dh_insert)
VALUES ('Lanche', 'Refeição pronta para consumo que possa ser comprada como Hambuguer, Pizza, Sanduiche entre outros.', now())
ON CONFLICT DO NOTHING;

INSERT INTO public.tb_category (name, description, dh_insert)
VALUES ('Acompanhamento', 'Todo e qualquer alimento de pequena média proporção que possa ser consumido com outras refeições.', now())
ON CONFLICT DO NOTHING;

INSERT INTO tb_product (name, price, quantity, id_category)
VALUES ('Suco', 19.99, 10, 1),
('Sorvete', 29.99, 5, 2),
('Hamburguer', 39.99, 15, 3),
('Batata Frita', 9.99, 15, 4);

INSERT INTO tb_address (street, city, state, postal_code)
VALUES ('123 Main St', 'Springfield', 'IL', '12345');

INSERT INTO tb_client (password, email, cpf, name, id_address)
VALUES ('c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 'client@example.com', '99999999999', 'admin', 1);
