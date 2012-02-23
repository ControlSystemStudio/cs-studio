% Matlab Example: Submit scan, monitor progress, plot data
%
% @author: Kay Kasemir

%% Set path to Java classes
% Example for classes fetched from IDE, using the 'bin'
% subdir of the source code.
%
% For compiled plugins, use '/org.csstudio.scan_1.2.3'
% i.e. versioned plugin name, but then no 'bin' subdir.
%
% Instead of configuring the path in here one could also
% update the file matlab/toolbox/local/classpath.txt
basepath='/Kram/MerurialRepos/cs-studio/products/SNS/plugins';
path={};
path{1} = strcat(basepath, '/org.csstudio.scan/bin');
path{2} = strcat(basepath, '/org.csstudio.scan.client/bin');
javaclasspath(path)

%% Import scan system classes
import org.csstudio.scan.server.*
import org.csstudio.scan.client.*
import org.csstudio.scan.command.*

%% Connect to scan server
server=ScanServerConnector.connect();
server.getInfo()

%% Create a scan
% Matlab/Java quirk:
% The LoopCommand body and the CommandSequence expect
% an array of scan commands.
% But Matlab turns a one-element array into a skalar.
% For now, the easiest workaround seems to be:
% Add dummy DelayCommands to have at least 2 elements
% in the array.
scan=[
    LoopCommand('xpos', 1, 10, 1,[
      LoopCommand('ypos', 1, 5, 1,[
        SetCommand('setpoint', 2, 'readback', true, 0.1, 0.0)
        SetCommand('setpoint', 3, 'readback', true, 0.1, 0.0)
      ]),
      DelayCommand(0.1)
    ]),
    DelayCommand(0.1)
]

seq=CommandSequence(scan);
seq.dump();

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
while ~ server.getScanInfo(id).isDone()
    server.getScanInfo(id)
    pause(1)
end

%% Get data
scandata=server.getScanData(id);
scandata.getDevices()
xpos=DecodeScanSamples(scandata.getSamples('xpos'));
readback=DecodeScanSamples(scandata.getSamples('readback'));

%% Plot data
plot(xpos.time, xpos.val, readback.time, readback.val);
legend('xpos', 'readback');
datetick('x');

