-- Insert Operator
INSERT INTO operator (name, balance) VALUES ('NS', 10000.00);

-- Insert Trains
INSERT INTO train (name, seats) VALUES ('SLT', 100);
INSERT INTO train (name, seats) VALUES ('FLIRT', 120);
INSERT INTO train (name, seats) VALUES ('ICM', 150);
INSERT INTO train (name, seats) VALUES ('IC Direct', 200);

-- Insert Journeys
-- Journey 1: Enschede to Hengelo using train SLT
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (1, 'Enschede', 'Hengelo', TIMESTAMP '2025-03-14 08:00:00', 30, 12.50, 0, 0);

-- Journey 2: Hengelo to Zwolle using train FLIRT
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (2, 'Hengelo', 'Zwolle', TIMESTAMP '2025-03-14 09:00:00', 45, 15.00, 0, 0);

-- Journey 3: Zwolle to Utrecht using train ICM
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (3, 'Zwolle', 'Utrecht', TIMESTAMP '2025-03-14 10:30:00', 60, 20.00, 0, 0);

-- Journey 4: Enschede to Utrecht using train IC Direct
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (4, 'Enschede', 'Utrecht', TIMESTAMP '2025-03-14 07:30:00', 90, 25.00, 0, 0);
