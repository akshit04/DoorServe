### General Info:
1. Port: [http://localhost:8080/](http://localhost:8080/)
2. database name: doorserve
3. db user: akshit_kharbanda

### Commands:
1. Build: `mvn clean install`
2. Run the application: `mvn spring-boot:run`

### PostgreSql server:
 1. Start psql server - `brew services start postgresql`
 2. Stop psql server - `brew services stop postgresql`
 3. Restart psql server - `brew services restart postgresql`
 4. Login as root - `psql postgres`
 5. Login as a user - `postgresql -u door-serve-test-user -p -h localhost`
    1. pswd: `thats-what-she-said`

### PostgreSql Commands:
 - Exit psql - `\q`
 - Show users - `\du`
 - Show database - `\l`
 - Show tables:
   - connect to the database - `\c database_name`
   - show tables - `\dt`
 - Check table schema - `\d+ tablename`
 - Create database - `CREATE DATABASE doorserve;`
 - Grant postgresql-user permission:
    - `GRANT ALL PRIVILEGES ON example_db.* TO 'door-serve-test-user'@'localhost';`
    - `FLUSH PRIVILEGES`
    - Grant for all databases - `GRANT ALL PRIVILEGES ON *.* TO 'door-serve-test-user'@'localhost';`
 - Create TABLE:
```
CREATE TABLE user (
  document_id VARCHAR(36) UNIQUE  PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  first_name VARCHAR(20) NOT NULL,
  last_name VARCHAR(20) NOT NULL,
  password VARCHAR(25) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```
 - Delete tables - `DROP TABLE table_1, table_2;`
 - NEXT_COMMAND