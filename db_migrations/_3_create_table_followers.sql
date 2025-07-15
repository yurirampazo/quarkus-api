CREATE DATABASE quakus_social;

create table followers(
	id bigserial not null primary key,
	user_id bigint not null references users(id),
	follower_id bigint not null references users(id)
);
