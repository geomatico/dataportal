-- User: icos
CREATE ROLE icos LOGIN ENCRYPTED PASSWORD 'md53b94b21842c692b80a66b4aed5f1d72b'
   VALID UNTIL 'infinity';


-- Database: dataportal
CREATE DATABASE dataportal
  WITH ENCODING='UTF8'
       OWNER=icos
       CONNECTION LIMIT=-1;
