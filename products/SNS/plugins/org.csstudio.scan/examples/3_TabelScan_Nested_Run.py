"""
Schedule scan with parameters from BOY table.

@author:Xihui Chen
"""

from scan_client import *
from scan_ui import *

table = display.getWidget("ScanTable0").getTable()

counter="Counter"

if widget.getVar(counter)== None:
    widget.setVar(counter, 0)
else:
    widget.setVar(counter, widget.getVar(counter)+1)


id =scan("Nested Scan " + str(widget.getVar(counter)),
     (table.getCellText(0,0), float(table.getCellText(0,1)), 
      float(table.getCellText(0,2)), float(table.getCellText(0,3))),
     (table.getCellText(1,0), float(table.getCellText(1,1)), 
      float(table.getCellText(1,2)), float(table.getCellText(1,3))),
     'readback')
    
    
showScans()
showPlot("TableScan0", id, 'xpos', 'ypos')