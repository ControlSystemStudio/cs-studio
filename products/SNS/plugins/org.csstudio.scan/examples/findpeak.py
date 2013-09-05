# ScriptCommand class that fits a gaussian shape to xpos & signal,
# then moves xpos to the center of the fit

from org.csstudio.scan.command import ScanScript
from math import sqrt, exp
import sys

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
        
        # Determine centroid
        center = sum(x * y) / sum(y)
        print "Center: ", center
        
        # Other parameters...
        m = max(y)
        print "Max: ", m
        
        width = sqrt( abs(sum((center-x)**2*y)/sum(y)) )
        print "Width: ", width
        
        # Compute fit
        fit = m*exp(-(x-center)**2/(2*width**2))
        print fit
        
        # Log the 'fit' data for later comparison with raw data
        context.logData("fit", fit.nda)
        
        # Set PVs with result
        context.write(self.pos_device, center)
        

