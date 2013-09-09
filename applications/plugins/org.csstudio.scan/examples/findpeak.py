# ScriptCommand class that fits a gaussian shape to xpos & signal,
# then moves xpos to the center of the fit

from org.csstudio.scan.command import ScanScript
import sys
from gaussian import Gaussian
#print "findpeak.py path: ", sys.path

from numjy import *

class FindPeak(ScanScript):
    def __init__(self, pos_device, sig_device):
        print "FindPeak", pos_device, sig_device
        self.pos_device = pos_device
        self.sig_device = sig_device
        
    def getDeviceNames(self):
        return [ self.pos_device, self.sig_device ]
    
    def run(self, context):
        # Turn raw python array into ndarray for easier math
        data = array(context.getData(self.pos_device, self.sig_device))
        x = data[0]
        y = data[1]
        
        # Compute fit
        g = Gaussian.fromCentroid(x, y)
        print g
        fit = g.values(x)
        
        # Log the 'fit' data for later comparison with raw data
        context.logData("fit", fit.nda)
        
        # Set PVs with result
        context.write(self.pos_device, g.center)
        

