from org.csstudio.scan.command import ScanScript
from math import sqrt, exp
import sys

from numjy import *

class FindPeak(ScanScript):
    def getDeviceNames(self):
        return [ "fit_center" ]
    
    def run(self, context):
        
        # TODO Create Jython wrapper for Java ScanScript so that
        #      context.getData right away returns ndarray?
        data = array(context.getData("xpos", "signal"))
        x = data[0]
        y = data[1]
        
        # Determine centroid
        center = sum(x * y) / sum(y)
        print "Center: ", center
        
        m = max(y)
        print "Max: ", m
        
        width = sqrt( abs(sum((center-x)**2*y)/sum(y)) )
        print "Width: ", width
        
        fit = m*exp(-(x-center)**2/(2*width**2))
        print fit
        
        # Put 'fit' into context
        context.logData("fit", fit.nda)
        
        # Set PVs with result
        context.write("fit_center", center)
        
        # TODO Send signal to plot [x, y], [x, fit]

