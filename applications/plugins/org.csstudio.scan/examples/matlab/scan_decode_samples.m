function data = scan_decode_samples(samples)
% Decode ScanSamples from Scan Server into Matlab data
%
% This example decodes the time stamps and values for
% some device from the scan data:
%
%  samples = scandata=server.getScanData(id).getSamples('some_device');
%  data = scan_decode_samples(samples)
%  plot(data.time, data.val);
%  datetick('x');

% @author: Kay Kasemir

data.time=[];
data.val=[];
for i=1:samples.size()
    data.time(i) = scan_jdate2mdate(samples.get(i-1).getTimestamp());
    data.val(i) = samples.get(i-1).getNumber(0);
end
