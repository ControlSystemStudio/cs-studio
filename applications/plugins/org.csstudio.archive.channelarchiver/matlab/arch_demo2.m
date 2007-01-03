% See arch_demo1.m to get started.
%
% The basic getSamples() request only returns a
% limited number of samples,
% and one needs to iterate over the result;
% Be it to convert the time stamps or values
% into something suitable for plotting,
% or because one wants to compute e.g. an average,
% and doesn't really care to keep the samples.
%
% The 'crawl' API iterates over samples in a way
% that might be more convenient.

%% Connect to an archive server.
%  This is the main SNS one:
url='http://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi';
server = org.csstudio.archive.channelarchiver.ArchiveServer.getInstance(url);

%% Get archive keys, start/end times
keys = [ server.getArchiveKey('RF') server.getArchiveKey('RF llrf (last restart)') ]
t0 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 1, 18, 10, 18, 0, 0)
t1 = org.csstudio.archive.util.TimestampUtil.fromPieces(2006, 1, 18, 14, 0, 0, 0)

%% Get raw values
% In here, we could actually also receive strings,
% which we wouldn't handle properly,
% and we have to check ourselves if some samples are
% actually things like 'archive off' markers,
% indicated by their severity
servers = [ server server ];
iter = org.csstudio.archive.crawl.RawSampleIterator(servers, keys, 'CCL_LLRF:FCM1:cavAmpAvg', t0, t1);

%%
tim = [];
val = [];
while iter.hasNext() > 0
    sample = iter.next();
    tim = [ tim timestamp2datenum(sample.getTime()) ];
    sev = sample.getSeverity();
    if sev.hasValue()
        val = [ val sample.getValue() ];
    else
        fprintf(1, 'Not plottable: %s\n', char(sev.toString()));
        val = [ val nan ];
    end
end
units = char(sample.getMetaData().getUnits());

% Plot
plot(tim, val);
ylabel(units);
datetick('x', 'dd-mmm-yyyy HH:MM:SS');

%% Get double values
%  Uses the DoubleIterator filter to simplify the loop.
raw = org.csstudio.archive.crawl.RawSampleIterator(servers, keys, 'CCL_LLRF:FCM1:cavAmpAvg', t0, t1);
iter = org.csstudio.archive.crawl.DoubleSampleIterator(raw);

tim = [];
val = [];
while iter.hasNext() > 0
    sample = iter.next();
    tim = [ tim timestamp2datenum(sample.getTime()) ];
    val = [ val sample.getValue ];
end
units = char(sample.getMetaData().getUnits());

% Plot
plot(tim, val);
ylabel(units);
datetick('x', 'dd-mmm-yyyy HH:MM:SS');
