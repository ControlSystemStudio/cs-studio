SELECT      mt.name messagetype, mpt.name property
FROM        msg_type mt, msg_property_type mpt, msg_type_property_type mtpt
WHERE       mt.id = mtpt.msg_type_id
AND         mpt.id = mtpt.msg_property_type_id
AND         mt.name LIKE 'css'
ORDER BY    mt.id, mpt.id;
