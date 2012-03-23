"""
Example for submitting scan file (XML) from command-line

@author: Kay Kasemir
"""

import os, optparse

# Check arguments
parser = optparse.OptionParser("%prog {scan file name}")
(options, args) = parser.parse_args()

if len(args) != 1:
    parser.print_help()
    parser.error("Missing name of scan file")
    
filepath = args[0]
filename = os.path.basename(filepath)

if not os.path.isfile(filepath):
    parser.error("Cannot locate scan file")
    
# Connect to scan server
from scan_client import *
print 'Connected to %s' % scan.server.getInfo()

# Read scan file
print 'Reading %s' % filepath
f = open(filepath, 'r')
commands = f.read()
f.close()

# Schedule for execution on server
print 'Submitting to scan server'
scan.submit(filename, commands)
scan.waitUntilDone()



