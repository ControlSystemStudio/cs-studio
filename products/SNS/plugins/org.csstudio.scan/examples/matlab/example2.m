% Matlab Example:
% Submit scan, monitor progress, plot data
% for a scan that tries to approximate a circular path
% via coarse X/Y steps
%
% @author: Kay Kasemir

%%
scan_setup

%% Connect to server
server = ScanServerConnector.connect();
server.getInfo()

%%
% Number of points
N = 20;
% Center, diameter
C = 5;
D = 4;
% Create circle
i = 1:N;
x = C + D*cos(2*pi*i/(N-1));
y = C + D*sin(2*pi*i/(N-1));
plot(x, y, '.');
xlim([0 10]);
ylim([0 10]);

%% Create a scan
% Scan x/y, at each step waiting for readback to follow setpoint

scan = java.util.ArrayList;
for i = 1:N
    scan.add(SetCommand('xpos', x(i)));
    scan.add(SetCommand('ypos', y(i)));
    scan.add(DelayCommand(0.5));
    scan.add(LogCommand({ 'xpos', 'ypos' }));
end

seq = CommandSequence(scan);
seq.dump();


%% Submit to server
id = server.submitScan('Matlab Scan', seq.getXML());
 
%% Wait for scan to finish
while 1
    info = server.getScanInfo(id)
    scandata = server.getScanData(id);
    scandata.getDevices();
    
    sheet = ScanDataIterator(scandata, { 'xpos', 'ypos' });
    table = scan_decode_spreadsheet(sheet);
  
    plot(x, y, 'g-', table(:,1), table(:,2), 'b-*');
    legend('Path', 'Scan Points');
    xlabel('X');
    ylabel('Y');
    xlim([0 10]);
    ylim([0 10]);
    
    if info.getState().isDone()
        break
    end
    
    pause(1)
end

%% Not absolutely necessary, but nice: Disconnect when done
ScanServerConnector.disconnect(server)
