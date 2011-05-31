--
-- Engine(s)
-- 
INSERT INTO engine (name, description, url) VALUES('TestEngine', 'TestEngineDesc', 'http://krykpcj.desy.de:4811');
-- BK: statement tested

--
-- Channel group(s)
--
INSERT INTO channel_group (name, engine_id, description) VALUES('TestGroup', 1, 'TestGroupDesc');
-- BK: statement tested

--
-- Channels
--
INSERT INTO channel (name, datatype, metatype, group_id, sample_mode_id, sample_period) 
       VALUES ('AT_char',   'Byte',   'Scalar', 1, 1, 1.0),
              ('AT_uchar',  'Byte',   'Scalar', 1, 1, 1.0),
              ('AT_short',  'Short',  'Scalar', 1, 1, 1.0),
              ('AT_ushort', 'Short',  'Scalar', 1, 1, 1.0),
              ('AT_long',   'Long',   'Scalar', 1, 1, 1.0),
              ('AT_ulong',  'Long',   'Scalar', 1, 1, 1.0),
              ('AT_float',  'Float',  'Scalar', 1, 1, 1.0),
              ('AT_double', 'Double', 'Scalar', 1, 1, 1.0),
              ('AT_string', 'String', 'Scalar', 1, 1, 1.0),
              ('AT_enum',   'Enum',   'Scalar', 1, 1, 1.0),
              ('AT_char_wf',   'Byte',   'Array', 1, 1, 1.0),
              ('AT_uchar_wf',  'Byte',   'Array', 1, 1, 1.0),
              ('AT_short_wf',  'Short',  'Array', 1, 1, 1.0),
              ('AT_ushort_wf', 'Short',  'Array', 1, 1, 1.0),
              ('AT_long_wf',   'Long',   'Array', 1, 1, 1.0),
              ('AT_ulong_wf',  'Long',   'Array', 1, 1, 1.0),
              ('AT_float_wf',  'Float',  'Array', 1, 1, 1.0),
              ('AT_double_wf', 'Double', 'Array', 1, 1, 1.0),
              ('AT_string_wf', 'String', 'Array', 1, 1, 1.0),
              ('AT_enum_wf',   'Enum',   'Array', 1, 1, 1.0);
