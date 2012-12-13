# ScriptCommand class to demo strings

from org.csstudio.scan.command import ScanScript
from math import sqrt, exp
import sys


class HandleTypes(ScanScript):
    def getDeviceNames(self):
        return [ "setpoint", "text.DESC", "text" ]

    def chars2string(self, chars):
        text = ""
        for c in chars:
        	if c == 0:
        		break;
        	text += "%c" % c
        return text
    
    def run(self, context):
        # Plain number
        value = context.read("setpoint")
        print "setpoint: ", value, value.__class__.__name__
        
        # Plain string
        value = context.read("text.DESC")
        print "text.DESC: ", value, value.__class__.__name__

        # On EPICS level, char[]
        # Presented as double[] 
        value = context.read("text")
        print "text: ", value, value.__class__.__name__
        
        # Convert to string
        text = self.chars2string(value)
        print "Converted to string: '%s'" % text
