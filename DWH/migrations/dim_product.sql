
DROP TABLE IF EXISTS dim_product;
CREATE TABLE dim_product (
    id             INTEGER NOT NULL,   -- Surrogate key
    sku            VARCHAR(9),            -- Business key
    description    VARCHAR(255),
    date_from DATETIME,
    date_to   DATETIME,
    version   INTEGER,
    PRIMARY KEY (id)
) Engine=InnoDB;
