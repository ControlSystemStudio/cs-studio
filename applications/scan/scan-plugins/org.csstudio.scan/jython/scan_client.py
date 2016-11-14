"""
Scan Client Tools

DEPRECATED.

This API is based on the Java org.csstudio.scan.client.ScanClient
and thus limited to Jython.

For a Python scan client library that can be used by
both Jython and C-Python,
see https://github.com/PythonScanClient/PyScanClient#pyscanclient

@author: Kay Kasemir
"""
import sys, os, glob
import logging

# To debug the setup, directly execute this file.
# Or, from other file that tries to use it:
#
#  import logging
#  logging.basicConfig(level=logging.DEBUG)
#  from scan_client import *
if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)

logging.basicConfig(level=logging.DEBUG)

# Example for displaying debug info:
#from org.eclipse.jface.dialogs import MessageDialog
# for p in sys.path:
#    print p
#    MessageDialog.openWarning(None, "Debug", "Using " + p)

# -------------------------------------------------------
# Scan Server connection setup

import org.csstudio.scan.client.ScanClient as JavaScanClient
import java.lang.System as System
    
# Python packages are different from Java Packages
# There can be issues with 'package scanning' that cause
# jython to not find classes when using
#   from org.csstudio.scan.command import *
# or
#   import org.csstudio.scan.command
#
# The most dependable way is to explicitly import one-by-one
import org.csstudio.scan.command.CommandSequence as CommandSequence
import org.csstudio.scan.command.CommentCommand as CommentCommand
import org.csstudio.scan.command.IncludeCommand as IncludeCommand
import org.csstudio.scan.command.LoopCommand as LoopCommand
import org.csstudio.scan.command.Comparison as Comparison
import org.csstudio.scan.command.ScanCommand as ScanCommand
import org.csstudio.scan.command.WaitCommand as WaitCommand
import org.csstudio.scan.command.DelayCommand as DelayCommand
import org.csstudio.scan.command.LogCommand as LogCommand
import org.csstudio.scan.command.SetCommand as SetCommand
import org.csstudio.scan.command.ScriptCommand as ScriptCommand
import org.csstudio.scan.data.ScanDataIterator as ScanDataIterator

import time


