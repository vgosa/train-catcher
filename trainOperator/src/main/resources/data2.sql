-- Insert Trains (data2)
INSERT INTO train (id, name, seats) VALUES (1, 'SLT', 110);
INSERT INTO train (id, name, seats) VALUES (2, 'FLIRT', 130);
INSERT INTO train (id, name, seats) VALUES (3, 'ICM', 140);
INSERT INTO train (id, name, seats) VALUES (4, 'IC Direct', 210);

-- Insert Journeys (data2)
-- Journey 1: Enschede to Hengelo using train SLT
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (1, 1, 'Enschede', 'Hengelo', TIMESTAMP '2025-03-14 08:15:00', 35, 13.00, 0);

-- Journey 2: Hengelo to Zwolle using train FLIRT
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (2, 2, 'Hengelo', 'Zwolle', TIMESTAMP '2025-03-14 09:10:00', 50, 16.00, 0);

-- Journey 3: Zwolle to Utrecht using train ICM
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (3, 3, 'Zwolle', 'Utrecht', TIMESTAMP '2025-03-14 10:45:00', 65, 21.00, 0);

-- Journey 4: Utrecht to Amsterdam using train IC Direct
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (4, 4, 'Utrecht', 'Amsterdam', TIMESTAMP '2025-03-14 12:00:00', 40, 18.00, 0);
