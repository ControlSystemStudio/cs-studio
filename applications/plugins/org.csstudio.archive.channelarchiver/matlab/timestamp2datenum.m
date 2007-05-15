function d=timestamp2datenum(t)
% d = timestamp2datenum(t)
%
% Convert Java Timestamp as returned by archive API
% into Matlab datenum

d = datenum(char(t.toString()), 'yyyy/mm/dd HH:MM:SS');
