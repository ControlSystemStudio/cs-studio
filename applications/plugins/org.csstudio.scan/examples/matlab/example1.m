% Matlab Example:
% Submit scan, monitor progress, plot data
% for a 2-D X/Y scan going up/down/left/right
%
% @author: Kay Kasemir

%%
scan_setup

%% Connect to server
server=ScanServerConnector.connect();
server.getInfo()

%% Create a scan
% Scan x/y, at each step waiting for readback to follow setpoint
scan=[
    LoopCommand('xpos', 1, 10, 1,[
        LoopCommand('ypos', 1, 5, -1,[
            SetCommand('setpoint', 2, 'readback', true, 0.1, 0.0)
            SetCommand('setpoint', 2.5, 'readback', true, 0.1, 0.0)
            LogCommand({ 'xpos', 'ypos', 'readback' })
      ])
    ])
];

seq=CommandSequence(scan);
seq.dump();

%% Submit to server
id=server.submitScan('Matlab Scan', seq.getXML());

%% Play with pause, resume
% Wait for scan to start
while server.getScanInfo(id).getState() == ScanState.Idle
    fprintf 1, 'Waiting for scan to start...'    
    pause(1)
end
% Then pause it for a little while
server.pause(id)
fprintf 1, 'Paused...'    
pause(2)
server.resume(id)
fprintf 1, 'Resume...'

%% Wait for scan to finish
while ~ server.getScanInfo(id).getState().isDone()
    server.getScanInfo(id)
    pause(1)
end

%% Get data
scandata=server.getScanData(id);
scandata.getDevices()
xpos=scan_decode_samples(scandata.getSamples('xpos'));
readback=scan_decode_samples(scandata.getSamples('readback'));

%% Plot data
plot(xpos.time, xpos.val, readback.time, readback.val);
legend('xpos', 'readback');
datetick('x');

%% Not absolutely necessary, but nice: Disconnect when done
ScanServerConnector.disconnect(server)
