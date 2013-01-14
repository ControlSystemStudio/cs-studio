--------------------------------------------------------
--  Datei erstellt -Mittwoch-August-22-2012   
--------------------------------------------------------

DROP SEQUENCE "SEQ_ARTIKEL_AUSGELIEHEN";
CREATE SEQUENCE  "SEQ_ARTIKEL_AUSGELIEHEN"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 801 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_ARTIKEL_AUSGEMUSTERT";
CREATE SEQUENCE  "SEQ_ARTIKEL_AUSGEMUSTERT"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 321 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_ARTIKEL_BESCHREIBUNG";
CREATE SEQUENCE  "SEQ_ARTIKEL_BESCHREIBUNG"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 800020 CACHE 5 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_ARTIKEL_DATEN";
CREATE SEQUENCE  "SEQ_ARTIKEL_DATEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 800450 CACHE 5 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_ARTIKEL_EINGANG";
CREATE SEQUENCE  "SEQ_ARTIKEL_EINGANG"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 5521 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_ARTIKEL_EINGEBAUT";
CREATE SEQUENCE  "SEQ_ARTIKEL_EINGEBAUT"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 901 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_ARTIKEL_IN_LAGER";
CREATE SEQUENCE  "SEQ_ARTIKEL_IN_LAGER"  MINVALUE 1 MAXVALUE 99999999999999999 INCREMENT BY 1 START WITH 3621 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_BA";
CREATE SEQUENCE  "SEQ_BA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 8001775 CACHE 5 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_BA_POSITION";
CREATE SEQUENCE  "SEQ_BA_POSITION"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 803435 CACHE 5 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_GEBAEUDE";
CREATE SEQUENCE  "SEQ_GEBAEUDE"  MINVALUE 1 MAXVALUE 99999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_INTERN_ID";
CREATE SEQUENCE  "SEQ_INTERN_ID"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 300060 CACHE 20 NOORDER  NOCYCLE ;

DROP SEQUENCE "SEQ_RAUM";
CREATE SEQUENCE  "SEQ_RAUM"  MINVALUE 1 MAXVALUE 99999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

CREATE TABLE "ACCESSRIGHT" 
(
  "ACCOUNTNAME" VARCHAR2(30), 
  "LOGGROUP" VARCHAR2(10), 
  "ACCESSAPPLICATION" VARCHAR2(20), 
  "ACCESSTYPE" VARCHAR2(10)
);

CREATE TABLE "APPLICATION" 
(
  "NAME" VARCHAR2(20), 
  "DESCRIPTION" VARCHAR2(80)
);

CREATE TABLE "ARTIKEL_AUSGELIEHEN" 
(
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "NAME" VARCHAR2(30), 
  "GROUP_NAME" VARCHAR2(10), 
  "ADDRESS" VARCHAR2(120), 
  "DATE_BACK" DATE, 
  "AUSGELIEHEN_AM" DATE, 
  "AUSGELIEHEN_DURCH" VARCHAR2(30), 
  "ID" NUMBER(10,0)
);

CREATE TABLE "ARTIKEL_AUSGEMUSTERT" 
(
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "BEGRUENDUNG" VARCHAR2(200), 
  "AUSGEMUSTERT_AM" DATE, 
  "AUSGEMUSTERT_DURCH" VARCHAR2(30), 
  "ID" NUMBER(10,0)
);

CREATE TABLE "ARTIKEL_BESCHREIBUNG" 
(
  "ID" NUMBER(8,0), 
  "DFG_SCHLUESSEL" VARCHAR2(8), 
  "BESCHREIBUNG" VARCHAR2(200), 
  "PRODUKT_TYP" VARCHAR2(30), 
  "LIEFERANT_NAME" VARCHAR2(30), 
  "LIEFERANT_BEST_NR" VARCHAR2(20), 
  "LIEFERANT_STUECKPREIS" NUMBER(10,0), 
  "HTML_LINK" VARCHAR2(120), 
  "VERGLEICHS_TYP" NUMBER(8,0), 
  "DESY_LAGER_NR" NUMBER(8,0), 
  "WAEHRUNG" VARCHAR2(5), 
  "LIEFERZEIT_WOCHEN" NUMBER(3,0)
);

CREATE TABLE "ARTIKEL_DATEN" 
(
  "ID" NUMBER(10,0), 
  "ARTIKEL_BESCHREIBUNG_ID" NUMBER(8,0), 
  "INTERN_ID" VARCHAR2(20), 
  "SERIEN_NR" VARCHAR2(30), 
  "GRUPPE_ARTIKEL" NUMBER(10,0), 
  "BARCODE" VARCHAR2(15), 
  "INVENTAR_NR" NUMBER(10,0), 
  "STATUS" VARCHAR2(20), 
  "GRUPPE" VARCHAR2(10)
);

