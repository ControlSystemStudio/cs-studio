from org.csstudio.opibuilder.scriptUtil import PVUtil
from org.csstudio.opibuilder.scriptUtil import ColorFontUtil


table = widget.getTable()
nbColPVs=2
#find index of the trigger PV
i=0
while triggerPV != pvs[i]:
    i+=1

table.setCellText(i/nbColPVs, i%nbColPVs +3, PVUtil.getString(triggerPV))

s = PVUtil.getSeverity(triggerPV)

color = ColorFontUtil.WHITE
if s == 0:
    color = ColorFontUtil.GREEN
elif s == 1:
    color = ColorFontUtil.RED
elif s == 2:
    color = ColorFontUtil.YELLOW
elif s == 3:
    color = ColorFontUtil.PINK
    
table.setCellBackground(i/nbColPVs, i%nbColPVs + 3, color)