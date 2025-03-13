-- Table for trains
CREATE TABLE train (
                       id INT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       seats INT NOT NULL
);

-- Table for journeys
CREATE TABLE journey (
                         id INT PRIMARY KEY,
                         train_id INT NOT NULL REFERENCES train(id),
                         departure_station VARCHAR(255) NOT NULL,
                         arrival_station VARCHAR(255) NOT NULL,
                         departure_time TIMESTAMP NOT NULL,
                         travel_time INT NOT NULL, -- duration in minutes
                         price NUMERIC(10, 2) NOT NULL,
                         occupied_seats INT NOT NULL
);
