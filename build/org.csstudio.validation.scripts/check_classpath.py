'''
Created on Mar 18, 2013

A simple script which scans all .classpath files under the directory it was invoked from to ensure that none of them explicitly define JRE container

@author: shroffk
'''
import os.path
import re
import sys

nonGenericPath='(.*)<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/(.+)"/>'

incorrectFiles = []
for dirpath, dirnames, filenames in os.walk("."):
    for completefilename in [ os.path.join(dirpath, f) for f in filenames if f.endswith(".classpath")]:
        for line in open(completefilename).readlines():
            m = re.match(nonGenericPath, line)
            if m is not None:
                incorrectFiles.append(completefilename)
if len(incorrectFiles) != 0:
    print 'Following incorrectly configured .classpath files committed to repository'
    for f in incorrectFiles:
        print f
    sys.exit(-1)         
        
