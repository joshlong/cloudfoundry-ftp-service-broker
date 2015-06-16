CREATE TABLE FTP_USER(
  USERNAME VARCHAR(255) NOT NULL,
  ADMIN BOOLEAN NOT NULL,
  ENABLED BOOLEAN NOT NULL,
  MAX_IDLE_TIME INTEGER NOT NULL,
  PASSWORD VARCHAR(255),
  WORKSPACE VARCHAR(255),
  PRIMARY KEY(USERNAME)
);

CREATE TABLE SERVICE_INSTANCE(
  SERVICE_INSTANCE_ID   VARCHAR NOT NULL UNIQUE,
  SERVICE_DEFINITION_ID VARCHAR NOT NULL,
  PLAN_ID               VARCHAR NULL,
  ORGANIZATION_NAME     VARCHAR NULL,
  SPACE_GUID            VARCHAR NULL,
);

CREATE TABLE SERVICE_INSTANCE_BINDING(
  SERVICE_INSTANCE_BINDING_ID VARCHAR NOT NULL UNIQUE,
  SERVICE_INSTANCE_ID         VARCHAR NOT NULL,
  SERVICE_DEFINITION_ID       VARCHAR NULL,
  PLAN_ID                     VARCHAR NULL,
  URI                         VARCHAR NULL, -- FOR CREDENTIALS
  ORGANIZATION_NAME           VARCHAR NULL,
  APP_GUID                    VARCHAR NULL,
  SYSLOG_DRAIN_URL            VARCHAR NULL,
  SPACE_GUID                  VARCHAR NULL
);