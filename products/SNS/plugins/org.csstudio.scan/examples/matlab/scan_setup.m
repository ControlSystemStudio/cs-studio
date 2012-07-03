% Scan System Setup
%
%   scan_setup
%  
% @author: Kay Kasemir

%% Prevent multiple setups because Matlab tends to crash when doing that
imports = import;
for i = 1:length(imports)
    imp = imports(i)
    if strcmp(imp, 'org.csstudio.scan.server.*')
        fprintf(1, 'Scan System already configured\n')
        return
    end
end
fprintf(1, 'Configuring Scan System ...\n')

%% Set path to Java classes
% Instead of configuring the path in here one could also
% update the file matlab/toolbox/local/classpath.txt

% Ideally, use the standalone client JAR
% created from org.csstudio.scan.client/build.xml
%javaclasspath('/usr/local/css/scan.client.jar')

% Alternatively, use use classes fetched from IDE,
% using the 'bin' subdir of the source code.
% Or the compiled plugins of a CSS installation that includes
% the scan system, in which case you need to specify the exact
% version number of the plugin, like '/org.csstudio.scan_1.2.3',
% and there's no 'bin' subdir.
%
basepath='/Kram/MerurialRepos/cs-studio-3.1/products/SNS/plugins';
path={};
path{1} = strcat(basepath, '/org.csstudio.scan/bin');
path{2} = strcat(basepath, '/org.csstudio.scan.client/bin');
javaclasspath(path)

%% Import scan system classes
import org.csstudio.scan.server.*
import org.csstudio.scan.client.*
import org.csstudio.scan.command.*
import org.csstudio.scan.data.*

% Default is localhost:4810. Change as needed
import java.lang.System
%System.setProperty(ScanServer.HOST_PROPERTY, 'ky9linux.ornl.gov');
%System.setProperty(ScanServer.PORT_PROPERTY, '4810');
