SELECT   m.id MESSAGE_ID, mt.name MESSAGE_TYPE, m.datum EVENTTIME, mpt.name PROPERTY, mc.value VALUE
FROM     msg_type mt, message m, msg_property_type mpt, message_content mc
WHERE    m.id = mc.message_id
AND      mc.msg_property_type_id = mpt.id
AND      mt.id = m.msg_type_id
--AND      m.msg_type_id = 3
-- AND      mpt.name LIKE 'TYPE'
-- AND      mc.value LIKE '%Sms%'
AND      m.datum > str_to_date('2008-06-11 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
--AND      m.datum < to_date('2008-03-11 09:00:00', 'YYYY-MM-DD HH24:MI:SS')
ORDER BY m.id, mc.msg_property_type_id;
