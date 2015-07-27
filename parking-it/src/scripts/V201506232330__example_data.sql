-- insert users
INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('admin', null, null, 'admin@mail.com', 'John', 'Admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 0, 1);
INSERT INTO user_groups VALUES ('admin', 'ADMIN');

INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('user', null, null, 'user@mail.com', 'Tony', 'User', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 0, 1);
INSERT INTO user_groups VALUES ('user', 'USER');

-- insert parkings
INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('1', 10, 'Katowice Francuska', 1, 'Katowice', '11', '40-507', 'Francuska');

INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('2', 20, 'Gliwice', 1, 'Gliwice', '7', '44-100', 'Tarnogórska');

INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('3', 20, 'Katowice Pl. Szewczyka', 1, 'Katowice', '7', '40-602', 'Plac Szewczyka');

INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('4', 200, 'Rybnik Plaza', 1, 'Rybnik', '16', '44-200', 'Raciborska');

-- assign users to parkings
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (1, 0, 1, 0, '1', 'user');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (2, 0, 1, 0, '2', 'user');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (3, 0, 1, 0, '3', 'user');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (4, 0, 1, 0, '4', 'user');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (5, 0, 1, 1, '1', 'admin');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (6, 0, 1, 1, '2', 'admin');

-- update records sequence
UPDATE sequence SET seq_count=100 WHERE seq_name='seq_records';
