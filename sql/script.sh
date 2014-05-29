sudo -u postgres psql postgres

sudo -u postgres createuser root

sudo createdb readLogs
sudo -u postgres psql reader_logs
sudo psql readLogs

SELECT datname FROM pg_database WHERE datistemplate = false;


CREATE USER reader_logs WITH PASSWORD 'temporal1';

CREATE SCHEMA CONTAINER_LOGS_DATA  AUTHORIZATION reader_logs;


-- CREATE SEQUENCES

CREATE SEQUENCE id_BASIC_LOGS;
CREATE SEQUENCE ID_FILE_LOADED;
CREATE SEQUENCE id_FILE_ENVIRONMENT;


-- Update index
ALTER SEQUENCE id_BASIC_LOGS RESTART WITH 1;
ALTER SEQUENCE ID_FILE_LOADED RESTART WITH 1;

-- CREATE TABLES
(id, Latency, bytes, dataType, elapsed, failureMessage, label, responseCode, responseMessage, success, threadName, timeStamp, application, environment


CREATE TABLE CONTAINER_LOGS_DATA.BASIC_LOGS (
ID integer , timeStamp TIMESTAMP NOT NULL  , elapsed INTEGER ,	label VARCHAR(1000) NOT NULL,	responseCode VARCHAR(200) NOT NULL,	responseMessage VARCHAR(200) NOT NULL, 	threadName VARCHAR(1000) NOT NULL,	dataType VARCHAR(1000) NOT NULL,	success VARCHAR(1000) NOT NULL,	failureMessage VARCHAR(1000) NOT NULL,	bytes INTEGER NOT NULL, 	Latency INTEGER NOT NULL , application VARCHAR(10) NOT NULL , ENVIRONMENT VARCHAR(5) NOT NULL , PRIMARY KEY(timestamp , ENVIRONMENT , application));


CREATE TABLE CONTAINER_LOGS_DATA.FILE_ENVIRONMENT(
ID integer NOT NULL UNIQUE , 
nameFile VARCHAR(100) not null,
environment VARCHAR(100) not null,
numberOfLine integer not null,
PRIMARY KEY(nameFile, environment));



--SELECT Tables
SELECT table_catalog ,table_schema ,table_name,table_type  FROM information_schema.tables WHERE table_schema = 'container_logs_data'




-- DROP
DROP TABLE CONTAINER_LOGS_DATA.FILES_LOADED;
DROP TABLE  CONTAINER_LOGS_DATA.BASIC_LOGS;

