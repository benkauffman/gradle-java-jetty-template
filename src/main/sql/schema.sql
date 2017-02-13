DROP SCHEMA IF EXISTS template_00_01_00;
CREATE SCHEMA IF NOT EXISTS template_00_01_00;
USE template_00_01_00;


-- tables
-- Table: application_user
CREATE TABLE application_user (
  id         INT          NOT NULL AUTO_INCREMENT,
  email      VARCHAR(200) NOT NULL,
  password   VARCHAR(200) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name  VARCHAR(100) NOT NULL,
  created    DATETIME     NOT NULL DEFAULT NOW(),
  updated    DATETIME     NOT NULL DEFAULT NOW(),
  deleted    DATETIME     NULL,
  UNIQUE INDEX email (email),
  CONSTRAINT application_user_pk PRIMARY KEY (id)
);


