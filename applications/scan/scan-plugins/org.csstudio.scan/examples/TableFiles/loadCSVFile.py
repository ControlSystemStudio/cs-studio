from org.csstudio.opibuilder.scriptUtil import PVUtil, ScriptUtil, FileUtil,\
    ConsoleUtil

import csv
from array import zeros, array
from java.lang import String
from java.util import Arrays
from java.lang.reflect import Array
table = display.getWidget("ScanTable1").getTable()

inWorkspace=display.getWidget("inWorkspace").getValue()

path = FileUtil.openFileDialog(inWorkspace)
if path != None and path.endswith('csv'):    
    if inWorkspace:
        path = FileUtil.workspacePathToSysPath(path)
    reader = csv.reader(open(path))
    lineList=[]
    for row in reader:
       lineList.append(row)
    content = Array.newInstance(String, len(lineList), len(row))
    for r in range(len(lineList)):
        for c in range(len(row)):
            content[r][c] = lineList[r][c]
    table.setContent(content)

    