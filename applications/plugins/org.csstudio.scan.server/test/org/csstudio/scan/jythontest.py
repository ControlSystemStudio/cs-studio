# Used by JythonSupportTest.java


import sys

print "Path: ", sys.path

from org.csstudio.scan.command import ScanScript

class JythonTest(ScanScript):
    def getDeviceNames(self):
        return [ 'device1', 'device2']
    
    def run(self, context):
        print 'Running script...'
        context.logData('device1', 42.0);
