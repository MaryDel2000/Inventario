#!/bin/bash
set -e

if [ -n "$APP_DB_NAME" ] && [ -n "$APP_DB_USER" ] && [ -n "$APP_DB_PASSWORD" ]; then
	echo "Creating database: $APP_DB_NAME"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
		CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASSWORD';
		CREATE DATABASE $APP_DB_NAME;
		GRANT ALL PRIVILEGES ON DATABASE $APP_DB_NAME TO $APP_DB_USER;
	EOSQL
	echo "Database $APP_DB_NAME created successfully"
else
	echo "APP_DB variables not set, skipping creation"
fi
