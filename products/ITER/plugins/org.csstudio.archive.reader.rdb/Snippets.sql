

-- Count samples
select count(*) from chan_arch.sample where channel_id=58418 and smpl_time between TIMESTAMP '2010-01-21 00:00:00' and TIMESTAMP '2010-02-04 00:00:00';

-- Get time of last sample at-or-before the start time
SELECT smpl_time FROM (SELECT smpl_time FROM chan_arch.sample WHERE channel_id=58418
    AND smpl_time <= TIMESTAMP '2010-01-21 00:00:00' ORDER BY smpl_time desc ) WHERE ROWNUM = 1;

-- Get raw samples
select * from chan_arch.sample where channel_id=58418 and smpl_time between TIMESTAMP '2010-01-21 00:00:00' and TIMESTAMP '2010-02-04 00:00:00';

-- Invoke stored procedure
select chan_arch_sns.archive_reader_pkg.get_browser_data(58418, TIMESTAMP '2010-01-21 00:00:00', TIMESTAMP '2010-02-04 00:00:00', 50) FROM DUAL;
