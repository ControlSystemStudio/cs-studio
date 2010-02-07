CREATE TABLE `desy_record_db`.`records`
(
   id int PRIMARY KEY NOT NULL,
   io_name text NOT NULL,
   epics_name text NOT NULL,
   record_type text NOT NULL,
   dct_id text,
   dct_project_id text
)
;
CREATE UNIQUE INDEX PRIMARY ON records(id)
;