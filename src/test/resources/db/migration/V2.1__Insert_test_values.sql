DROP TABLE IF EXISTS currency_rates;

CREATE TABLE IF NOT EXISTS currency_rates(
  id bigint auto_increment primary key,
  bank varchar(127) not null,
  code varchar(3) not null,
  buy double not null,
  sell double not null,
  unique (bank,code)
);

INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('aval','EUR',30.3,30.8);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('aval','USD',25.8,26.2);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('aval','RUB',0.414,0.424);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('aval','CHF',25.8,26.45);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('aval','GBP',34.4,35.0);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('otp','USD',25.9,26.1);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('otp','CHF',25.75,26.35);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('otp','EUR',30.2,30.75);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('pumb','USD',26.0,26.2);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('pumb','RUB',0.41,0.42);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('pumb','EUR',30.5,30.9);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('pumb','CHF',25.5,26.3);
INSERT INTO currency_rates(bank, code, buy, sell) VALUES ('pumb','GBP',34.3,35.0);