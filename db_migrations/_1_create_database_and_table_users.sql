CREATE DATABASE quakus_social;

CREATE TABLE public."USER" (
	id bigserial NOT NULL primary key,
	name varchar NOT NULL,
	age integer NOT NULL
);