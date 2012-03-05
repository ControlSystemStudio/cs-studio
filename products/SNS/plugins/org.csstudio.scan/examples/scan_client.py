"""
Scan Client Tools

Helpers for connecting to the Scan Server,
to assemble commands for a scan,
submit them to the server,
monitor the execution.

Shortcuts for 1D, 2D, *D scans.

This code depends on the basic org.csstudio.scan.*
packages and can be invoked from Jython command
lines outside of CSS.

@author: Kay Kasemir
"""

# -------------------------------------------------------
# Path setup

# For now this is hard coded in here to allow use
# from BOY scripts
import sys

# Path to scan client library
# Should point to the scan client jar
scan_client_jar="/Kram/MerurialRepos/cs-studio/products/SNS/plugins/org.csstudio.scan.client/scan.client.jar"

# Alternatively, especially during development,
# can use the binaries that the IDE creates within
# the workspace
workspace="/Kram/MerurialRepos/cs-studio/products/SNS/plugins"

# Avoid Python os.path because that doesn't exist in stripped-down jython.jar
import java.io.File as File
 
#
if File(scan_client_jar).exists():
    # print "Using Scan Client Jar " + scan_client_jar
    sys.path.append(scan_client_jar)
elif File(workspace).isDirectory():
    # print "Using Workspace " + workspace
    sys.path.append(workspace + "/org.csstudio.scan/bin")
    sys.path.append(workspace + "/org.csstudio.scan.client/bin")
else:
    raise Exception("Scan client library not configured")

# -------------------------------------------------------
# Scan Server connection setup

import org.csstudio.scan.server.ScanServer as ScanServer
import java.lang.System as System
    
# Set scan server host and port if they're not the default.
# Can also pass this as command-line arg to jython:
#  jython  -DScanServerHost=ky9linux.ornl.gov ....
#System.setProperty(ScanServer.HOST_PROPERTY, "ky9linux.ornl.gov")
#System.setProperty(ScanServer.PORT_PROPERTY, str(4810))


# -------------------------------------------------------
# Leave rest as is

# Python packages are different from Java Packages
# There can be issues with 'package scanning' that cause
# jython to not find classes when using
#   from org.csstudio.scan.command import *
# or
#   import org.csstudio.scan.command
#
# The most dependable way is to explicitly import one-by-one
import org.csstudio.scan.client.ScanServerConnector as ScanServerConnector
import org.csstudio.scan.command.CommandSequence as CommandSequence
import org.csstudio.scan.command.LoopCommand as LoopCommand
import org.csstudio.scan.command.Comparison as Comparison
import org.csstudio.scan.command.ScanCommand as ScanCommand
import org.csstudio.scan.command.WaitCommand as WaitCommand
import org.csstudio.scan.command.DelayCommand as DelayCommand
import org.csstudio.scan.command.LogCommand as LogCommand
import org.csstudio.scan.command.SetCommand as SetCommand

import time


class ScanClient(object):
    """
    Base class for a scan client
    
    Can submit scans to the server and monitor them
    """
    def __init__(self):
        # Connection to the scan server
        self.server = ScanServerConnector.connect()
        # Scan ID
        self.id = -1
        
    def checkServer(self):
        """
        Attempt to call the server, and try to re-connect on error.
        
        The server could be restarted, or there could have been
        a network issue between the time we originally connected
        to the server and now, which would invalidate the original
        server connection.
        """
        try:
            self.server.getInfo()
        except:
            self.server = ScanServerConnector.connect()

    def submit(self, name, command_sequence):
        """
        Submit a CommandSequence to the server for execution
        
        @param name  Name of the scan
        @param command_sequence  CommandSequence
          
        @return Scan ID
        """
        self.checkServer()
        self.id = self.server.submitScan(name, command_sequence.getXML())
        return self.id

    def getScanInfo(self, id=-1):
        """
        Get scan info
        
        @param id Scan ID, defaulting to the last submitted scan
        """
        self.checkServer()
        if id == -1:
            id = self.id
        return self.server.getScanInfo(id)

    def waitUntilDone(self, id=-1):
        """
        Wait until a submitted scan has finished
        
        @param id Scan ID, defaulting to the last submitted scan
        """
        while True:
            info = self.getScanInfo(id)
            print info
            if info.getState().isDone():
                break;
            time.sleep(1.0)
            
    def __str__(self):
        return "Scan client, connected to %s" % self.server.getInfo()



