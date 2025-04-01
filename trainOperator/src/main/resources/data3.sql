-- Insert Operator
INSERT INTO operator (name, balance) VALUES ('Blauwnet', 10000.00);

-- Insert Trains (data3)
INSERT INTO train (name, seats) VALUES ('SLT', 105);
INSERT INTO train (name, seats) VALUES ('FLIRT', 125);
INSERT INTO train (name, seats) VALUES ('ICM', 155);
INSERT INTO train (name, seats) VALUES ('IC Direct', 205);

-- Insert Journeys (data3)
-- Journey 1: Enschede to Zwolle using train SLT (a direct segment alternative)
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (1, 'Enschede', 'Zwolle', TIMESTAMP '2025-03-14 07:45:00', 55, 14.00, 0, 0);

-- Journey 2: Zwolle to Utrecht using train FLIRT
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (2, 'Zwolle', 'Utrecht', TIMESTAMP '2025-03-14 09:30:00', 60, 19.00, 0, 0);

-- Journey 3: Utrecht to Amsterdam using train ICM
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (3, 'Utrecht', 'Amsterdam', TIMESTAMP '2025-03-14 11:00:00', 45, 22.00, 0, 0);

-- Journey 4: Enschede to Hengelo using train IC Direct (extra segment for flexibility)
INSERT INTO journey (train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats, blocked_seats)
VALUES (4, 'Enschede', 'Hengelo', TIMESTAMP '2025-03-14 08:00:00', 30, 12.00, 0, 0);
