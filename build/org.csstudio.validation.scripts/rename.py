'''
Created on Mar 18, 2013

A simple script which scans all .classpath files under the directory it was invoked from to ensure that none of them explicitly define JRE container

@author: shroffk
'''
import os.path
import re
import sys
import xml.etree.ElementTree as ET
from optparse import OptionParser


incorrectFiles = []
for dirpath, dirnames, filenames in os.walk("."):
    for completefilename in [ os.path.join(dirpath, f) for f in filenames if f.endswith("pom.xml")]:
        with open(completefilename, 'r+') as f:
            lines = f.readlines()
            for line in lines:
                if "<artifactId>core-plugins</artifactId>" in line:
                    line = line.replace("<artifactId>core-plugins</artifactId>", "<artifactId>unorganized-plugins</artifactId>")        
                    print "found:", line            
                f.write(line)
            