from org.csstudio.opibuilder.scriptUtil import PVUtil, ScriptUtil, FileUtil,\
    ConsoleUtil

import csv
from array import zeros, array
from java.lang import String
from java.util import Arrays
from java.lang.reflect import Array
from string import join

table = display.getWidget("ScanTable1").getTable()

inWorkspace=display.getWidget("inWorkspace").getValue()

path = FileUtil.openFileDialog(inWorkspace)

if path != None and path.endswith('csv'):    
    content=table.getContent()       
    lineList=[]    
    
    for row in content:
        rowList=[]
        for s in row:
            rowList.append(s)
        lineList.append(join(rowList, ','))
    text = join(lineList, '\n')
    FileUtil.writeTextFile(path, inWorkspace, text, False)