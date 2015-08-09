-- insert users
INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('admin', null, null, 'admin@mail.com', 'John', 'Admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 0, 1);
INSERT INTO user_groups VALUES ('admin', 'ADMIN');
INSERT INTO user_groups VALUES ('admin', 'USER');

INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('user', null, null, 'user@mail.com', 'Tony', 'User', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 0, 1);
INSERT INTO user_groups VALUES ('user', 'USER');

INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('donald', null, null, 'donald@mail.com', 'Donald', 'Duck', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 0, 1);
INSERT INTO user_groups VALUES ('donald', 'USER');

INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('mickey', null, null, 'mickey@mail.com', 'Mickey', 'Mouse', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 0, 1);
INSERT INTO user_groups VALUES ('mickey', 'USER');

INSERT INTO users (LOGIN, ACTIVATIONDEADLINE, ACTIVATIONUUID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, REMOVED, VERSION) 
VALUES ('speedy', null, null, 'speedy@mail.com', 'Speedy', 'Gonzalez', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 0, 1);
INSERT INTO user_groups VALUES ('speedy', 'USER');

-- insert parkings
INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('1', 10, 'Houston Plaza', 1, 'Houston', '11', '22876', 'Louis Armstrong St.');

INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('2', 20, 'New York North', 1, 'New York', '7', '19885', 'Acme St.');

INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('3', 20, 'New York Essex', 1, 'New York', '12', '8713', 'Die Hard Blvd.');

INSERT INTO parkings (UUID, CAPACITY, NAME, VERSION, CITY, NUMBER, POSTALCODE, STREET)
VALUES ('4', 200, 'Los Angeles', 1, 'Los Angeles', '16', '18735', 'Main St.');

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
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (7, 0, 1, 0, '3', 'donald');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (8, 0, 1, 0, '4', 'donald');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (9, 0, 1, 0, '1', 'mickey');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (10, 0, 1, 0, '2', 'mickey');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (11, 0, 1, 0, '3', 'mickey');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (12, 0, 1, 0, '4', 'mickey');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (13, 0, 1, 0, '2', 'speedy');
INSERT INTO records (ID, POINTS, VERSION, VIP, parking_uuid, login) 
VALUES (14, 0, 1, 0, '3', 'speedy');

-- update records sequence
UPDATE sequence SET seq_count=100 WHERE seq_name='seq_records';
