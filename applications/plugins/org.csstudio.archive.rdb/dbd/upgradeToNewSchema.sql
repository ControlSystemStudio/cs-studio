# Set is_an_array retroactively for existing array_val data
#
# Lana Abadie

update sample t set t.is_an_array=true where t.channel_id||'|'||t.smpl_time||'|'||t.nanosecs in (select channel_id||'|'||smpl_time||'|'||nanosecs from array_val where t.channel_id=channel_id and t.smpl_time=smpl_time and t.nanosecs=nanosecs);
