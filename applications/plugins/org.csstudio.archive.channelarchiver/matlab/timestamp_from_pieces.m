function t = timestamp_from_pieces(year, month, day, hours, minutes, seconds)
% t = timestamp_from_pieces(year, month, day, hours, minutes, seconds)
%
% Create an archive time stamp from pieces
cal = java.util.Calendar.getInstance();
cal.clear();
% Set calendar to year, month-1 (!), day, hour, minute, seconds:
cal.set(year, month-1, day, hours, minutes, seconds);
t = org.csstudio.platform.data.TimestampFactory.fromCalendar(cal);
