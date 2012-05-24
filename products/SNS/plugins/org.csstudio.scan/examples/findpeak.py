from org.csstudio.scan.command import ScanScript
from math import sqrt, exp
import sys

class FindPeak(ScanScript):
    def getDeviceNames(self):
        return [ "fit_center" ]
    
    def gaussian(self, center, max, width, x):
        return max*exp(-(x-center)**2/(2*width**2))
    
    def run(self, context):
        print "Running FindPeak.run!"
        print sys.path
        
        [x, y] = context.getData("xpos", "signal")
        
        # Determine centroid
        # center = sum( x[i]*y[i] ) / sum(y)
        sum_xy = sum([i*j for i, j in zip(x, y)])
        sum_y = sum(y)
        center = sum_xy / sum_y

        m = max(y)
        print "Max: ", m
        
        # width = sqrt( abs(sum((center-x)**2*y)/sum_y) )
        
        sum_width = sum([ (center-i)**2 * j for i, j in zip(x, y) ])
        width = sqrt( abs(sum_width) )
        print "Width: ", width

        for i in x:
            print i, self.gaussian(center, m, width, i)
        
        # TODO: Set PVs with result
        context.write("fit_center", center)