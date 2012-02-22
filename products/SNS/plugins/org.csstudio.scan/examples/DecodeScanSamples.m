function data = DecodeScanSamples(samples)
% Decode ScanSamples from Scan Server into Matlab data
%
%  samples = scandata=server.getScanData(id).getSamples('some_device');
%  data = DecodeScanSamples(samples)
%  plot(data.time, data.val);
%  datetick('x');

% @author: Kay Kasemir

data.time=[];
data.val=[];
for i=1:samples.size()
    data.time(i) = JDate2MDate(samples.get(i-1).getTimestamp());
    data.val(i) = samples.get(i-1).getValue();
end
