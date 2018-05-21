--tenant_a
CREATE DATABASE IF NOT EXISTS tenant_a;

CREATE TABLE IF NOT EXISTS tenant_a.people (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  first_name varchar(60) not null,
  last_name  varchar(60) not null,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

INSERT INTO tenant_a.PEOPLE (FIRST_NAME, LAST_NAME, CREATED_AT, UPDATED_AT) VALUES ( 'Drools 7', 'Red Hat', sysdate(), sysdate());

COMMIT;

--tenant_b

CREATE DATABASE IF NOT EXISTS tenant_b;

CREATE TABLE IF NOT EXISTS tenant_b.people (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  first_name varchar(60) not null,
  last_name  varchar(60) not null,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

INSERT INTO tenant_b.PEOPLE (FIRST_NAME, LAST_NAME, CREATED_AT, UPDATED_AT) VALUES ( 'Java 8', 'Ultimate', sysdate(), sysdate());

COMMIT;


-- tenant_C

CREATE DATABASE IF NOT EXISTS tenant_c;

CREATE TABLE IF NOT EXISTS tenant_c.people (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  first_name varchar(60) not null,
  last_name  varchar(60) not null,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

INSERT INTO TENANT_c.PEOPLE (FIRST_NAME, LAST_NAME, CREATED_AT, UPDATED_AT) VALUES ( 'SPRINGBOOT', 'MULTIDB', sysdate(), sysdate());

COMMIT;