CREATE TABLE "ARTIKEL_EINGANG" 
(
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "EINGEGANGEN_AM" DATE, 
  "EINGETRAGEN_DURCH" VARCHAR2(30), 
  "ID" NUMBER(10,0)
);

CREATE TABLE "ARTIKEL_EINGEBAUT" 
(
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "PROJEKT" VARCHAR2(20), 
  "DEVICE" VARCHAR2(20), 
  "GEBAEUDE" VARCHAR2(20), 
  "RAUM" VARCHAR2(20), 
  "EINGEBAUT_IN_ARTIKEL" NUMBER(10,0), 
  "EINGEBAUT_AM" DATE, 
  "EINGEBAUT_DURCH" VARCHAR2(30), 
  "LOCATION_DETAILS" VARCHAR2(20), 
  "RAUM_ID" NUMBER(*,0), 
  "ID" NUMBER(10,0)
);

CREATE TABLE "ARTIKEL_IN_LAGER" 
(
  "LAGER_NAME" VARCHAR2(20), 
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "LAGER_ARTIKEL_ID" VARCHAR2(30), 
  "IN_LAGER_AM" DATE, 
  "IN_LAGER_DURCH" VARCHAR2(30), 
  "FLAG_EXIST" VARCHAR2(3), 
  "ID" NUMBER(10,0)
);

CREATE TABLE "ARTIKEL_IN_WARTUNG" 
(
  "ID" VARCHAR2(30), 
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "BEI_FIRMA" VARCHAR2(30), 
  "BEI_GRUPPE" VARCHAR2(10), 
  "BEI_ACCOUNT" VARCHAR2(30), 
  "WARTUNG_DURCH" VARCHAR2(30), 
  "STATUS" VARCHAR2(20), 
  "STATUS_VOM" DATE, 
  "PROJECT" VARCHAR2(20), 
  "DEVICE" VARCHAR2(20), 
  "LOCATION" VARCHAR2(30), 
  "KEYWORDS" VARCHAR2(80), 
  "DESCSHORT" VARCHAR2(60), 
  "DESCLONG" VARCHAR2(400), 
  "START_REQUEST" DATE, 
  "FINISH_REQUEST" DATE, 
  "SENDEMAILTO" VARCHAR2(200)
);

CREATE TABLE "ARTIKEL_STATUS" 
(
  "STATUS" VARCHAR2(20)
);

CREATE TABLE "BA" 
(
  "NUMMER" NUMBER(10,0),
  "BA_TYPE" VARCHAR2(20), 
  "FIRMA_NAME" VARCHAR2(30), 
  "AUSSTELLER" VARCHAR2(30), 
  "GRUPPE" VARCHAR2(10), 
  "VORHERIGE_BA" NUMBER(10,0), 
  "ABLADE_STELLE" VARCHAR2(20), 
  "KOSTENSTELLE" VARCHAR2(10), 
  "TERMIN" DATE, 
  "AUSSTELLUNGS_DATUM" DATE, 
  "ZU_INVENTAR_NR" NUMBER(10,0), 
  "PROJEKT" VARCHAR2(20), 
  "GESAMTWERT" NUMBER(10,0), 
  "DESY_AUFTRAGS_NR" VARCHAR2(20), 
  "BESCHREIBUNG" VARCHAR2(120), 
  "WAEHRUNG" VARCHAR2(5), 
  "TEXT" VARCHAR2(400), 
  "MAINTENANCE_CONTRACT" NUMBER(1,0), 
  "VALID_UNTIL" DATE, 
  "REMEMBER_EXPIRATION" NUMBER(1,0)
);

CREATE TABLE "BA_POSITION" 
(
  "BA_NR" NUMBER(10,0), 
  "ARTIKEL_DATEN_ID" NUMBER(10,0), 
  "POSITION_NR" NUMBER(4,0), 
  "ANZAHL_BESTELLT" NUMBER(10,0), 
  "ANZAHL_GELIEFERT" NUMBER(10,0), 
  "LIEFERDATUM_GEPLANT" DATE, 
  "LIEFERDATUM" DATE, 
  "EINZELPREIS" NUMBER(10,0), 
  "WAEHRUNG" VARCHAR2(5), 
  "ID" NUMBER(10,0)
);

CREATE TABLE "CHECKBALOGIN" 
(
  "LOGIN_DATE" DATE, 
  "ACCOUNTNAME" VARCHAR2(64), 
  "USERNAME" VARCHAR2(64)
);

CREATE TABLE "DEVICE" 
(
  "KEYWORD" VARCHAR2(20)
);

