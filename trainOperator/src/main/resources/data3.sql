-- Insert Trains (data3)
INSERT INTO train (id, name, seats) VALUES (1, 'SLT', 105);
INSERT INTO train (id, name, seats) VALUES (2, 'FLIRT', 125);
INSERT INTO train (id, name, seats) VALUES (3, 'ICM', 155);
INSERT INTO train (id, name, seats) VALUES (4, 'IC Direct', 205);

-- Insert Journeys (data3)
-- Journey 1: Enschede to Zwolle using train SLT (a direct segment alternative)
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (1, 1, 'Enschede', 'Zwolle', TIMESTAMP '2025-03-14 07:45:00', 55, 14.00, 0);

-- Journey 2: Zwolle to Utrecht using train FLIRT
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (2, 2, 'Zwolle', 'Utrecht', TIMESTAMP '2025-03-14 09:30:00', 60, 19.00, 0);

-- Journey 3: Utrecht to Amsterdam using train ICM
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (3, 3, 'Utrecht', 'Amsterdam', TIMESTAMP '2025-03-14 11:00:00', 45, 22.00, 0);

-- Journey 4: Enschede to Hengelo using train IC Direct (extra segment for flexibility)
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (4, 4, 'Enschede', 'Hengelo', TIMESTAMP '2025-03-14 08:00:00', 30, 12.00, 0);
