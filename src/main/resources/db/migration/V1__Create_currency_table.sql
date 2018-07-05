CREATE TABLE IF NOT EXISTS currency_rates(
    bank varchar(127) not null,
    code varchar(3) not null,
    buy double not null,
    sell double not null,
    primary key(bank,code)
);