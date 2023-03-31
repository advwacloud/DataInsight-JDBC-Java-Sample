# etl-api

## Swagger
### Install Swag command line tool(v1.7.4)
- `go get -u github.com/swaggo/swag/cmd/swag@v1.7.4`
### Generate
- `swag init`

## Migration
- https://github.com/golang-migrate/migrate

### Install
- https://github.com/golang-migrate/migrate/releases/tag/v4.14.1

### Create a migration
1. `migrate create -ext sql -dir ./migration/sql -seq $MIGRATION_NAME`
2. write migration logic in `.up.sql`
3. write db rollback logic in `.down.sql`

### Migrate
1. Migrate to latest
  - `migrate -database "postgres://$USER_NAME:$PASS_WORD@$HOST:$PORT/$DATABASE?sslmode=disable&search_path=$SCHEMA_NAME" -path ./migration/sql up`
2. Migrate 2 migrations from now
  - `migrate -database "postgres://$USER_NAME:$PASS_WORD@$HOST:$PORT/$DATABASE?sslmode=disable&search_path=$SCHEMA_NAME" -path ./migration/sql up 2`

### Rollback
1. Migrate 2 migrations from now
  - `migrate -database "postgres://$USER_NAME:$PASS_WORD@$HOST:$PORT/$DATABASE?sslmode=disable&search_path=$SCHEMA_NAME" -path ./migration/sql down 2`
2. Migrate to very first(careful)
  - `migrate -database "postgres://$USER_NAME:$PASS_WORD@$HOST:$PORT/$DATABASE?sslmode=disable&search_path=$SCHEMA_NAME" -path ./migration/sql down`

### Dealing with dirty migration
1. fix the sql file
2. `migrate -database "postgres://$USER_NAME:$PASS_WORD@$HOST:$PORT/$DATABASE?sslmode=disable&search_path=$SCHEMA_NAME" -path ./migration/sql force $DIRTY_VERSION_NAME`