CREATE TABLE "FIRMA" 
(
  "NAME" VARCHAR2(30), 
  "FIRMA_NUMBER" NUMBER(8,0), 
  "NAME_LANG" VARCHAR2(60), 
  "STRASSE" VARCHAR2(100), 
  "POSTLEITZAHL" VARCHAR2(10), 
  "STADT" VARCHAR2(60), 
  "LAND" VARCHAR2(60), 
  "TELEFON" VARCHAR2(60), 
  "FAX" VARCHAR2(60), 
  "EMAIL" VARCHAR2(200), 
  "BESCHREIBUNG" VARCHAR2(120), 
  "KEYWORD_LIST" VARCHAR2(200)
);

CREATE TABLE "FIRMA_KEYWORDS" 
(
  "KEYWORD" VARCHAR2(20)
);

CREATE TABLE "FIRMA_KONTAKT" 
(
  "FIRMA_NAME" VARCHAR2(30), 
  "NAME" VARCHAR2(30), 
  "BESCHREIBUNG" VARCHAR2(120), 
  "EMAIL" VARCHAR2(200), 
  "TELEFON" VARCHAR2(20)
);

CREATE TABLE "GEBAEUDE" 
(
  "NAME" VARCHAR2(20), 
  "GEBAEUDE_ID" NUMBER(*,0)
);

CREATE TABLE "KEYWORDBA" 
(
  "KEYWORD" VARCHAR2(20)
);

CREATE TABLE "KEYWORDHARD" 
(
  "KEYWORD" VARCHAR2(20)
);

CREATE TABLE "KEYWORDSOFT" 
(
  "KEYWORD" VARCHAR2(20)
);

CREATE TABLE "KRYOPROMPT" 
(
  "ITEMNAME" VARCHAR2(80), 
  "ENGLISH_HINT" VARCHAR2(100), 
  "GERMAN_HINT" VARCHAR2(100), 
  "FORMNAME" VARCHAR2(20)
);

CREATE TABLE "LAGER" 
(
  "NAME" VARCHAR2(20), 
  "GROUP_OWNER" VARCHAR2(10), 
  "RESPONSIBLE_PERSON" VARCHAR2(30), 
  "IN_GEBAEUDE" VARCHAR2(20), 
  "IN_RAUM" VARCHAR2(20), 
  "LAGER_PRAEFIX" VARCHAR2(10)
);

CREATE TABLE "LAGER_ARTIKEL" 
(
  "ID" VARCHAR2(30), 
  "LAGER_NAME" VARCHAR2(20), 
  "ARTIKEL_BESCHREIBUNG_ID" NUMBER(10,0), 
  "ORT" VARCHAR2(20), 
  "FACH" VARCHAR2(20), 
  "BOX" VARCHAR2(20), 
  "SOLL_BESTAND" NUMBER(8,0), 
  "ACTUAL_BESTAND" NUMBER(8,0), 
  "QUANTITY" VARCHAR2(8), 
  "NOTE" VARCHAR2(200), 
  "DOC_REF_PAPER" VARCHAR2(100), 
  "DOC_REF_HTML" VARCHAR2(200)
);

CREATE TABLE "LAGER_BOX" 
(
  "LAGER_NAME" VARCHAR2(20), 
  "NAME" VARCHAR2(20)
);

CREATE TABLE "LAGER_FACH" 
(
  "LAGER_NAME" VARCHAR2(20), 
  "NAME" VARCHAR2(20)
);

CREATE TABLE "LAGER_ORT" 
(
  "LAGER_NAME" VARCHAR2(20), 
  "NAME" VARCHAR2(20)
);

CREATE TABLE "LOGGROUP" 
(
  "GROUPNAME" VARCHAR2(10), 
  "GROUPEMAIL" VARCHAR2(200) DEFAULT 0, 
  "WARTUNGEMAIL" VARCHAR2(200), 
  "PRAEFIX" VARCHAR2(10)
);

CREATE TABLE "LOGUSER" 
(
  "FIRSTNAME" VARCHAR2(20), 
  "LASTNAME" VARCHAR2(20), 
  "ACCOUNTNAME" VARCHAR2(30), 
  "PASSWORD" VARCHAR2(20), 
  "LOGGROUP" VARCHAR2(10), 
  "EMAIL" VARCHAR2(200), 
  "TELEFONEWORK" VARCHAR2(20), 
  "TELEFONEPRIVATE1" VARCHAR2(20), 
  "TELEFONEPRIVATE2" VARCHAR2(20), 
  "TELEFONEALARM" VARCHAR2(20), 
  "ACCOUNTCREATED" DATE, 
  "ACCOUNTCREATEDBY" VARCHAR2(20), 
  "ACCOUNTMODIFIED" DATE, 
  "ACCOUNTMODIFIEDBY" VARCHAR2(20), 
  "GROUPADMINISTRATOR" NUMBER(1,0) DEFAULT 0, 
  "ADMINISTRATOR" NUMBER(1,0) DEFAULT 0, 
  "PERSONAL_DATA" NUMBER(1,0) DEFAULT 0, 
  "AT_DESY_SINCE" DATE, 
  "BIRTH_DAY" DATE, 
  "STRASSE" VARCHAR2(60), 
  "NUMMER" NUMBER(6,0), 
  "ORT" VARCHAR2(60), 
  "PLZ" NUMBER(12,0), 
  "LAND" VARCHAR2(60), 
  "OFFICE" VARCHAR2(80), 
  "INITIALS" VARCHAR2(10)
);

