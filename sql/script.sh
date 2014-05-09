


sudo -u postgres psql postgres

sudo -u postgres createuser root

sudo createdb readLogs

sudo psql readLogs

SELECT datname FROM pg_database WHERE datistemplate = false;


CREATE USER reader_logs WITH PASSWORD 'temporal1';

CREATE SCHEMA CONTAINER_LOGS_DATA  AUTHORIZATION reader_logs;


-- CREATE SEQUENCES

CREATE SEQUENCE id_BASIC_LOGS;
CREATE SEQUENCE ID_FILE_LOADED;


-- Update index
ALTER SEQUENCE id_BASIC_LOGS RESTART WITH 1;
ALTER SEQUENCE ID_FILE_LOADED RESTART WITH 1;

-- CREATE TABLES
CREATE TABLE CONTAINER_LOGS_DATA.FILES_LOADED (ID integer  PRIMARY KEY , NAME varchar(200) NOT NULL UNIQUE);
CREATE TABLE CONTAINER_LOGS_DATA.BASIC_LOGS (
ID integer , timeStamp TIMESTAMP NOT NULL  , NAME_file varchar(200) not null, elapsed INTEGER ,	label VARCHAR(1000) NOT NULL,	responseCode VARCHAR(200) NOT NULL,	responseMessage VARCHAR(200) NOT NULL, 	threadName VARCHAR(1000) NOT NULL,	dataType VARCHAR(1000) NOT NULL,	success VARCHAR(1000) NOT NULL,	failureMessage VARCHAR(1000) NOT NULL,	bytes INTEGER NOT NULL, 	Latency INTEGER NOT NULL,primary key (timeStamp, NAME_file));



--SELECT Tables
SELECT table_catalog ,table_schema ,table_name,table_type  FROM information_schema.tables WHERE table_schema = 'container_logs_data'




-- DROP
DROP TABLE CONTAINER_LOGS_DATA.FILES_LOADED;
DROP TABLE  CONTAINER_LOGS_DATA.BASIC_LOGS;

