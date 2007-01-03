% Example how Matlab can access the JavaArchiveClient classes
%
% This is not one big example program,
% but a tutorial of things to try one after the other
% in a cut-and-paste mode,
% or via the matlab 'evaluate cell' mode.

%% Connect to an archive server.
%  This is the main SNS one:
url='http://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi';
server = org.csstudio.archive.channelarchiver.ArchiveServer.getInstance(url);

% Dump some basic info
% Note that the command-line completion of the Matlab command window
% can now be very helpful:
% Type 'server.<tab>', and a popup-list will show all the methods,
% including the following ones:
description = server.getDescription()
version = server.getVersion()
% Of course some methods might not make any sense unless one reads
% the full Javadoc, which is not accessible from inside Matlab.

%% Connect, locate archive key
%  All requests use the numeric 'key' to select the (sub) archive.
%  List all archives and their keys
archive_infos = server.getArchiveInfos();
%  Each archive has a name and key. Print that.
for i=1:length(archive_infos)
    fprintf(1, '%4d : "%s"\n', archive_infos(i).getKey(), char(archive_infos(i).getName()))
end
%  Note concerning Strings:
%  The name is a Java 'String', which works just like a Matlab string
%  in many, but not all cases. fprintf is one such exception, where
%  specific conversion from a Java String to a Matlab string via char()
%  is required.

%  Note concerning arrays:
%  Compared to the Java code,
%  Matlab uses 1..length for indices, not 0..(length-1)!

%% Get a key for a known archive by name.
%  For the SNS, one should find these two:
key = server.getArchiveKey('- All -')
key = server.getArchiveKey('- All - (last restart)')
%  ... and they will work for almost all the requests,
%  but going to a specific sub-archive can be quicker
%  and also avoid confusion in case a channel was archived
%  in more than one sub-archive.
key = server.getArchiveKey('Admin')
key = server.getArchiveKey('Admin iocHealth (last restart)')
key = server.getArchiveKey('RF llrf (last restart)')
key = server.getArchiveKey('RF')

% One can also get the name for a key:
server.getArchiveName(key)

%% Find info about channels in one sub-archive
names = server.getNames(key, 'CCL_LLRF:FCM.*:cavAmpAvg');
%%
for i=1:names.length
    name = char(names(i).getName());
    t0 = char(names(i).getStart());
    t1 = char(names(i).getEnd());
    fprintf(1, '%s - %s: %s\n', t0, t1, name);
end

%% Timestamps
%  The archive library uses a Java Timestamp class.
%  Above, the getStart()/getEnd() calls returned such Timestamps,
%  which were converted to string by implicit toString() calls:
%    names(i).getStart().toString() ...
%  This is one way to create a Timestamp for a given date & time,
%  using year, month, day, hour, minute, secs, nanosecs:
t0 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 1, 18, 10, 18, 0, 0)
t1 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 1, 18, 14, 0, 0, 0)

%% These are ways to convert back and forth.
% Timestamp -> Matlab datenum
% ..toPieces gives year, month, day, hour, minute, sec, nanosec
% as in64.
% datenum needs a 'double' row vector with only year ... sec:
t0.toPieces()
vec = double(t0.toPieces())';
num = datenum(vec(1:6))
datestr(num)

%% Matlab datenum -> Timestamp
num = datenum(2006, 1, 30, 13, 30, 10.0001)
% Given the 2006, 1, ... pieces, one could of course directly
% use Timestamp.fromPieces(), but let's assume we only have the
% datenum, and want to get a Timestamp:
vec = datevec(num)
t = org.csstudio.archive.util.TimestampUtil.fromPieces(vec(1), vec(2), vec(3), vec(4), vec(5), vec(6), 0)
% Note that there might be some rounding error:
% With 10 seconds, I get 13:30:09 in the end,
% but with 10.0001 it's OK.

%% Data request types
% The data server supports several request types.
% One can list them by name:
server.getRequestTypes()

% ... or get the request code for a specific type
request = server.getRequestType('raw')

% .. in which case one best uses the pre-defined string constants
server.GET_RAW
request = server.getRequestType(server.GET_RAW)

%% Get Data
result = server.getSamples(key, { 'CCL_LLRF:FCM1:cavAmpAvg' }, t0, t1, request, 10);
% Always returns an array, one element per channel.
% Get the first result for the one and only channel we requested:
result = result(1)
%% The result contains some meta info as well as the actual samples:
result.getChannelName()
samples = result.getSamples();
samples(1).getMetaData()
samples(1).getMetaData().getUnits()

%% The sample has a time stamp, status, severity, and value.
% One can format them by hand, so you have full control:
for i = 1:samples.length
    s = samples(i);
    fprintf(1, 'Sample: %s %g %s %s %s\n', ...
        char(s.getTime()),...
        s.getValue(),...
        char(s.getMetaData().getUnits()), ...
        char(s.getStatus()),...
        char(s.getSeverity()));
end
%% .. or ask the format() method to do this for the value
% which picks some defaults:
for i = 1:samples.length
    fprintf(1, '%s\n', char(samples(i).format()));
end

%% .. or ask the sample for the full string:
for i = 1:samples.length
    fprintf(1, '%s\n', char(samples(i).toString()));
end

%% Another data example, reading two channels
key = server.getArchiveKey('RF');
channels = { 'CCL_LLRF:FCM1:cavAmpAvg', 'CCL_LLRF:FCM2:cavAmpAvg' };
t0 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 1, 18, 10, 18, 0, 0);
t1 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 1, 18, 14, 0, 0, 0);
request = server.getRequestType(server.GET_RAW);
result = server.getSamples(key, channels, t0, t1, request, 1000);
%% Extract time and values as matlab date and double vectors for plotting
samples = result(1).getSamples();
ccl1_t = [];
ccl1_v = [];
for i=1:length(samples)
    ccl1_t = [ ccl1_t timestamp2datenum(samples(i).getTime()) ];
    ccl1_v = [ ccl1_v samples(i).getValue() ];
end

samples = result(2).getSamples();
ccl2_t = [];
ccl2_v = [];
for i=1:length(samples)
    ccl2_t = [ ccl2_t timestamp2datenum(samples(i).getTime()) ];
    ccl2_v = [ ccl2_v samples(i).getValue() ];
end
% Plot
plot(ccl1_t, ccl1_v, ccl2_t, ccl2_v);
legend('CCL 1', 'CCL 2');
ylabel(char(samples(1).getMetaData().getUnits()));
datetick('x', 'dd-mmm-yyyy HH:MM:SS');
