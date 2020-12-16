
DROP TABLE IF EXISTS dim_geo;
CREATE TABLE dim_geo(
    id INTEGER NOT NULL PRIMARY KEY,        -- Surrogate/Business key
    location_name VARCHAR(100),
    state_id INTEGER,
    state_name VARCHAR(100),
    country_id INTEGER,
    country_name VARCHAR(50),
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

LOAD DATA LOCAL INFILE './dim_geo.csv'
INTO TABLE dim_geo
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;
