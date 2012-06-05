from org.csstudio.scan.command import ScanScript
from math import sqrt, exp
import sys

from numjy import *

# TODO: Use plain script where "context" is set by server? No ScanScript.run()?
class FindPeak(ScanScript):
    def getDeviceNames(self):
        return [ "fit_center" ]
    
    
    def run(self, context):
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
        
        # TODO: Set PVs with result
        context.write("fit_center", center)