INSERT into country (id,code, flag, name) values (1,'IN','','India');

INSERT into state (id,code,  name, country_code) values (1,'AN',' Andaman and Nicobar Islands', 'IN');

INSERT into city (id,code, name, other_name, state_code,country_code) values(1,'Nicobars','Nicobars','','AN','IN');
INSERT into city (id,code, name, other_name, state_code,country_code) values(2,'North and Middle Andaman','North and Middle Andaman','','AN','IN');
INSERT into city (id,code, name, other_name, state_code,country_code) values(3,'South Andaman','South Andaman','','AN','IN');
INSERT into city (id,code, name, other_name, state_code,country_code) values(4,'Anantapur','Anantapur','','AN','IN');
