"""
Digital Filtering

@author: Kay Kasemir
"""

import numjy as np

def fir(data, fir, zero_edge=True):
    """FIR filter
       @param data: Array data
       @param fir: FIR filter elements
       @param zero_edge: Set edge elements in result to 0, or preserve original data?
       @return: Filtered array 
    """
    L = len(fir)
    N = len(data)
    mid = int(len(fir)/2)
    if zero_edge:
        result = np.zeros(N)
    else:
        result = np.array(data)
    for i in range(mid, N-mid):
        # index into 'data' at left edge of filter
        i0 = i-mid
        # print fir, data[i0:(i0+L)], fir * data[i0:(i0+L)], sum(fir * data[i0:(i0+L)])
        result[i] = sum(fir * data[i0:(i0+L)])
    return result

# Unit Test -------------------
import unittest

class Test(unittest.TestCase):
    def testFIR(self):
        smooth = np.array([0.25, 0.5, 0.25])
    
        d = np.arange(10)
        filtered = fir(d, smooth, zero_edge=False)
        print d, '--', smooth, '(keep edge) -->', filtered
        self.assertTrue(np.all( filtered == d))

        filtered = fir(d, smooth, zero_edge=True)
        print d, '--', smooth, '-->', filtered
        self.assertEquals(0, filtered[9])

        
        d = np.zeros(10)
        d[5] = 10
        filtered = fir(d, smooth)
        print d, '--', smooth, '-->', filtered
        self.assertEquals(5.0, filtered[5])

        d = np.zeros(10)
        d[5:10] = np.ones(5)
        filtered = fir(d, smooth)
        print d, '--', smooth, '-->', filtered
        self.assertEquals(0.0, filtered[3])
        self.assertEquals(0.25, filtered[4])
        self.assertEquals(0.75, filtered[5])
        self.assertEquals(1.0, filtered[6])
        
        diff = np.array([-0.5, 0, 0.5])
        filtered = fir(d, diff)
        print d, '--', diff, '-->', filtered
        
    
if __name__ == "__main__":
    unittest.main()
