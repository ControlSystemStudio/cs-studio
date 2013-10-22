"""Example for submitting scan file (XML) from command-line

   jython.sh submit_scan my_scan.py

   @author: Kay Kasemir
"""
import os, optparse
from scan_client import *

def submitScan(filepath, wait=True):
    """Submit a scan file to server

       filepath: Full path to the *.scn file
       wait [optional]: Wait for scan to finish?
    """
    if not os.path.isfile(filepath):
        raise Exception("Cannot locate scan file %s", filepath)

    filename = os.path.basename(filepath)

    # Connect to scan server
    print 'Connected to %s' % scan.server.getInfo()

    # Read scan file
    print 'Reading %s' % filepath
    f = open(filepath, 'r')
    commands = f.read()
    f.close()

    # Schedule for execution on server
    print 'Submitting to scan server'
    scan.submit(filename, commands)
    if wait:
        scan.waitUntilDone()


if __name__ == '__main__':
    # Check arguments
    parser = optparse.OptionParser("%prog {scan file name}")
    (options, args) = parser.parse_args()

    if len(args) != 1:
        parser.print_help()
        parser.error("Missing name of scan file")
    submitScan(args[0])
