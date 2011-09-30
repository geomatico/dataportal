-- Table: users
CREATE TABLE "users"
(
   id character varying(128) NOT NULL, 
   "password" character varying(32) NOT NULL, 
   state character varying(16) NOT NULL, 
   hash character varying(32), 
   CONSTRAINT users_pk PRIMARY KEY (id)
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE "users" OWNER TO icos;


-- Table: search
CREATE TABLE "search"
(
   id serial, 
   "user" character varying(128),
   "timestamp" timestamp without time zone, 
   "text" text, 
   start_date date, 
   end_date date, 
   bboxes numeric(9,6)[][4], 
   variables text[], 
   CONSTRAINT search_pk PRIMARY KEY (id),
   CONSTRAINT search_user_fk FOREIGN KEY ("user")
      REFERENCES "users" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE "search" OWNER TO icos;


-- Table: download
CREATE TABLE download
(
  id serial NOT NULL,
  "user" character varying(128),
  "timestamp" timestamp without time zone NOT NULL,
  filename text,
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
DROP TABLE IF EXISTS "download_item" CASCADE;

CREATE TABLE download_item
(
   id_item serial NOT NULL, 
	 text	text	NOT NULL,
	 id_download serial,
   CONSTRAINT download_item_pk PRIMARY KEY (id_item), 
   CONSTRAINT id_download_fk FOREIGN KEY (id_download) 
			REFERENCES download (id) MATCH SIMPLE 
			ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE download_item OWNER TO icos;

-- Table: download_item
/*CREATE TABLE download_item
(
   id_download integer, 
   id_item text NOT NULL, 
   CONSTRAINT download_item_pk PRIMARY KEY (id_download, id_item), 
   CONSTRAINT download_item_id_download_fk FOREIGN KEY (id_download) REFERENCES download (id) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE download_item OWNER TO icos;*/

