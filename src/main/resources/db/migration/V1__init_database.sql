DROP TABLE IF EXISTS beer;
DROP TABLE IF EXISTS customer;

-- Create Beer table
CREATE TABLE beer (
                      id CHAR(36) PRIMARY KEY,
                      version BIGINT,
                      beer_name VARCHAR(255),
                      beer_style VARCHAR(50),
                      upc VARCHAR(255),
                      quantity_on_hand INTEGER,
                      price DECIMAL(19,2),
                      created_date TIMESTAMP,
                      last_modified_date TIMESTAMP
);

-- Create index on beer_name
CREATE INDEX idx_beer_name ON beer (beer_name);

-- Create Customer table
CREATE TABLE customer (
                          id CHAR(36) PRIMARY KEY,
                          name VARCHAR(255)
);

-- Create index on customer name
CREATE INDEX idx_customer_name ON customer (name);