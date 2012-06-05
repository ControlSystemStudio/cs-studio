""" Array support unit test

@author: Kay Kasemir
"""

import unittest
import math
from numjy import *


class ScanDataTest(unittest.TestCase):
    
    def testVersion(self):
        print version
        self.assertTrue(version_info > (0, 0))

    def testCreateZeros(self):
        a = zeros(5)
        for i in range(5):
            self.assertEqual(0, a[i]);
        a = zeros([5, 5])
        for i in range(5):
            for j in range(5):
                self.assertEqual(0, a[i,j]);

    def testCreateRange(self):
        a = arange(5)
        for i in range(5):
            self.assertEqual(i, a[i]);

        a = arange(25).reshape(5, 5)
        for i in range(5):
            for j in range(5):
                self.assertEqual(i*5+j, a[i,j]);

    def testCreateLinspace(self):
        a = linspace(0, 10, 5)
        for i in range(5):
            self.assertEqual(i*2.5, a[i]);

    def testShape(self):
        # Shape returned as tuple. Zeros uses tuple for desired size
        a = zeros(5, dtype=byte)
        self.assertEqual((5,), a.shape)
        self.assertEqual(1, a.ndim)
        # When using byte, the NumPy and NumJy strides match.
        # Otherwise the NumJy strides address a flat array,
        # while NumPy strides through a byte buffer, so
        # its strides scale with the element size
        self.assertEqual((1,), a.strides)
        
        a = zeros((3,2), dtype=byte)
        self.assertEqual((3,2), a.shape)
        self.assertEqual(2, a.ndim)
        self.assertEqual((2,1), a.strides)
        
        # reshape takes var-arg numbers or tuple
        a = zeros(6, dtype=byte).reshape(3,2)
        self.assertEqual((3,2), a.shape)
        self.assertEqual((2,1), a.strides)
        a = zeros(6, dtype=byte).reshape([2,3])
        self.assertEqual((2,3), a.shape)
        self.assertEqual((3,1), a.strides)
        
        # len()
        a = zeros(6)
        self.assertEqual(6, len(a))
        a = zeros(6).reshape(3, 2)
        self.assertEqual(3, len(a))

    def testTypes(self):
        # Select type
        a = array([ 1, 2, 3, 4 ], dtype=float32)
        self.assertEqual(float32, a.dtype)
        a = array([ 0, 1 ], dtype=bool)
        self.assertEqual(bool, a.dtype)
        # Pick type automatically
        a = array([ 1, 2, 3, 4 ])
        self.assertEqual(int64, a.dtype)
        a = array([ 1.0, 2.0, 3.0, 4.0 ])
        self.assertEqual(float64, a.dtype)
        a = array([ True, False ])
        self.assertEqual(bool, a.dtype)

    def testWriteToElements(self):
        a = zeros(5)
        for i in range(5):
            a[i] = i
        self.assertTrue(any(a == arange(5)))
        
    def testView(self):
        a=arange(6)
        # 'b' should be a view of a
        b=a.reshape(2, 3)
        # Changing 'b' also changes corresponding element in 'a'
        b[1,2]=666
        self.assertEqual(666, b[1,2])
        self.assertEqual(666, a[5])
        
        # Views with different offsets
        a = arange(6)
        b = a[2:5]
        self.assertTrue(all(b == array([ 2, 3, 4 ])))
        c = b[1:3]
        self.assertTrue(all(c == array([ 3, 4 ])))

    def testSlicing(self):
        # Simple 1-D subsection
        a = arange(10)
        sub = a[2:4]
        self.assertTrue(all(sub == array([ 2, 3 ])))
        
        sub = a[1:6:2]
        self.assertTrue(all(sub == array([ 1.0, 3.0, 5.0 ])))
        
        sub = a[1:]
        self.assertTrue(all(sub == array([ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ])))
        
        sub = a[:-1]
        self.assertTrue(all(sub == array([ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 ])))
        
        sub = a[:3]
        self.assertTrue(all(sub == array([ 0.0, 1.0, 2.0 ])))
        
        sub = a[::2]
        self.assertTrue(all(sub == array([ 0.0, 2.0, 4.0, 6.0, 8.0 ])))

        # Assignment to slice changes original array
        sub[1] = 666;
        self.assertEqual(666, a[2])
        
        # Get view into second 'row' of data
        a = arange(6).reshape(2, 3)
        sub = a[1]
        self.assertTrue(all(sub == array([ 3.0, 4.0, 5.0 ])))
        sub[1] = 666
        self.assertEqual(666, sub[1])
        self.assertEqual(666, a[1, 1])

        # Write to 1-D slice with matching array
        a = zeros(5)
        sub = a[2:4]
        sub[:] = array([ 2, 3 ])
        self.assertTrue(all(a == array([ 0, 0, 2, 3, 0 ])))
        self.assertTrue(all(sub == array([ 2, 3 ])))
        self.assertTrue(all(sub[:] == array([ 2, 3 ])))

        # Update 2x2 section of 2x3 array with matching array
        orig = arange(6).reshape(2, 3)
        orig[:, ::2] = array([ [ 40, 42], [ 43, 45 ]])
        self.assertTrue(all(orig == array([ [ 40.0, 1.0, 42.0 ],
                                            [ 43.0, 4.0, 45.0 ] ] ) ) )

        # Update 2x2 section of 2x3 array with 'column' vector (broadcast)
        orig = arange(6).reshape(2, 3)
        orig[:, ::2] = array([ [ 41], [ 42 ]])
        self.assertTrue(all(orig == array([ [ 41.0, 1.0, 41.0 ],
                                            [ 42.0, 4.0, 42.0 ] ] ) ) )

        
        
    def testIteration(self):
        # Iteration is always over flat array
        a = arange(10)
        i = 0
        for n in a:
            self.assertEqual(i, n)
            i += 1

        # View iterates over its elements, not the base array
        b = a[2:5]
        i = 2
        for n in b:
            self.assertEqual(i, n)
            i += 1
        self.assertEqual(i, 5)

    def testComparisons(self):
        a = array([[ False, False ], [ False, True ]])
        self.assertTrue(any(a))
        self.assertTrue(a.any())
        self.assertFalse(all(a))
        self.assertFalse(a.all())
        
        # Compare flat arrays
        a = arange(4)
        b = arange(4)
        c = a == b
        self.assertEquals(4, len(c))
        self.assertEquals(True, c[0])
        self.assertEquals(True, c[1])
        self.assertEquals(True, c[2])
        self.assertEquals(True, c[3])
        
        # array and scalar
        c = a == 2
        self.assertEquals(4, len(c))
        self.assertEquals(False, c[0])
        self.assertEquals(False, c[1])
        self.assertEquals(True, c[2])
        self.assertEquals(False, c[3])
        
        # Same shape and content, but different strides into orig. data
        a = array([ 1, 2, 1, 1, 2, 2], dtype=byte)
        b = a[0:2]
        c = a[2:5:2]
        self.assertEqual((1,), b.strides)
        self.assertEqual((2,), c.strides)
        d = b == c
        self.assertEquals(2, len(d))
        self.assertEquals(True, d[0])
        self.assertEquals(True, d[1])

        # Broadcast
        a = array([ [ 1, 1 ], [ 2, 2] ])
        b = array([ [ 1 ], [ 2 ] ])
        c = a == b
        self.assertEquals((2,2), c.shape)
        self.assertEquals(True, c[0, 0])
        self.assertEquals(True, c[0, 1])
        self.assertEquals(True, c[1, 0])
        self.assertEquals(True, c[1, 1])
        
        # More...
        a = array([[ 1, 2 ], [ 3, 4]])
        b = array([[ 1, 1 ], [ 1, 1]]) + array([[ 0, 1 ], [ 2, 3]])
        c = a == b
        self.assertTrue(all(c))

        c = a != b
        self.assertFalse(any(c))
        
        c = array([ 1, 2 ]) < array([ 2, 3 ])
        self.assertTrue(all(c))

        c = array([ 1, 3 ]) <= array([ 2, 3 ])
        self.assertTrue(all(c))

        c = array([ 2, 3 ]) > array([ 1, 2 ])
        self.assertTrue(all(c))

        c = array([ 2, 3 ]) >= array([ 1, 3 ])
        self.assertTrue(all(c))

    def testNeg(self):
        self.assertTrue(all(-arange(5) == array([ 0, -1, -2, -3, -4 ])))
        # View into subsection
        a = arange(5)
        b = a[2:4]
        self.assertTrue(all(-b == array([ -2, -3 ])))

    def testSum(self):
        self.assertEqual(3, sum(array([ 0, 1, 2 ])))
        self.assertEqual(-2, sum(array([ -2 ])))
        self.assertEqual(0, sum(array([  ])))

    def testMinMax(self):
        self.assertEqual(2, max(array([ -2, 1, 2 ])))
        self.assertEqual(-2, min(array([ -2, 1, 2 ])))

    def testAdd(self):
        # Flat array
        self.assertTrue(all(arange(5) + arange(5) == array([ 0, 2, 4, 6, 8 ])))
        # Array + scalar
        self.assertTrue(all(arange(5) + 42 == array([ 42.0, 43.0, 44.0, 45.0, 46.0 ])))
        self.assertTrue(all(42 + arange(5) == array([ 42.0, 43.0, 44.0, 45.0, 46.0 ])))
        # in-place
        a = arange(5)
        a += 42
        self.assertTrue(all(a == array([ 42.0, 43.0, 44.0, 45.0, 46.0 ])))
        a = arange(5)
        a += arange(5)
        self.assertTrue(all(a == array([ 0, 2, 4, 6, 8 ])))

        # Same shape, but different strides
        a = arange(6).reshape(2, 3)
        b = a[:, 0:2]
        c = a[:, ::2]
        self.assertTrue(all(b + c == array([ [ 0.0, 3.0 ],
                                             [ 6.0, 9.0 ] ])))

        # Broadcast        
        a = arange(12).reshape(2, 3, 2)
        b = array([[ 10, 20, 30]]).T
        c = a + b
        self.assertTrue(all(c == array([ [ [10, 11], [22, 23], [34, 35]],
                                         [ [16, 17], [28, 29], [40, 41]]])))

    def testSub(self):
        self.assertTrue(all(arange(5) - arange(5) == array([ 0, 0, 0, 0, 0 ])))
        self.assertTrue(all(arange(5) - 42 == array([ -42.0, -41.0, -40.0, -39.0, -38.0 ])))
        self.assertTrue(all(42 - arange(5) == array([ 42.0, 41.0, 40.0, 39.0, 38.0 ])))
        a = arange(5)
        a -= arange(5)
        self.assertTrue(all(a == array([ 0, 0, 0, 0, 0 ])))

    def testMul(self):
        self.assertTrue(all(arange(5) * arange(5) == array([ 0, 1, 4, 9, 16 ])))
        self.assertTrue(all(arange(3) * 42 == array([ 0, 42, 84 ])))
        self.assertTrue(all(42 * arange(3) == array([ 0, 42, 84 ])))
        a = arange(3)
        a *= 42 
        self.assertTrue(all(a == array([ 0, 42, 84 ])))
        a = arange(3)
        a *= array([ 42, 21, 7]) 
        self.assertTrue(all(a == array([ 0, 21, 14 ])))

    def testDiv(self):
        self.assertTrue(all(arange(1, 5) / arange(1, 5) == array([ 1, 1, 1, 1 ])))
        self.assertTrue(all((arange(3) * 42) / 42 == array([ 0, 1, 2 ])) )
        self.assertTrue(all((arange(3) * 42) / array([ 42, 42, 42 ]) == array([ 0, 1, 2 ])))
        self.assertTrue(all(42 / arange(1, 3) == array([ 42.0, 21.0 ])))
        a = arange(5, dtype=float)
        a /= 5
        self.assertTrue(all(a == array([ 0.0, 0.2, 0.4, 0.6, 0.8 ])))
        a = arange(1, 5)
        a /= arange(1, 5)
        self.assertTrue(all(a == array([ 1, 1, 1, 1 ])))
        
    def testPower(self):
        a = arange(3)
        a = a ** 2
        self.assertTrue(all(a == array([ 0, 1, 4 ])))
        a = arange(3)
        a **= 2
        self.assertTrue(all(a == array([ 0, 1, 4 ])))
        a = pow(arange(3), array([2]))
        self.assertTrue(all(a == array([ 0, 1, 4 ])))
        a = arange(3)
        a = 2 ** a
        self.assertTrue(all(a == array([ 1, 2, 4 ])))
        
    def testAbs(self):
        a = -arange(3)
        a = abs(a)
        self.assertTrue(all(a == arange(3)))
        self.assertEquals(2.0, abs(-2))
        
    def testSqrt(self):
        a = array([ 0, 1, 4, 9, 16 ])
        a = sqrt(a)
        self.assertTrue(all(a == arange(5)))
        self.assertEquals(2.0, sqrt(4))

    def testExp(self):
        a = array([ 0, 1, 2, -2 ])
        a = exp(a)
        b = array([ 1.0, exp(1), exp(2), exp(-2) ])
        self.assertTrue(all(a == b))

    def testLog(self):
        a = array([ 1, 2, 4 ])
        a = log(a)
        b = array([ 0.0, log(2.0), log(4.0) ])
        self.assertTrue(all(a == b))

    def testLog10(self):
        a = array([ 1, 10, 100, 1000, 10000 ])
        a = log10(a)
        b = arange(5)
        self.assertTrue(all(a == b))

    def testTranspose(self):
        a = arange(4).T
        self.assertTrue(all(a == arange(4)))

        a = arange(4).reshape(2, 2).T
        self.assertTrue(all(a == array([ [ 0, 2], [ 1, 3 ]])))

        a = arange(6).reshape(3, 2).T
        self.assertTrue(all(a == array([ [ 0, 2, 4], [ 1, 3, 5 ]])))

        a = arange(6).reshape(1, 2, 3).T
        self.assertTrue(all(a == array([ [[0], [3]],  [[1], [4]],  [[2], [5]] ])))

        # Specifically request the 'default' axis ordering of transpose
        a = transpose(arange(6).reshape(1, 2, 3), ( 2, 1, 0 ))
        self.assertTrue(all(a == array([ [[0], [3]],  [[1], [4]],  [[2], [5]] ])))

        # Request axis actually stay unchanged
        a = transpose(arange(6).reshape(1, 2, 3), ( 0, 1, 2))
        self.assertTrue(all(a == arange(6).reshape(1, 2, 3)))

        # Request odd axis order
        a = transpose(arange(6).reshape(1, 2, 3), ( 0, 2, 1))
        self.assertTrue(all(a == array([ [ [0, 3], [1, 4], [2, 5] ] ] )))

    def testDot(self):
        a = arange(6)
        b = arange(6)
        c = dot(a, b)
        self.assertEquals(55, c)
        
        a = array([[ 1, 2], [ 3, 4] ])
        b = array([[ 1, 2], [ 3, 4] ])
        c = dot(a, b)
        self.assertTrue(all(c == array([[ 7, 10], [15, 22]])))

        a = array([[ 1, 2], [ 3, 4] ])
        b = array([[ 1, 2, 3, 4], [ 5, 6, 7, 8] ])
        c = dot(a, b)
        self.assertTrue(all(c == array([[11, 14, 17, 20], [23, 30, 37, 44]])))

        a = array([[ 1, 2], [ 3, 4] ])
        b = array([10, 20])
        c = dot(a, b)
        self.assertTrue(all(c == array([[ 50, 110]])))
        
        angle = math.radians(90)
        rotate = array( [ [ math.cos(angle), -math.sin(angle) ], [ math.sin(angle), math.cos(angle) ] ] )
        vec = array( [ 1, 0 ])
        c = dot(rotate, vec) 
        self.assertTrue(abs(0 - c[0]) < 0.001)
        self.assertEquals(1, c[1])


if __name__ == '__main__':
    unittest.main()

