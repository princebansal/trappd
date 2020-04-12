INSERT into country (id, code, flag, name)
values (1, 'IN', '', 'India');

INSERT into state (id, code, name, country_code)
values (1, 'AN', ' Andaman and Nicobar Islands', 'IN');

INSERT into city (id, code, name, other_name, state_code, country_code)
values (1, 'Nicobars', 'Nicobars', '', 'AN', 'IN');
INSERT into city (id, code, name, other_name, state_code, country_code)
values (2, 'North and Middle Andaman', 'North and Middle Andaman', '', 'AN', 'IN');
INSERT into city (id, code, name, other_name, state_code, country_code)
values (3, 'South Andaman', 'South Andaman', '', 'AN', 'IN');
INSERT into city (id, code, name, other_name, state_code, country_code)
values (4, 'Anantapur', 'Anantapur', '', 'AN', 'IN');


INSERT into covid_case(announced_date, patient_number, status, city_id,
                       country_code,
                       state_code) values ('2020-04-11', 'p1', 'ACTIVE', 1, 'IN', 'AN');
INSERT into covid_case(announced_date, patient_number, status, city_id,
                       country_code,
                       state_code) values ('2020-04-11', 'p2', 'RECOVERED', 1, 'IN', 'AN');
INSERT into covid_case(announced_date, patient_number, status, city_id,
                       country_code,
                       state_code) values ('2020-04-11', 'p3', 'DECEASED', 1, 'IN', 'AN');
INSERT into covid_case(announced_date, patient_number, status, city_id,
                       country_code,
                       state_code) values ('2020-04-11', 'p4', 'ACTIVE', 1, 'IN', 'AN');