function d=timestamp2datenum(t)
% d = timestamp2datenum(t)
%
% Convert Java Timestamp as returned by archive API
% into Matlab datenum

vec = double(t.toPieces())';
d = datenum(vec(1:6));
