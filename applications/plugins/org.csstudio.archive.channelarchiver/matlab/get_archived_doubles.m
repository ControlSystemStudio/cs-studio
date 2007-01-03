function [ tim, val ] = get_archived_doubles(servers, keys, channel, start_time, end_time)
% [ tim, val ] = get_archived_doubles(servers, keys, channel, start_time, end_time)
%
% Get samples from the archive and convert them to datenum and double
% vectors.
%
% servers    ArchiveServer instances.
% keys       Array of archive keys (Length must match 'servers')
% channel    Channel name
% start_time start time stamp
% end_time   end time stamp
%
% Example:
%  url='http://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi';
%  server = org.csstudio.archive.channelarchiver.ArchiveServer.getInstance(url);
%  servers = [ server server ];
%  keys = [ server.getArchiveKey('RF') server.getArchiveKey('RF llrf (last restart)') ];
%  t0 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 10, 24, 0, 0, 0, 0);
%  t1 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 10, 26, 0, 0, 0, 0);
%  [ ccl1_t, ccl1_val ] = get_archived_doubles(servers, keys, 'CCL_LLRF:FCM1:cavAmpAvg', t0, t1);
%  [ ccl2_t, ccl2_val ] = get_archived_doubles(servers, keys, 'CCL_LLRF:FCM2:cavAmpAvg', t0, t1);
%  plot(ccl1_t, ccl1_val, ccl2_t, ccl2_val);
%  legend('CCL 1', 'CCL 2');
%  datetick('x', 'dd-mmm-yyyy HH:MM:SS');
%
% This example will first search the 'RF', then the 'RF llrf (last
% restart)' archive for samples from start time t0 to end time t1,
% and plot the result.

%  Uses the DoubleIterator filter to simplify the loop.
raw = org.csstudio.archive.crawl.RawSampleIterator(servers, keys, channel, start_time, end_time);
iter = org.csstudio.archive.crawl.DoubleSampleIterator(raw);
tim = [];
val = [];
while iter.hasNext() > 0
    sample = iter.next();
    tim = [ tim timestamp2datenum(sample.getTime()) ];
    val = [ val sample.getValue() ];
end

