-- Insert Trains
INSERT INTO train (id, name, seats) VALUES (1, 'SLT', 100);
INSERT INTO train (id, name, seats) VALUES (2, 'FLIRT', 120);
INSERT INTO train (id, name, seats) VALUES (3, 'ICM', 150);
INSERT INTO train (id, name, seats) VALUES (4, 'IC Direct', 200);

-- Insert Journeys
-- Journey 1: Enschede to Hengelo using train SLT
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (1, 1, 'Enschede', 'Hengelo', TIMESTAMP '2025-03-14 08:00:00', 30, 12.50, 0);

-- Journey 2: Hengelo to Zwolle using train FLIRT
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (2, 2, 'Hengelo', 'Zwolle', TIMESTAMP '2025-03-14 09:00:00', 45, 15.00, 0);

-- Journey 3: Zwolle to Utrecht using train ICM
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (3, 3, 'Zwolle', 'Utrecht', TIMESTAMP '2025-03-14 10:30:00', 60, 20.00, 0);

-- Journey 4: Enschede to Utrecht using train IC Direct
INSERT INTO journey (id, train_id, departure_station, arrival_station, departure_time, travel_time, price, occupied_seats)
VALUES (4, 4, 'Enschede', 'Utrecht', TIMESTAMP '2025-03-14 07:30:00', 90, 25.00, 0);
