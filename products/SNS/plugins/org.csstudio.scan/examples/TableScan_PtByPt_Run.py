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

seq = CommandSequence()
for i in range(table.getRowCount()):
    table.setRowBackground(i, ColorFontUtil.WHITE)
    seq.set('xpos', float(table.getCellText(i, 1)))
    seq.set('ypos', float(table.getCellText(i, 2)))
    seq.set('setpoint', float(table.getCellText(i, 3)), 'readback', 0.1, 0)
    seq.log('readback')    
    
id = client.submit("Point by Point Scan " + str(widget.getVar(counter)), seq)


display.setVar("LatestPointScanID", id)

showScans()
showPlot("Point by Point Scan", id, 'xpos', 'ypos')
     
    
