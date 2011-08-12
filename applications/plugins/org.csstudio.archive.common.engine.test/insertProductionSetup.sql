
--
-- ENGINES FOR PRODUCTIION ALL, MKK
--
insert into engine (name, description, url, alive) values ('KryoEngineAll', 'Kryo Production Engine', 'http://krynfsc.desy.de:4811/main', 0);

--
-- CHANNEL GROUPS FOR PRODUCTION
--
insert into channel_group (name, engine_id, description) values ('AMTF', 2, 'AMTF Channel Group');
insert into channel_group (name, engine_id, description) values ('KRYO', 2, 'KRYO Channel Group');
insert into channel_group (name, engine_id, description) values ('MKK', 2, 'MKK Channel Group');
insert into channel_group (name, engine_id, description) values ('TTF', 2, 'TTF Channel Group');
insert into channel_group (name, engine_id, description) values ('CMTB', 2, 'CMTB Channel Group');

--
-- CONTROL_SYSTEM
--
insert into control_system (name, type) values ('EpicsDefault', 'EPICS_V3'),('DoocsDefault', 'DOOCS'),('TangoDefault', 'TANGO');

--
-- CHANNEL
--
