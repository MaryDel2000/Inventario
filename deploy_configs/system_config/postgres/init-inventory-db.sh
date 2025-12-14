#!/bin/bash
set -e

if [ -n "$INVENTORY_DB_NAME" ] && [ -n "$INVENTORY_DB_USER" ] && [ -n "$INVENTORY_DB_PASSWORD" ]; then
	echo "Creating database: $INVENTORY_DB_NAME"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
		CREATE USER $INVENTORY_DB_USER WITH PASSWORD '$INVENTORY_DB_PASSWORD';
		CREATE DATABASE $INVENTORY_DB_NAME;
		GRANT ALL PRIVILEGES ON DATABASE $INVENTORY_DB_NAME TO $INVENTORY_DB_USER;
	EOSQL
	echo "Database $INVENTORY_DB_NAME created successfully"
else
	echo "INVENTORY_DB variables not set, skipping creation"
fi
