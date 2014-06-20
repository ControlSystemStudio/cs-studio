"""
Gaussian Fit

@author: Kay Kasemir
"""

import numjy as np

class Gaussian:
    def __init__(self, center, width, height):
        self.center = center
        self.width = width
        self.height = height
        
    def values(self, x):
        return self.height * np.exp(-(x-self.center)**2/(2*self.width**2))
    
    @classmethod
    def fromCentroid(cls, x, y):
        """Centroid-based fit of a gaussian to data
        """ 
        center = np.sum(x * y) / np.sum(y)
        m = max(y)
        width = np.sqrt( abs(np.sum((center-x)**2*y) / np.sum(y) ) )

        return Gaussian(center, width, m)
        
    def __str__(self):
        return "Gaussian at %g, width %g, height %g" % (self.center, self.width, self.height)

# Unit Test -------------------
import unittest

class Test(unittest.TestCase):
    def testFit(self):
        x = np.linspace(0, 10, num=11)
        gauss = Gaussian(5.0, 1.0, 10.0)
        y = gauss.values(x)
        print x
        print y
        
        fit = Gaussian.fromCentroid(x, y)
        print fit
        self.assertAlmostEquals(5.0, fit.center, places=3)
        self.assertAlmostEquals(1.0, fit.width, places=3)
        self.assertAlmostEquals(10.0, fit.height, places=3)    
    
if __name__ == "__main__":
    unittest.main()
