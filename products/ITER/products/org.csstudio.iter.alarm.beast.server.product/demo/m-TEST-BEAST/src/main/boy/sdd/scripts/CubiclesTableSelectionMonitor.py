from org.csstudio.opibuilder.scriptUtil import PVUtil
from org.csstudio.opibuilder.scriptUtil import DataUtil
from org.csstudio.opibuilder.scriptUtil import ScriptUtil
from org.csstudio.swt.widgets.natives.SpreadSheetTable  import ITableSelectionChangedListener
from java.util import Arrays

table = widget.getTable()
fct_name=display.getPropertyValue("name")
class SelectionListener(ITableSelectionChangedListener):
    def selectionChanged(self, selection):
    	cuIndex=""
    	phyName=""
        for row in selection:
        	cuIndex=row[0];
        	phyName=row[1]
        # change $(CU_INDEX) substitution
        macroInput = DataUtil.createMacrosInput(True)
        macroInput.put("CUB", cuIndex)
        macroInput.put("PHY_NAME", phyName)
        macroInput.put("FCT_NAME", fct_name)
        # open OPI
        # see https://svnpub.iter.org/codac/iter/codac/dev/units/m-css-boy/trunk/org.csstudio.opibuilder/src/org/csstudio/opibuilder/scriptUtil/ScriptUtil.java
        ScriptUtil.openOPI(display.getWidget("Table"), fct_name+"-CubicleDetails.opi", 1, macroInput)
table.addSelectionChangedListener(SelectionListener())