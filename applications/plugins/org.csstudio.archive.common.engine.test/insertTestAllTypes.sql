--
-- FOR TEST
--

-- ENGINE
insert into engine (name, description, url, alive) values ('TestAllTypesEngine', 'Engine capturing all possible types', 'http://krykpcj.desy.de
:4811/main', 0);
insert into engine (id, name, description, url, alive) values (3, 'TestSingleChannelEngine', 'Engine capturing a single channel', 'http://krykpcj.desy.de
:4811/main', 0);
-- CHANNEL_GROUP
insert into channel_group (name, engine_id, description) values ('TestAllTypesGroup', 1, 'Group capturing one channel per allowed data type');
insert into channel_group (name, engine_id, description) values ('TestSingleChannelGroup', 3, 'Group capturing exactly one channel');
-- CONTROL_SYSTEM
insert into control_system (name, type) values ('EpicsDefault', 'EPICS_V3'),('DoocsDefault', 'DOOCS'),('TangoDefault', 'TANGO');
-- CHANNEL
insert into channel (name, datatype, group_id, control_system_id) 
       values ('AT_char',    'Byte', 1, 1),
('AT_uchar',   'Byte', 1, 1),
('AT_short',   'Short', 1, 1),
('AT_ushort',  'Integer', 1, 1),
('AT_long',    'Long', 1, 1),
('AT_ulong',   'Long', 1, 1),
('AT_float.A0','Float', 1, 1),
('AT_double',  'Double', 1, 1),
('AT_string',  'String', 1, 1),
('AT_enum',    'EpicsEnum', 1, 1),
('AT_char_wf',   'ArrayList<Byte>', 1, 1),
('AT_uchar_wf',  'ArrayList<Byte>', 1, 1),
('AT_short_wf',  'ArrayList<Short>', 1, 1),
('AT_ushort_wf', 'LinkedHashSet<Short>', 1, 1),
('AT_long_wf',   'Vector<Long>', 1, 1),
('AT_ulong_wf',  'LinkedList<Long>', 1, 1),
('AT_float_wf',  'LinkedList<Float>', 1, 1),
('AT_double_wf', 'ArrayList<Double>', 1, 1),
('AT_string_wf', 'TreeSet<String>', 1, 1),
('AT_enum_wf',   'HashSet<EpicsEnum>', 1, 1),
('AT_menu',      'EpicsEnum', 1, 1),
('AT_mbbi_raw_to_string',      'EpicsEnum', 1, 1),
('AT_mbbi_raw_to_string.RVAL', 'Double', 1, 1);

--
-- END FOR TEST
--