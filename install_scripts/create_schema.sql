-- Table: users
CREATE TABLE "users"
(
   "id" character varying(128) NOT NULL, 
   "password" character varying(32) NOT NULL, 
   "state" character varying(16) NOT NULL, 
   "hash" character varying(32), 
   CONSTRAINT users_pk PRIMARY KEY (id)
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE "users" OWNER TO icos;

-- Table: search
CREATE TABLE "search"
(
   "id" serial, 
   "user" character varying(128),
   "timestamp" timestamp without time zone, 
   "text" text, 
   "start_date" date, 
   "end_date" date, 
   "bboxes" text, 
   "variables" text,
   CONSTRAINT search_pk PRIMARY KEY (id),
   CONSTRAINT search_user_fk FOREIGN KEY ("user")
      REFERENCES "users" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE "search" OWNER TO icos;

-- Table: download
CREATE TABLE download
(
  "id" character varying(128) NOT NULL,
  "user" character varying(128) NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  "filename" text,
  CONSTRAINT download_pk PRIMARY KEY (id),
  CONSTRAINT download_user_fk FOREIGN KEY ("user")
      REFERENCES "users" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE download OWNER TO icos;

-- Table: download_item
CREATE TABLE download_item
(
  "id" serial,
  download character varying(128) NOT NULL,
  item_id character varying(128) NOT NULL,
  url text,
  institution text,
  icos_domain text,
  CONSTRAINT download_item_pk PRIMARY KEY (id),
  CONSTRAINT download_item_download_fk FOREIGN KEY (download)
      REFERENCES download (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE download_item OWNER TO icos;
