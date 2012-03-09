% Matlab Example: Submit scan, monitor progress, plot data
%
% @author: Kay Kasemir

%% Set path to Java classes
% Instead of configuring the path in here one could also
% update the file matlab/toolbox/local/classpath.txt

% Ideally, use the standalone client JAR
% created from org.csstudio.scan.client/build.xml
javaclasspath('/usr/local/css/scan.client.jar')

% Alternatively, use use classes fetched from IDE,
% using the 'bin' subdir of the source code.
% Or the compiled plugins of a CSS installation that includes
% the scan system, in which case you need to specify the exact
% version number of the plugin, like '/org.csstudio.scan_1.2.3',
% and there's no 'bin' subdir.
%
%basepath='/Kram/MerurialRepos/cs-studio/products/SNS/plugins';
%path={};
%path{1} = strcat(basepath, '/org.csstudio.scan/bin');
%path{2} = strcat(basepath, '/org.csstudio.scan.client/bin');
%javaclasspath(path)

%% Import scan system classes
import org.csstudio.scan.server.*
import org.csstudio.scan.client.*
import org.csstudio.scan.command.*

%% Connect to scan server
% Default is localhost:4810. Change as needed
import java.lang.System
%System.setProperty(ScanServer.HOST_PROPERTY, 'ky9linux.ornl.gov');
%System.setProperty(ScanServer.PORT_PROPERTY, '4810');

server=ScanServerConnector.connect();
server.getInfo()

%% Create a scan
% Scan x/y, at each step waiting for readback to follow setpoint
scan=[
    LoopCommand('xpos', 1, 10, 1,[
        LoopCommand('ypos', 1, 5, -1,[
            SetCommand('setpoint', 2, 'readback', true, 0.1, 0.0)
            SetCommand('setpoint', 3, 'readback', true, 0.1, 0.0)
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
xpos=DecodeScanSamples(scandata.getSamples('xpos'));
readback=DecodeScanSamples(scandata.getSamples('readback'));

%% Plot data
plot(xpos.time, xpos.val, readback.time, readback.val);
legend('xpos', 'readback');
datetick('x');

%% Not absolutely necessary, but nice: Disconnect when done
ScanServerConnector.disconnect(server)
