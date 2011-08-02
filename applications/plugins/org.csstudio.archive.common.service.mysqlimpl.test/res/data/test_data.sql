--
-- Engine(s)
-- 
INSERT INTO engine (id, name, description, url, alive) 
       VALUES (1, 'TestEngine', 'TestEngineDescription', 'http://krykpcj.desy.de:4811', unix_timestamp('2011-07-01 00:00:01')*1000000000);

--
-- Channel Group(s)
--        
INSERT INTO channel_group (id, name, engine_id, description) 
       VALUES (1, 'TestGroup1', 1, 'TestGroupDescription'),
              (2, 'TestGroup2', 1, NULL);
       
--
-- Engine Status
--        
INSERT INTO engine_status (id, engine_id, status, time, info)
       VALUES (1, 1, 'ON', unix_timestamp('2011-06-01 00:00:01')*1000000000, "Started"),
              (2, 1, 'OFF', unix_timestamp('2011-07-01 00:00:01')*1000000000, "Stopped");

--
-- Control System
--        
INSERT INTO control_system (id, name, type)
       VALUES (1, 'EpicsDefault', 'EPICS_V3'),
              (2, 'DoocsDefault', 'DOOCS'),
              (3, 'TangoDefault', 'TANGO');
              
--
-- Channels
--        
INSERT INTO channel (id, name, description, datatype, group_id, last_sample_time, control_system_id, display_high, display_low)
       VALUES (1, 'doubleChannel1', 'test double channel 1', 'Double', 1, unix_timestamp('2011-01-01 00:00:01')*1000000000, 1, '20.0', '10.0'),
              (2, 'doubleChannel2', 'test double channel 2', 'Double', 1, unix_timestamp('2010-01-01 01:02:03')*1000000000, 1, '25.0', '5.0'),
              (3, 'byteChannel1', 'test byte channel 1', 'Byte', 2, unix_timestamp('2011-05-01 00:00:01')*1000000000, 1, '127', '-128'),
              (4, 'enumChannel1', 'test enum channel 1', 'EpicsEnum', 2, unix_timestamp('2010-01-01 01:02:03')*1000000000, 1, NULL, NULL);
              
--
-- Samples
--        
INSERT INTO sample (channel_id, time, value) 
       VALUES (3, unix_timestamp('1970-01-01 00:00:02')*1000000000, '26');    