CREATE TABLE "PROJECT" 
(
  "KEYWORD" VARCHAR2(20)
);

CREATE TABLE "RAUM" 
(
  "NAME" VARCHAR2(20), 
  "RAUM_ID" NUMBER(*,0), 
  "GEBAEUDE_ID" NUMBER(*,0)
);

CREATE TABLE "TOOLBOX_PROMPT" 
(
  "ITEMNAME" VARCHAR2(80), 
  "ENGLISH_HINT" VARCHAR2(100), 
  "GERMAN_HINT" VARCHAR2(100), 
  "FORMNAME" VARCHAR2(20)
);

CREATE TABLE "WARTUNG_BESCHREIBUNG" 
(
  "ARTIKEL_BESCHREIBUNG_ID" NUMBER(8,0), 
  "BESCHREIBUNG" VARCHAR2(400), 
  "HTML_LINK" VARCHAR2(120), 
  "WARTUNGS_INTERVALL" VARCHAR2(20)
);

CREATE TABLE "WARTUNG_STATUS" 
(
  "STATUS" VARCHAR2(20)
);

REM INSERTING into WARTUNG_STATUS
Insert into WARTUNG_STATUS (STATUS) values ('Rueckfrage');
Insert into WARTUNG_STATUS (STATUS) values ('angeliefert');
Insert into WARTUNG_STATUS (STATUS) values ('ausgeliefert');
Insert into WARTUNG_STATUS (STATUS) values ('definiert');
Insert into WARTUNG_STATUS (STATUS) values ('in Arbeit');
Insert into WARTUNG_STATUS (STATUS) values ('nicht definiert');
Insert into WARTUNG_STATUS (STATUS) values ('nicht reparabel');
Insert into WARTUNG_STATUS (STATUS) values ('repariert');

Insert into ARTIKEL_STATUS (STATUS) values ('angeliefert');
Insert into ARTIKEL_STATUS (STATUS) values ('ausgeliehen');
Insert into ARTIKEL_STATUS (STATUS) values ('ausgeliehen naechst');
Insert into ARTIKEL_STATUS (STATUS) values ('ausgemustert');
Insert into ARTIKEL_STATUS (STATUS) values ('bestellt');
Insert into ARTIKEL_STATUS (STATUS) values ('definiert');
Insert into ARTIKEL_STATUS (STATUS) values ('eingebaut');
Insert into ARTIKEL_STATUS (STATUS) values ('in Lager');
Insert into ARTIKEL_STATUS (STATUS) values ('in Reparatur');
Insert into ARTIKEL_STATUS (STATUS) values ('nicht definiert');

--------------------------------------------------------
--  Constraints for Table LAGER_ARTIKEL
--------------------------------------------------------

ALTER TABLE "LAGER_ARTIKEL" ADD CONSTRAINT "LAGER_ARTIKEL_UN" UNIQUE ("LAGER_NAME", "ORT", "FACH", "BOX") DISABLE;
ALTER TABLE "LAGER_ARTIKEL" ADD PRIMARY KEY ("ID") ENABLE;

--------------------------------------------------------
--  Constraints for Table ARTIKEL_BESCHREIBUNG
--------------------------------------------------------

