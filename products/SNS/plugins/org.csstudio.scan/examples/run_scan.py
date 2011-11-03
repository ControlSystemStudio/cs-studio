"""
Schedule YABES scan with parameters from BOY script

@author: Kay Kasemir
"""

from org.csstudio.opibuilder.scriptUtil import PVUtil
from scan_client import *

pv = display.getWidget("x0").getPV();
x0 = PVUtil.getDouble(pv);
pv = display.getWidget("x1").getPV();
x1 = PVUtil.getDouble(pv);
pv = display.getWidget("dx").getPV();
dx = PVUtil.getDouble(pv);

pv = display.getWidget("y0").getPV();
y0 = PVUtil.getDouble(pv);
pv = display.getWidget("y1").getPV();
y1 = PVUtil.getDouble(pv);
pv = display.getWidget("dy").getPV();
dy = PVUtil.getDouble(pv);

pv = display.getWidget("updown").getPV();
if PVUtil.getLong(pv) > 0:
    toggle = -1
else:
    toggle = 1


scan('Scan',
     ('xpos', min(x0, x1), max(x0, x1), max(0.1, abs(dx))),
     ('ypos', min(y0, y1), max(y0, y1), toggle * max(0.1, abs(dy))),
     'readback')



