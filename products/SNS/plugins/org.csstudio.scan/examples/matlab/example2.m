% Matlab Example: Submit scan, monitor progress, plot data
%
% @author: Kay Kasemir

%%
scan_setup

%% Connect to server
server = ScanServerConnector.connect();
server.getInfo()

%%
% Number points
N = 100;
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

seq = CommandSequence();
for i = 1:N
    seq.add(SetCommand('xpos', x(i)));
    seq.add(SetCommand('ypos', y(i)));
    seq.add(SetCommand('setpoint', 2.0, 'readback', true, 0.1, 0.0));
    seq.add(SetCommand('setpoint', 2.5, 'readback', true, 0.1, 0.0));
end
seq.dump();


%% Submit to server
id = server.submitScan('Matlab Scan', seq.getXML());

%% Wait for scan to finish
while ~ server.getScanInfo(id).getState().isDone()
    server.getScanInfo(id)

    scandata=server.getScanData(id);
    scandata.getDevices();
    xpos=scan_decode_samples(scandata.getSamples('xpos'));
    ypos=scan_decode_samples(scandata.getSamples('ypos'));

    % TODO Interpolate on matching time stamps
    % For now just cropping to same length
    N = min(length(xpos.val), length(ypos.val));
    plot(xpos.val(1:N), ypos.val(1:N), '.');
    legend('Position');
    xlabel('X');
    ylabel('Y');
    xlim([0 10]);
    ylim([0 10]);
    
    pause(1)
end

%% Not absolutely necessary, but nice: Disconnect when done
ScanServerConnector.disconnect(server)
