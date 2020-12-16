
DROP TABLE IF EXISTS dim_customer;
CREATE TABLE dim_customer (
    id        INTEGER NOT NULL,    -- Surrogate key
    control   VARCHAR(25)     ,   -- Business key
    name      VARCHAR(100),
    date_from DATETIME,
    date_to   DATETIME,
    version   INTEGER,
    PRIMARY KEY (id)
) Engine=InnoDB;
