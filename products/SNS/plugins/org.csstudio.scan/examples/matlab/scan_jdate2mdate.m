function [ mdate ] = scan_jdate2mdate(jdate)
% Convert Java Date to Matlab datenum
%
%  num = scan_jdate2mdate(java.util.Date())
%  datestr(num)

% @author: Kay Kasemir

% It's really stupid to convert to string and back,
% for for now that's the easiest
sdf=java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:ss.SS');
date_str = char(cell(sdf.format(jdate)));
mdate = datenum(date_str, 'yyyy-mm-dd HH:MM:SS.FFF');