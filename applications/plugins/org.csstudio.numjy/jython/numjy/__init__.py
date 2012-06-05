"""
NumJy - NumPy-inspired array support for Jython
-----------------------------------------------

See also 
  help(ndarray)
"""

# TODO Path setup
import sys, os

# When executing tests from IDE/repository,
# use binaries compiled by IDE
if os.path.exists("../../org.csstudio.ndarray/bin"):
    sys.path.append("../../org.csstudio.ndarray/bin")
    sys.path.append("../../org.csstudio.ndarray/lib/util.jar")
else:
    raise Exception("NumJy library not configured")


version_info = (0, 1)
version = "NumJy V%d.%d" % version_info

import os
if os.name == 'java':
    # Import NumJy
    from ndarray import *
else:
    # Fall back to original numpy
    from numpy import *
    