class ScanNd(ScanClient):
    """
    N-dimensional scan that logs arbitrary number of readings
    based on nested loops.
    
    Arguments:
    
    * Optional scan name
    * One or more scan specifications: ('device', start, end[, step])
    * Names of device to log in addition to loop'ed devices
    * Commands to perform in innermost loop
    
    Examples:
    
    # Scan 'xpos' from 1 to 10, stepping 1
    scan('My first one', ('xpos', 1, 10) )
    # Scan name is optional
    scan( ('xpos', 1, 10) )

    # Log the 'readback' (xpos is logged automatically)
    scan( ('xpos', 1, 10), 'readback')
    
    # Scan 'xpos' from 1 to 10, stepping 1,
    # inside that looping 'ypos' from 1 to 5 by 0.2,
    # logging 'readback'
    scan('XY Example', ('xpos', 1, 10), ('ypos', 1, 5, 0.2), 'readback')

    # Scan 'xpos' and 'ypos', set something to '1' and then '3' (with readback)
    scan('XY Example', ('xpos', 1, 10), ('ypos', 1, 5, 0.2),
         SetCommand('setpoint', 1, 'readback'),
         SetCommand('setpoint', 3, 'readback'))
    """
    
    def __init__(self):
        ScanClient.__init__(self)

    def _decodeScan(self, parms):
        """ Check for 
                ('device', start, end, step)
             or 
                ('device', start, end)
             for a default step size of 1
        """
        if (len(parms) == 4):
            return (parms[0], parms[1], parms[2], parms[3])
        elif (len(parms) == 3):
            return (parms[0], parms[1], parms[2], 1)
        else:
            raise Exception('Scan parameters should be (''device'', start, end, step), not %s' % str(parms)) 
    
    def __call__(self, *args):
        """ N-dimensional scan command.
            @return ID of scan that was scheduled on the scan server
        """
        args = list(args)
        if len(args) > 0  and  isinstance(args[0], str):
            name = args[0]
            args.pop(0)
        else:
            name = "Scan"
        
        # Determine the (nested) scans and inner commands and devices to log
        # (doesn't really care if the log devices are listed last)
        scans = []
        commands = []
        log = []
        for arg in args:
            if isinstance(arg, tuple):
                scan = self._decodeScan(arg)
                scans.append(scan)
            elif isinstance(arg, ScanCommand):
                commands.append(arg)
            else:
                log.append(arg)
        
        # Wrap by scans, going in reverse from inner loop
        scans.reverse()
        cmds = CommandSequence()
        for scan in scans:
            body = CommandSequence()
            
            # Add cmds from inner loop
            body.add(cmds)
            
            # Commands for innermost loop
            if len(commands) > 0:
                for command in commands:
                    body.add(command)
                commands = []

            # Innermost loop logs
            if len(log) > 0:
                body.log(log)
                log = []
            
            # Wrap in loop which then becomes cmds to next loop 'up'
            cmds = CommandSequence()
            cmds.loop(scan[0], scan[1], scan[2], scan[3], body)
            
            
        id = self.submit(name, cmds)
        if __name__ == '__main__':
            cmds.dump()
            self.waitUntilDone()
        return id

# Create 'scan' command
scan = ScanNd()

if __name__ == '__main__':
    print 'Welcome to the scan system'
    # print 'Running in %s' % os.getcwd()
    print 'Connected to %s' % scan.server.getInfo()
    
    # 'Normal' loops
    #scan('Normal 2D', ('xpos', 1, 10), ('ypos', 1, 10, 0.5), 'readback')

    # 'Reversing' inner loop
    #scan('Reversing 2D', ('xpos', 1, 10), ('ypos', 1, 10, -0.5), 'readback')

