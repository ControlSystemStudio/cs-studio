"""
Schedule scan with parameters from BOY table.

@author:Xihui Chen
"""

from scan_client import *
from scan_ui import *
from java.lang import Thread, Runnable
from org.csstudio.opibuilder.scriptUtil import ColorFontUtil

table = display.getWidget("ScanTable1").getTable()

client = ScanClient()

counter="Counter"

if widget.getVar(counter)== None:
    widget.setVar(counter, 0)
else:
    widget.setVar(counter, widget.getVar(counter)+1)

cmds = []
for i in range(table.getRowCount()):
    table.setRowBackground(i, ColorFontUtil.WHITE)
    cmds.append(SetCommand('xpos', float(table.getCellText(i, 1))))
    cmds.append(SetCommand('ypos', float(table.getCellText(i, 2))))
    cmds.append(SetCommand('setpoint', float(table.getCellText(i, 3)), 'readback'))
    cmds.append(LogCommand([ 'xpos', 'ypos', 'readback' ]))
    
seq = CommandSequence(cmds)
id = client.submit("Point by Point Scan " + str(widget.getVar(counter)), seq)

display.setVar("LatestPointScanID", id)

showScans()
showPlot("Point by Point Scan", id, 'xpos', 'ypos')
     
    
