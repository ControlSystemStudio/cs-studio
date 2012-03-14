function table = scan_decode_spreadsheet(sheet)
% Decode ScanSamples from Scan Server for multiple devices into Matlab matrix
%
% This example decodes the time stamps and values for
% some device from the scan data:
%
%  data = server.getScanData(id)
%  sheet = SpreadsheetScanDataIterator(data, { 'xpos', 'ypos' })
%  table = scan_decode_spreadsheet(sheet)
%  plot(table(:,1), table(:,2), '-*');
%
% @author: Kay Kasemir

table = [];
while sheet.hasNext()
    samples = sheet.getSamples();
    row = zeros(1, samples.size());
    for i=1:samples.size()
        s = samples.get(i-1);
        if length(s) > 0
            row(i) = s.getValue();
        else
            row(i) = NaN;
        end
    end
    table(end+1,:) = row;
end
