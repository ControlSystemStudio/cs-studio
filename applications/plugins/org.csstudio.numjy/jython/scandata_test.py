""" Array support unit test

@author: Kay Kasemir
"""

import unittest
from scandata import *

class ScanDataTest(unittest.TestCase):
    
    def test_shape(self):
        x = array([ 0, 1, 2, 3, 4, 5 ])
        self.assertEqual([6], x.shape)
        x.reshape(2, 3)
        self.assertEqual([2, 3], x.shape)

    def test_elements(self):
        x = arange(6)
        self.assertEqual(0, x[0])
        self.assertEqual(5, x[5])
        self.assertEqual(5, x[-1])
        x.reshape(2, 3)
        self.assertEqual(0, x[0, 0])
        self.assertEqual(5, x[1, 2])

    def test_slice(self):
        x = arange(6)[2:4]
        self.assertEqual("[ 2.0, 3.0 ]", str(x))
        x = arange(6)[0:6:2]
        self.assertEqual("[ 0.0, 2.0, 4.0 ]", str(x))

    def test_range(self):
        x = arange(5)
        self.assertEqual("[ 0.0, 1.0, 2.0, 3.0, 4.0 ]", str(x))
    
    def test_sum(self):
        x = arange(6)
        self.assertEqual(15, sum(x))
    
    def test_neg(self):
        x = arange(6)
        y = -x
        self.assertEqual(-15, sum(y))
    
    def test_add(self):
        x = arange(6) + 42
        for i in range(6):
            self.assertEqual(i + 42, x[i])
        
        x = arange(6)
        x += 42
        for i in range(6):
            self.assertEqual(i + 42, x[i])
        
        x = arange(6)
        y = arange(6)
        z = x + y
        for i in range(6):
            self.assertEqual(2*i, z[i])
        
    def test_sub(self):
        x = arange(6) - 42
        for i in range(6):
            self.assertEqual(i - 42, x[i])
            
        x = 42 - arange(6)
        for i in range(6):
            self.assertEqual(42 - i, x[i])





if __name__ == '__main__':
    unittest.main()

