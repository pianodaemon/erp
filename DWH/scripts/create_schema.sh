#!/bin/sh -e


CONTAINER="rdbms_staging"
export MYSQL_PWD=$MYSQL_ROOT_PASSWORD

echo "Creating database..."
echo "DROP DATABASE IF EXISTS $MYSQL_DATABASE; CREATE DATABASE $MYSQL_DATABASE;" | mysql -u root

cd /migrations

echo "Creating time dimension"
# Creates the dimension along with its data
mysql $MYSQL_DATABASE -u root < dim_time.sql

echo "Creating geographic dimension"
# Creates the dimension along with its data
mysql $MYSQL_DATABASE -u root < dim_geo.sql

echo "Creating customer dimension"
# Creates the dimension
mysql $MYSQL_DATABASE -u root < dim_customer.sql

echo "Creating product dimension"
# Creates the dimension
mysql $MYSQL_DATABASE -u root < dim_product.sql

echo "Creating fact table for sales"
mysql $MYSQL_DATABASE -u root < fact_sales.sql
