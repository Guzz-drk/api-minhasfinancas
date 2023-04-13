
CREATE DATABASE minhasfinancas;

CREATE SCHEMA financas;

CREATE TABLE financas.usuario
(
  id bigserial NOT NULL PRIMARY KEY,
  nome varchar(150),
  email varchar(100),
  senha varchar(20),
  data_cadastro date default now()
);

CREATE TABLE financas.lancamento
(
  id bigserial NOT NULL PRIMARY KEY ,
  descricao varchar(100) NOT NULL,
  mes integer NOT NULL,
  ano integer NOT NULL,
  valor numeric(16,2),
  tipo varchar(20) check (tipo in ('RECEITA', 'DESPESA')) NOT NULL,
  status varchar(20) check (status in ('PENDENTE', 'CANCELADO', 'EFETIVADO')) NOT NULL,
  id_usuario bigint REFERENCES financas.usuario (id),
  data_cadastro date default now()
);