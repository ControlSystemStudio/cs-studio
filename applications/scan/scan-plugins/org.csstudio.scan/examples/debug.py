# ScriptCommand class for displaying debug information

from org.csstudio.scan.command import ScanScript

import sys

class Debug(ScanScript):
    def __init__(self):
        print "Debug Command"
        
    def run(self, context):
        print "Debug Information:"
        print "sys.version = ", sys.version
        print "sys.prefix = ", sys.prefix
        print "sys.executable = ", sys.executable
        print "sys.path = ", sys.path
        print "sys.platform = ", sys.platform

