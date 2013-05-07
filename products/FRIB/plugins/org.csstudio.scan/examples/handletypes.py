# ScriptCommand class to demo strings

from org.csstudio.scan.command import ScanScript
from math import sqrt, exp
import sys


class HandleTypes(ScanScript):
    def getDeviceNames(self):
        return [ "setpoint", "text.DESC", "text" ]

    def run(self, context):
        # Plain number
        value = context.read("setpoint")
        print "setpoint: ", value, value.__class__.__name__
        
        # Plain (unicode) string
        value = context.read("text.DESC")
        print "text.DESC: ", value, value.__class__.__name__

        # On EPICS level, char[]
        # Presented as (unicode) String because Scan Server PVDevice
        # handles the conversion
        value = context.read("text")
        print "text: ", value, value.__class__.__name__
        print "Converted to string: '%s'" % str(value), str(value).__class__.__name__