ALTER TABLE "ARTIKEL_BESCHREIBUNG" ADD CONSTRAINT "UNIQUE_DESC" UNIQUE ("BESCHREIBUNG") ENABLE;
ALTER TABLE "ARTIKEL_BESCHREIBUNG" ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "ARTIKEL_BESCHREIBUNG" MODIFY ("BESCHREIBUNG" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ARTIKEL_AUSGELIEHEN
--------------------------------------------------------

ALTER TABLE "ARTIKEL_AUSGELIEHEN" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table KEYWORDBA
--------------------------------------------------------

ALTER TABLE "KEYWORDBA" ADD PRIMARY KEY ("KEYWORD") ENABLE;

--------------------------------------------------------
--  Constraints for Table DEVICE
--------------------------------------------------------

ALTER TABLE "DEVICE" ADD PRIMARY KEY ("KEYWORD") ENABLE;

--------------------------------------------------------
--  Constraints for Table WARTUNG_BESCHREIBUNG
--------------------------------------------------------

ALTER TABLE "WARTUNG_BESCHREIBUNG" ADD CONSTRAINT "UNIQ_WART_BESCH" UNIQUE ("BESCHREIBUNG") ENABLE;
ALTER TABLE "WARTUNG_BESCHREIBUNG" ADD CONSTRAINT "UNIQ_WART_BESCH_ID" UNIQUE ("ARTIKEL_BESCHREIBUNG_ID") ENABLE;

--------------------------------------------------------
--  Constraints for Table KEYWORDHARD
--------------------------------------------------------

ALTER TABLE "KEYWORDHARD" ADD PRIMARY KEY ("KEYWORD") ENABLE;

--------------------------------------------------------
--  Constraints for Table ARTIKEL_EINGANG
--------------------------------------------------------

ALTER TABLE "ARTIKEL_EINGANG" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ARTIKEL_AUSGEMUSTERT
--------------------------------------------------------

ALTER TABLE "ARTIKEL_AUSGEMUSTERT" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ARTIKEL_IN_WARTUNG
--------------------------------------------------------

ALTER TABLE "ARTIKEL_IN_WARTUNG" MODIFY ("DESCLONG" NOT NULL ENABLE);
ALTER TABLE "ARTIKEL_IN_WARTUNG" MODIFY ("DESCSHORT" NOT NULL ENABLE);
ALTER TABLE "ARTIKEL_IN_WARTUNG" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table LOGGROUP
--------------------------------------------------------

ALTER TABLE "LOGGROUP" ADD PRIMARY KEY ("GROUPNAME") ENABLE;

--------------------------------------------------------
--  Constraints for Table APPLICATION
--------------------------------------------------------

ALTER TABLE "APPLICATION" ADD PRIMARY KEY ("NAME") ENABLE;
ALTER TABLE "APPLICATION" MODIFY ("DESCRIPTION" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table FIRMA
--------------------------------------------------------

ALTER TABLE "FIRMA" ADD UNIQUE ("FIRMA_NUMBER") ENABLE;
ALTER TABLE "FIRMA" ADD PRIMARY KEY ("NAME") ENABLE;

--------------------------------------------------------
--  Constraints for Table RAUM
--------------------------------------------------------

ALTER TABLE "RAUM" ADD PRIMARY KEY ("NAME") ENABLE;

--------------------------------------------------------
--  Constraints for Table LAGER_BOX
--------------------------------------------------------

ALTER TABLE "LAGER_BOX" ADD CONSTRAINT "LAGER_BOX_PRIM" PRIMARY KEY ("LAGER_NAME", "NAME") ENABLE;
ALTER TABLE "LAGER_BOX" MODIFY ("NAME" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table KEYWORDSOFT
--------------------------------------------------------

ALTER TABLE "KEYWORDSOFT" ADD PRIMARY KEY ("KEYWORD") ENABLE;

--------------------------------------------------------
--  Constraints for Table KRYOPROMPT
--------------------------------------------------------

ALTER TABLE "KRYOPROMPT" ADD CONSTRAINT "UN_KRY_PRO" UNIQUE ("FORMNAME", "ITEMNAME") ENABLE;

--------------------------------------------------------
--  Constraints for Table BA
--------------------------------------------------------

ALTER TABLE "BA" ADD PRIMARY KEY ("NUMMER") ENABLE;
ALTER TABLE "BA" MODIFY ("AUSSTELLER" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table WARTUNG_STATUS
--------------------------------------------------------

ALTER TABLE "WARTUNG_STATUS" ADD PRIMARY KEY ("STATUS") ENABLE;

--------------------------------------------------------
--  Constraints for Table LAGER_ORT
--------------------------------------------------------

ALTER TABLE "LAGER_ORT" ADD CONSTRAINT "LAGER_ORT_PRIM" PRIMARY KEY ("LAGER_NAME", "NAME") ENABLE;
ALTER TABLE "LAGER_ORT" MODIFY ("NAME" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table LAGER_FACH
--------------------------------------------------------

ALTER TABLE "LAGER_FACH" ADD CONSTRAINT "LAGER_FACH_PRIM" PRIMARY KEY ("LAGER_NAME", "NAME") ENABLE;
ALTER TABLE "LAGER_FACH" MODIFY ("NAME" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ARTIKEL_EINGEBAUT
--------------------------------------------------------

ALTER TABLE "ARTIKEL_EINGEBAUT" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table PROJECT
--------------------------------------------------------

ALTER TABLE "PROJECT" ADD PRIMARY KEY ("KEYWORD") ENABLE;

--------------------------------------------------------
--  Constraints for Table ARTIKEL_STATUS
--------------------------------------------------------

ALTER TABLE "ARTIKEL_STATUS" ADD PRIMARY KEY ("STATUS") ENABLE;

--------------------------------------------------------
--  Constraints for Table BA_POSITION
--------------------------------------------------------

ALTER TABLE "BA_POSITION" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table FIRMA_KONTAKT
--------------------------------------------------------

ALTER TABLE "FIRMA_KONTAKT" ADD CONSTRAINT "FIRMA_KONTAKT_UNIQ" UNIQUE ("FIRMA_NAME", "NAME") ENABLE;
ALTER TABLE "FIRMA_KONTAKT" MODIFY ("NAME" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table LAGER
--------------------------------------------------------

ALTER TABLE "LAGER" ADD PRIMARY KEY ("NAME") ENABLE;

--------------------------------------------------------
--  Constraints for Table LOGUSER
--------------------------------------------------------

ALTER TABLE "LOGUSER" ADD PRIMARY KEY ("ACCOUNTNAME") ENABLE;
ALTER TABLE "LOGUSER" MODIFY ("ACCOUNTCREATED" NOT NULL ENABLE);
ALTER TABLE "LOGUSER" MODIFY ("PASSWORD" NOT NULL ENABLE);
ALTER TABLE "LOGUSER" MODIFY ("LASTNAME" NOT NULL ENABLE);
ALTER TABLE "LOGUSER" MODIFY ("FIRSTNAME" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table GEBAEUDE
--------------------------------------------------------

ALTER TABLE "GEBAEUDE" ADD PRIMARY KEY ("NAME") ENABLE;

--------------------------------------------------------
--  Constraints for Table ARTIKEL_IN_LAGER
--------------------------------------------------------

ALTER TABLE "ARTIKEL_IN_LAGER" MODIFY ("ARTIKEL_DATEN_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ARTIKEL_DATEN
--------------------------------------------------------

ALTER TABLE "ARTIKEL_DATEN" ADD UNIQUE ("INTERN_ID") ENABLE;
ALTER TABLE "ARTIKEL_DATEN" ADD PRIMARY KEY ("ID") ENABLE;

--------------------------------------------------------
--  Constraints for Table ACCESSRIGHT
--------------------------------------------------------

ALTER TABLE "ACCESSRIGHT" ADD CONSTRAINT "ACCESSRIGHT_UNIQUE" UNIQUE ("ACCOUNTNAME", "LOGGROUP", "ACCESSAPPLICATION") ENABLE;

--------------------------------------------------------
--  Constraints for Table FIRMA_KEYWORDS
--------------------------------------------------------

ALTER TABLE "FIRMA_KEYWORDS" ADD UNIQUE ("KEYWORD") ENABLE;

--------------------------------------------------------
--  DDL for Index LAGER_ORT_PRIM
--------------------------------------------------------

CREATE UNIQUE INDEX "LAGER_ORT_PRIM" ON "LAGER_ORT" ("LAGER_NAME", "NAME");

--------------------------------------------------------
--  DDL for Index UNIQUE_DESC
--------------------------------------------------------

CREATE UNIQUE INDEX "UNIQUE_DESC" ON "ARTIKEL_BESCHREIBUNG" ("BESCHREIBUNG");

--------------------------------------------------------
--  DDL for Index LAGER_FACH_PRIM
--------------------------------------------------------

CREATE UNIQUE INDEX "LAGER_FACH_PRIM" ON "LAGER_FACH" ("LAGER_NAME", "NAME");

--------------------------------------------------------
--  DDL for Index UNIQ_WART_BESCH_ID
--------------------------------------------------------

CREATE UNIQUE INDEX "UNIQ_WART_BESCH_ID" ON "WARTUNG_BESCHREIBUNG" ("ARTIKEL_BESCHREIBUNG_ID");

--------------------------------------------------------
--  DDL for Index UN_KRY_PRO
--------------------------------------------------------

CREATE UNIQUE INDEX "UN_KRY_PRO" ON "KRYOPROMPT" ("FORMNAME", "ITEMNAME");

--------------------------------------------------------
--  DDL for Index ACCESSRIGHT_UNIQUE
--------------------------------------------------------

CREATE UNIQUE INDEX "ACCESSRIGHT_UNIQUE" ON "ACCESSRIGHT" ("ACCOUNTNAME", "LOGGROUP", "ACCESSAPPLICATION");

--------------------------------------------------------
--  DDL for Index UNIQ_WART_BESCH
--------------------------------------------------------

CREATE UNIQUE INDEX "UNIQ_WART_BESCH" ON "WARTUNG_BESCHREIBUNG" ("BESCHREIBUNG");

--------------------------------------------------------
--  DDL for Index IDX_AD_ABID
--------------------------------------------------------

CREATE INDEX "IDX_AD_ABID" ON "ARTIKEL_DATEN" ("ARTIKEL_BESCHREIBUNG_ID");

--------------------------------------------------------
--  DDL for Index FIRMA_KONTAKT_UNIQ
--------------------------------------------------------

CREATE UNIQUE INDEX "FIRMA_KONTAKT_UNIQ" ON "FIRMA_KONTAKT" ("FIRMA_NAME", "NAME");

--------------------------------------------------------
--  DDL for Index LAGER_BOX_PRIM
--------------------------------------------------------

CREATE UNIQUE INDEX "LAGER_BOX_PRIM" ON "LAGER_BOX" ("LAGER_NAME", "NAME");

--------------------------------------------------------
--  DDL for Index IDX_AE_AD_ID
--------------------------------------------------------

CREATE INDEX "IDX_AE_AD_ID" ON "ARTIKEL_EINGEBAUT" ("ARTIKEL_DATEN_ID");

--------------------------------------------------------
--  Ref Constraints for Table ACCESSRIGHT
--------------------------------------------------------

ALTER TABLE "ACCESSRIGHT" ADD FOREIGN KEY ("ACCESSAPPLICATION")
      REFERENCES "APPLICATION" ("NAME") ENABLE;
ALTER TABLE "ACCESSRIGHT" ADD FOREIGN KEY ("LOGGROUP")
      REFERENCES "LOGGROUP" ("GROUPNAME") ENABLE;
ALTER TABLE "ACCESSRIGHT" ADD FOREIGN KEY ("ACCOUNTNAME")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_AUSGELIEHEN
--------------------------------------------------------

ALTER TABLE "ARTIKEL_AUSGELIEHEN" ADD FOREIGN KEY ("ARTIKEL_DATEN_ID")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_AUSGELIEHEN" ADD FOREIGN KEY ("GROUP_NAME")
      REFERENCES "LOGGROUP" ("GROUPNAME") ENABLE;
ALTER TABLE "ARTIKEL_AUSGELIEHEN" ADD FOREIGN KEY ("AUSGELIEHEN_DURCH")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;
ALTER TABLE "ARTIKEL_AUSGELIEHEN" ADD FOREIGN KEY ("NAME")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_AUSGEMUSTERT
--------------------------------------------------------

ALTER TABLE "ARTIKEL_AUSGEMUSTERT" ADD FOREIGN KEY ("ARTIKEL_DATEN_ID")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_AUSGEMUSTERT" ADD FOREIGN KEY ("AUSGEMUSTERT_DURCH")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_BESCHREIBUNG
--------------------------------------------------------

ALTER TABLE "ARTIKEL_BESCHREIBUNG" ADD FOREIGN KEY ("VERGLEICHS_TYP")
      REFERENCES "ARTIKEL_BESCHREIBUNG" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_BESCHREIBUNG" ADD FOREIGN KEY ("LIEFERANT_NAME")
      REFERENCES "FIRMA" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_DATEN
--------------------------------------------------------

ALTER TABLE "ARTIKEL_DATEN" ADD FOREIGN KEY ("ARTIKEL_BESCHREIBUNG_ID")
      REFERENCES "ARTIKEL_BESCHREIBUNG" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_DATEN" ADD FOREIGN KEY ("STATUS")
      REFERENCES "ARTIKEL_STATUS" ("STATUS") ENABLE;
ALTER TABLE "ARTIKEL_DATEN" ADD FOREIGN KEY ("GRUPPE")
      REFERENCES "LOGGROUP" ("GROUPNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_EINGANG
--------------------------------------------------------

ALTER TABLE "ARTIKEL_EINGANG" ADD FOREIGN KEY ("ARTIKEL_DATEN_ID")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_EINGANG" ADD FOREIGN KEY ("EINGETRAGEN_DURCH")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_EINGEBAUT
--------------------------------------------------------

ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("EINGEBAUT_IN_ARTIKEL")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("ARTIKEL_DATEN_ID")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("DEVICE")
      REFERENCES "DEVICE" ("KEYWORD") ENABLE;
ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("GEBAEUDE")
      REFERENCES "GEBAEUDE" ("NAME") ENABLE;
ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("EINGEBAUT_DURCH")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;
ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("PROJEKT")
      REFERENCES "PROJECT" ("KEYWORD") ENABLE;
ALTER TABLE "ARTIKEL_EINGEBAUT" ADD FOREIGN KEY ("RAUM")
      REFERENCES "RAUM" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARTIKEL_IN_LAGER
--------------------------------------------------------

ALTER TABLE "ARTIKEL_IN_LAGER" ADD FOREIGN KEY ("ARTIKEL_DATEN_ID")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_IN_LAGER" ADD FOREIGN KEY ("LAGER_NAME")
      REFERENCES "LAGER" ("NAME") ENABLE;
ALTER TABLE "ARTIKEL_IN_LAGER" ADD FOREIGN KEY ("LAGER_ARTIKEL_ID")
      REFERENCES "LAGER_ARTIKEL" ("ID") ENABLE;
ALTER TABLE "ARTIKEL_IN_LAGER" ADD FOREIGN KEY ("IN_LAGER_DURCH")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table BA
--------------------------------------------------------

ALTER TABLE "BA" ADD FOREIGN KEY ("VORHERIGE_BA")
      REFERENCES "BA" ("NUMMER") ENABLE;
ALTER TABLE "BA" ADD FOREIGN KEY ("FIRMA_NAME")
      REFERENCES "FIRMA" ("NAME") ENABLE;
ALTER TABLE "BA" ADD FOREIGN KEY ("GRUPPE")
      REFERENCES "LOGGROUP" ("GROUPNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table BA_POSITION
--------------------------------------------------------

ALTER TABLE "BA_POSITION" ADD FOREIGN KEY ("ARTIKEL_DATEN_ID")
      REFERENCES "ARTIKEL_DATEN" ("ID") ENABLE;
ALTER TABLE "BA_POSITION" ADD FOREIGN KEY ("BA_NR")
      REFERENCES "BA" ("NUMMER") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table FIRMA_KONTAKT
--------------------------------------------------------

ALTER TABLE "FIRMA_KONTAKT" ADD FOREIGN KEY ("FIRMA_NAME")
      REFERENCES "FIRMA" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table LAGER
--------------------------------------------------------

ALTER TABLE "LAGER" ADD FOREIGN KEY ("GROUP_OWNER")
      REFERENCES "LOGGROUP" ("GROUPNAME") ENABLE;
ALTER TABLE "LAGER" ADD FOREIGN KEY ("RESPONSIBLE_PERSON")
      REFERENCES "LOGUSER" ("ACCOUNTNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table LAGER_ARTIKEL
--------------------------------------------------------

ALTER TABLE "LAGER_ARTIKEL" ADD CONSTRAINT "LAGER_BOX_REF" FOREIGN KEY ("LAGER_NAME", "BOX")
      REFERENCES "LAGER_BOX" ("LAGER_NAME", "NAME") ENABLE;
ALTER TABLE "LAGER_ARTIKEL" ADD CONSTRAINT "LAGER_FACH_REF" FOREIGN KEY ("LAGER_NAME", "FACH")
      REFERENCES "LAGER_FACH" ("LAGER_NAME", "NAME") ENABLE;
ALTER TABLE "LAGER_ARTIKEL" ADD CONSTRAINT "LAGER_ORT_REF" FOREIGN KEY ("LAGER_NAME", "ORT")
      REFERENCES "LAGER_ORT" ("LAGER_NAME", "NAME") ENABLE;
ALTER TABLE "LAGER_ARTIKEL" ADD FOREIGN KEY ("ARTIKEL_BESCHREIBUNG_ID")
      REFERENCES "ARTIKEL_BESCHREIBUNG" ("ID") ENABLE;
ALTER TABLE "LAGER_ARTIKEL" ADD FOREIGN KEY ("LAGER_NAME")
      REFERENCES "LAGER" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table LAGER_BOX
--------------------------------------------------------

ALTER TABLE "LAGER_BOX" ADD FOREIGN KEY ("LAGER_NAME")
      REFERENCES "LAGER" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table LAGER_FACH
--------------------------------------------------------

ALTER TABLE "LAGER_FACH" ADD FOREIGN KEY ("LAGER_NAME")
      REFERENCES "LAGER" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table LAGER_ORT
--------------------------------------------------------

ALTER TABLE "LAGER_ORT" ADD FOREIGN KEY ("LAGER_NAME")
      REFERENCES "LAGER" ("NAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table LOGUSER
--------------------------------------------------------

ALTER TABLE "LOGUSER" ADD FOREIGN KEY ("LOGGROUP")
      REFERENCES "LOGGROUP" ("GROUPNAME") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table WARTUNG_BESCHREIBUNG
--------------------------------------------------------

ALTER TABLE "WARTUNG_BESCHREIBUNG" ADD FOREIGN KEY ("ARTIKEL_BESCHREIBUNG_ID")
      REFERENCES "ARTIKEL_BESCHREIBUNG" ("ID") ENABLE;

