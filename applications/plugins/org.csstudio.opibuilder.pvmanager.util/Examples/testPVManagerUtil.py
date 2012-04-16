from org.csstudio.opibuilder.scriptUtil import PVUtil, ConsoleUtil
from org.csstudio.opibuilder.pvmanager.util import PVManagerUtil
from org.epics.pvmanager import PVReaderListener
from org.eclipse.gef import EditPartListener

s = 'pvReader'
if widget.getVar(s)!=None:
    widget.getVar(s).close()

pvName=PVUtil.getString(pvs[0])

pvReader = PVManagerUtil.createObjectPVReader(pvName, 50, True)
class UpdateTask(PVReaderListener):
    def pvChanged(self):
        value = pvReader.getValue()
        if value== None or pvReader.lastException() != None:
            ConsoleUtil.writeError(pvReader.lastException().toString())
            return;
        widget.setValue(pvReader.getValue().getArray());

pvReader.addPVReaderListener(UpdateTask())

widget.setVar(s, pvReader)

class ClosePVReader(EditPartListener):
    def partDeactivated(self, editpart):
        pvReader.close()

widget.addEditPartListener(ClosePVReader())