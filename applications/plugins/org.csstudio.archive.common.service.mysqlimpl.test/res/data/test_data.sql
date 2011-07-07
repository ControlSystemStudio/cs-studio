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
       
