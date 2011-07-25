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
              