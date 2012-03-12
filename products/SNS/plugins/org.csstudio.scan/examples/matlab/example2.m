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
N = 8;
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
    seq.set('xpos', x(i));
    seq.set('ypos', y(i));
%    seq.add(SetCommand('setpoint', 2.0, 'readback', true, 0.1, 0.0));
%    seq.add(SetCommand('setpoint', 2.5, 'readback', true, 0.1, 0.0));
end
seq.dump();


%% Submit to server
id = server.submitScan('Matlab Scan', seq.getXML());
 
%% Wait for scan to finish
while 1
    info = server.getScanInfo(id)

    scandata=server.getScanData(id);
    scandata.getDevices();
    xpos=scan_decode_samples(scandata.getSamples('xpos'));
    ypos=scan_decode_samples(scandata.getSamples('ypos'));

    % TODO Interpolate on matching time stamps
    [xt, i] = unique(xpos.time);
    xinterp = interp1(xt, xpos.val(i), ypos.time, 'linear');

    plot(x, y, '-', xinterp, ypos.val, '-');
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

%% Dump the data
data = server.getScanData(id)
sheet = SpreadsheetScanDataIterator(data, { 'xpos', 'ypos' })
sheet.dump(System.out)


%%
data=[
	7.493959207434934	8.12732592987212
	4.109916264174743	8.12732592987212
	4.109916264174743	8.899711648727294
	1.3961245283903239	8.899711648727294
	1.3961245283903239	6.735534956470233
	1.3961245283903239	6.735534956470233
	1.3961245283903239	3.2644650435297677
	4.109916264174742	3.2644650435297677
	4.109916264174742	1.1002883512727055
	7.4939592074349335	1.1002883512727055
	7.4939592074349335	1.8726740701278803
	9.0	1.8726740701278803
	9.0	4.999999999999999
	7.493959207434935	4.999999999999999
	7.493959207434935	8.127325929872118
]
plot(x, y, '-', data(:,1), data(:,2), '-*');

%% Not absolutely necessary, but nice: Disconnect when done
ScanServerConnector.disconnect(server)
