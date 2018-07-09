CREATE TABLE IF NOT EXISTS currency_rates(
    id bigint auto_increment primary key,
    bank varchar(127) not null,
    code varchar(3) not null,
    buy double not null,
    sell double not null,
    unique (bank,code)
);