class ScanClient(object):
    """
    Base class for a scan client
    
    Can submit scans to the server and monitor them
    """
    def __init__(self):
        # Connection to the scan server
        self.client = JavaScanClient()
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
            self.client.getServerInfo()
        except:
            self.client = JavaScanClient()
            
            
    def simulate(self, commands):
        """
        Submit a CommandSequence to the server for simulation
        
        @param commands  CommandSequence or string with XML text
          
        @return Simulation info
        """
        self.checkServer()
        if isinstance(commands, str):
            xml = commands
        elif isinstance(commands, CommandSequence):
            xml = commands.getXML()
        else:
            raise Exception('Expecting CommandSequence or XML-text')
        return self.client.simulateScan(xml)


    def submit(self, name, commands, queue=True):
        """
        Submit a CommandSequence to the server for execution
        
        @param name  Name of the scan
        @param commands  CommandSequence or string with XML text
        @param queue  Submit to scan server queue, or execute as soon as possible?
          
        @return Scan ID
        """
        self.checkServer()
        if isinstance(commands, str):
            xml = commands
        elif isinstance(commands, CommandSequence):
            xml = commands.getXML()
        else:
            raise Exception('Expecting CommandSequence or XML-text')
        self.id = self.client.submitScan(name, xml, queue)
        return self.id

    def getScanInfo(self, id=-1):
        """
        Get scan info
        
        @param id Scan ID, defaulting to the last submitted scan
        """
        self.checkServer()
        if id == -1:
            id = self.id
        return self.client.getScanInfo(id)
    
    def printData(self, id=-1, *devices):
        """
        Print scan data
        
        @param id: Scan ID, defaulting to the last submitted scan
        @param devices: One or more device names. Default: All devices in scan.
        """
        self.checkServer()
        if id == -1:
            id = self.id
        data = self.client.getScanData(id)
        if devices:
            sheet = ScanDataIterator(data, devices)
        else:
            sheet = ScanDataIterator(data)
        sheet.printTable(System.out)
            
    def waitUntilDone(self, id=-1):
        """
        Wait until a submitted scan has finished
        
        @param id: Scan ID, defaulting to the last submitted scan
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
    
    * First argument can be scan name. Optional.
    * Loop specification for all following arguments: ('device', start, end[, step])
    * Names of device to log in addition to loop'ed devices
    * Basic ScanCommand to perform: SetCommand, WaitCommand, ...
    
    All the devices used in loops, mentioned as device names or
    accessed in specific SetCommands will be logged in the innermost loop.
    
    Examples:
    
    # Scan 'xpos' from 1 to 10, stepping 1. xpos will be logged.
    scan('My first one', ('xpos', 1, 10) )
    
    # Scan name is optional. Loop of xpos from 1 to 10.
    scan( ('xpos', 1, 10) )

    # Log the 'readback' together with 'xpos' from the loop.
    scan( ('xpos', 1, 10), 'readback')
    
    # Scan 'xpos' from 1 to 10, stepping 1,
    # inside that looping 'ypos' from 1 to 5 by 0.2,
    # logging 'readback' with 'xpos' and 'ypos'.
    scan('XY Example', ('xpos', 1, 10), ('ypos', 1, 5, 0.2), 'readback')

    # Scan 'xpos' and 'ypos', set something to '1' and then '3' (with readback)
    # Will log 'xpos', 'ypos', 'setpoint', 'readback'
    scan('XY Example', ('xpos', 1, 10), ('ypos', 1, 5, 0.2),
         SetCommand('setpoint', 1, 'readback'),
         SetCommand('setpoint', 3, 'readback'))
    """
    
    def __init__(self):
        ScanClient.__init__(self)

    def _decodeLoop(self, parms):
        """ Check for 
                ('device', start, end, step)
             or 
                ('device', start, end)
             for a default step size of 1
             @return ('device', start, end, step)
        """
        if (len(parms) == 4):
            return (parms[0], parms[1], parms[2], parms[3])
        elif (len(parms) == 3):
            return (parms[0], parms[1], parms[2], 1)
        else:
            raise Exception('Scan parameters should be (''device'', start, end, step), not %s' % str(parms)) 
    
    def _decodeScan(self, log, args):
        """ Recursively build commands from scan arguments
            @param log: Devices to log so far while going down the argument list
            @param args: Remaining scan arguments 
            @return List of commands
        """
        if len(args) <= 0:
            # Reached innermost layer, no arguments left.
            # Log what needs to be logged. May be nothing.
            if len(log) <= 0:
                return []
            # Remove duplicate device names from list,
            # but preserve the list order
            cleaned_log = []
            for device in log:
                if device not in cleaned_log:
                    cleaned_log.append(device)
            return [ LogCommand(cleaned_log) ]
        
        # Analyze next argument
        arg = args.pop(0)
        if isinstance(arg, str):
            # Remember device to log, move on
            log.append(arg)
            return self._decodeScan(log, args)
        elif isinstance(arg, tuple):
            # Loop specification
            scan = self._decodeLoop(arg)
            # Remember loop variable for log
            log.append(scan[0])
            # Create loop with remaining arguments as body
            return [ LoopCommand(scan[0], scan[1], scan[2], scan[3], self._decodeScan(log, args)) ]
        elif isinstance(arg, ScanCommand):
            if isinstance(arg, SetCommand):
                # Log device affected by 'set'
                log.append(arg.getDeviceName())
            # Create list of commands
            cmds = [ arg ]
            cmds.extend(self._decodeScan(log, args))
            return cmds
        else:
            raise Exception('Cannot handle scan parameter of type %s' % arg.__class__.__name__)
   
    def __call__(self, *args):
        """ N-dimensional scan command.
            @return ID of scan that was scheduled on the scan server
        """
        # Turn args into modifyable list
        args = list(args)
        
        # First string is optional scan title
        if len(args) > 0  and  isinstance(args[0], str):
            name = args[0]
            args.pop(0)
        else:
            name = "Scan"
            
        # End result, overall scan
        cmds = self._decodeScan([], args)
        if len(cmds) <= 0:
            raise Exception('Empty scan')

        seq = CommandSequence(cmds)
        id = self.submit(name, seq)
        if __name__ == '__main__':
            seq.dump()
            self.waitUntilDone()
        return id

# Create 'scan' command
scan = ScanNd()

if __name__ == '__main__':
    print 'Welcome to the scan system'
    
    # print 'Running in %s' % os.getcwd()
    print 'Connected to %s' % scan.client.getServerInfo()
    
    for scan in scan.client.getScanInfos():
        print scan
    
    # 'Normal' loops
    #scan('Normal 2D', ('xpos', 1, 10), ('ypos', 1, 10, 0.5), 'readback')

    # 'Reversing' inner loop
    #scan('Reversing 2D', ('xpos', 1, 10), ('ypos', 1, 10, -0.5), 'readback')

