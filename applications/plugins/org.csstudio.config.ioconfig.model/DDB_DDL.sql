drop table MIME_FILES_DDB_MCPROTOTYPE_LNK cascade constraints
drop table MIME_FILES_DDB_NODES_LINK cascade constraints
drop table MIME_FILES_LINK cascade constraints
drop table dct_Records cascade constraints
drop table ddb_Channel_Prototype cascade constraints
drop table ddb_Facility cascade constraints
drop table ddb_GSD_File cascade constraints
drop table ddb_GSD_Module cascade constraints
drop table ddb_Ioc cascade constraints
drop table ddb_Profibus_Channel cascade constraints
drop table ddb_Profibus_Channel_Structure cascade constraints
drop table ddb_Profibus_Master cascade constraints
drop table ddb_Profibus_Module cascade constraints
drop table ddb_Profibus_Slave cascade constraints
drop table ddb_Profibus_Subnet cascade constraints
drop table ddb_Sensors cascade constraints
drop table ddb_node cascade constraints
drop table ddb_view_search_node cascade constraints
drop sequence SEQ_DDB
drop sequence hibernate_sequence
create table MIME_FILES_DDB_MCPROTOTYPE_LNK (prototype_id number(10,0) not null unique, docs_id varchar2(100 char) not null, primary key (prototype_id, docs_id))
create table MIME_FILES_DDB_NODES_LINK (docs_id number(10,0) not null unique, nodes_id varchar2(100 char) not null, primary key (docs_id, nodes_id))
create table MIME_FILES_LINK (id varchar2(100 char) not null, accountname varchar2(30 char), CREATED_DATE timestamp, DELETE_DATE timestamp, desclong varchar2(4000 char), entrydate timestamp, erroridentifyer varchar2(30 char), image blob, keywords varchar2(200 char), Link_Forward varchar2(200 char), LINK_ID varchar2(100 char), location varchar2(30 char), logseverity varchar2(16 char), MIME_TYPE varchar2(10 char), subject varchar2(200 char), UPDATE_DATE timestamp, primary key (id))
create table dct_Records (id number(10,0) not null, epics_name varchar2(255 char) not null, io_name varchar2(255 char) not null, record_type varchar2(255 char) not null, primary key (id))
create table ddb_Channel_Prototype (id number(10,0) not null, createdBy varchar2(255 char), createdOn timestamp, updatedBy varchar2(255 char), updatedOn timestamp, byteOrdering number(10,0), input number(1,0) not null, maximum number(10,0), minimum number(10,0), name varchar2(255 char) not null, offset number(10,0) not null, shift number(10,0) not null, structure number(1,0) not null, type number(10,0) not null, GSDModule_id number(10,0), primary key (id))
create table ddb_Facility (id number(10,0) not null, primary key (id))
create table ddb_GSD_File (id number(10,0) not null, GSDFile clob not null, name varchar2(255 char) not null unique, primary key (id))
create table ddb_GSD_Module (id number(10,0) not null, createdBy varchar2(255 char), createdOn timestamp, updatedBy varchar2(255 char), updatedOn timestamp, moduleId number(10,0) not null, name varchar2(255 char), GSDFile_id number(10,0), primary key (id), unique (GSDFile_id, moduleId))
create table ddb_Ioc (id number(10,0) not null, primary key (id))
create table ddb_Profibus_Channel (channelNumber number(10,0) not null, channelType number(10,0), currenUserParamDataIndex varchar2(255 char), currentValue varchar2(255 char), digital number(1,0) not null, epicsAddressString varchar2(255 char), input number(1,0) not null, ioName varchar2(255 char), CHSIZE number(10,0), id number(10,0) not null, channelStructure_id number(10,0), primary key (id))
create table ddb_Profibus_Channel_Structure (simple number(1,0) not null, structureType number(10,0), id number(10,0) not null, module_id number(10,0), primary key (id))
create table ddb_Profibus_Master (autoclear number(1,0) not null, dataControlTime number(10,0) not null, masterUserData varchar2(255 char), maxBusParaLen number(10,0) not null, maxNrSlave number(10,0) not null, maxSlaveDiagEntries number(10,0) not null, maxSlaveDiagLen number(10,0) not null, maxSlaveInputLen number(10,0) not null, maxSlaveOutputLen number(10,0) not null, maxSlaveParaLen number(10,0) not null, minSlaveInt number(10,0) not null, modelName varchar2(255 char), pollTime number(10,0) not null, profibusDPMasterId number(19,0) not null, profibusPnoId number(10,0) not null, profibusdpmasterBez varchar2(255 char), FDLADDRESS number(5,0), vendorName varchar2(255 char), id number(10,0) not null, GSDFile_id number(10,0), profibusSubnet_id number(10,0), primary key (id))
create table ddb_Profibus_Module (cfg_data varchar2(99 char), inputOffset number(10,0) not null, inputSize number(10,0) not null, moduleNumber number(10,0) not null, outputOffset number(10,0) not null, outputSize number(10,0) not null, id number(10,0) not null, slave_id number(10,0), primary key (id))
create table ddb_Profibus_Slave (fdlAddress number(10,0) not null, groupIdent number(10,0) not null, minTsdr number(10,0) not null, modelName varchar2(255 char), prmUserData varchar2(255 char), profibusPNoID number(10,0) not null, revision varchar2(255 char), slaveFlag number(10,0) not null, slaveType number(10,0) not null, stationStatus number(10,0) not null, vendorName varchar2(255 char), wdFact1 number(10,0) not null, wdFact2 number(10,0) not null, id number(10,0) not null, GSDFile_id number(10,0), profibusDPMaster_id number(10,0), primary key (id))
create table ddb_Profibus_Subnet (baudRate varchar2(255 char), cuLineLength float not null, gap number(5,0) not null, hsa number(5,0) not null, lwlLength float not null, masterNumber number(5,0) not null, maxTsdr number(10,0) not null, minTsdr number(10,0) not null, olmNumber number(5,0) not null, optionPar number(1,0) not null, profil varchar2(255 char), quotaFdlFmsS7com varchar2(255 char), repeaterNumber number(5,0) not null, slaveNumber number(5,0) not null, slotTime number(10,0) not null, subscriber number(5,0) not null, tqui number(5,0) not null, tset number(5,0) not null, ttr number(19,0) not null, watchdog number(10,0) not null, id number(10,0) not null, ioc_id number(10,0), primary key (id))
create table ddb_Sensors (id number(10,0) not null, ioName varchar2(255 char), selection varchar2(32 char), sensorID varchar2(32 char), primary key (id))
create table ddb_node (id number(10,0) not null, createdBy varchar2(255 char), createdOn timestamp, updatedBy varchar2(255 char), updatedOn timestamp, name varchar2(255 char), sortIndex number(5,0), description varchar2(255 char), version number(10,0) not null, parent_id number(10,0), primary key (id))
create table ddb_view_search_node (id number(10,0) not null, createdBy varchar2(255 char), createdOn timestamp, updatedBy varchar2(255 char), updatedOn timestamp, epicsAddressString varchar2(255 char), ioName varchar2(255 char), name varchar2(255 char), parent_id number(10,0), primary key (id))
alter table MIME_FILES_DDB_MCPROTOTYPE_LNK add constraint FK412EA7C68E285C18 foreign key (docs_id) references MIME_FILES_LINK
alter table MIME_FILES_DDB_MCPROTOTYPE_LNK add constraint FK412EA7C6B2AA0679 foreign key (prototype_id) references ddb_GSD_Module
alter table MIME_FILES_DDB_NODES_LINK add constraint FKA117B39864250FE2 foreign key (nodes_id) references MIME_FILES_LINK
alter table MIME_FILES_DDB_NODES_LINK add constraint FKA117B398BB2DF5CF foreign key (docs_id) references ddb_node
alter table ddb_Channel_Prototype add constraint FK3E5BCD891FD98EF7 foreign key (GSDModule_id) references ddb_GSD_Module
alter table ddb_Facility add constraint FKD5F5BE804E2C214B foreign key (id) references ddb_node
alter table ddb_GSD_Module add constraint FK2E2FC85033794DD7 foreign key (GSDFile_id) references ddb_GSD_File
alter table ddb_Ioc add constraint FK5A2512004E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Channel add constraint FKFCDD16D14E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Channel add constraint FKFCDD16D18B5CB1B7 foreign key (channelStructure_id) references ddb_Profibus_Channel_Structure
alter table ddb_Profibus_Channel_Structure add constraint FKA9FFCD654E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Channel_Structure add constraint FKA9FFCD656E89645F foreign key (module_id) references ddb_Profibus_Module
alter table ddb_Profibus_Master add constraint FK85A1ED44E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Master add constraint FK85A1ED4CFCE5E7D foreign key (profibusSubnet_id) references ddb_Profibus_Subnet
alter table ddb_Profibus_Master add constraint FK85A1ED433794DD7 foreign key (GSDFile_id) references ddb_GSD_File
alter table ddb_Profibus_Module add constraint FK9189ADEED265A71 foreign key (slave_id) references ddb_Profibus_Slave
alter table ddb_Profibus_Module add constraint FK9189ADE4E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Slave add constraint FK4AF0D7254E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Slave add constraint FK4AF0D7257013BB97 foreign key (profibusDPMaster_id) references ddb_Profibus_Master
alter table ddb_Profibus_Slave add constraint FK4AF0D72533794DD7 foreign key (GSDFile_id) references ddb_GSD_File
alter table ddb_Profibus_Subnet add constraint FK13A937CF4E2C214B foreign key (id) references ddb_node
alter table ddb_Profibus_Subnet add constraint FK13A937CF7E928F4E foreign key (ioc_id) references ddb_Ioc
alter table ddb_node add constraint FKEA8E003FC992C500 foreign key (parent_id) references ddb_node
alter table ddb_node add constraint FKEA8E003F6294E69D foreign key (parent_id) references ddb_Profibus_Channel_Structure
create sequence SEQ_DDB
create sequence hibernate_sequence
