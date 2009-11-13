package org.csstudio.nams.common;

import org.csstudio.nams.common.contract.Contract_Test;
import org.csstudio.nams.common.decision.StandardAblagekorb_Test;
import org.csstudio.nams.common.decision.Vorgangsmappe_Test;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung_Test;
import org.csstudio.nams.common.fachwert.MessageKeyEnum_Test;
import org.csstudio.nams.common.fachwert.Millisekunden_Test;
import org.csstudio.nams.common.material.AlarmNachricht_Test;
import org.csstudio.nams.common.material.Regelwerkskennung_Test;
import org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.NichtVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableRegel_Test;
import org.csstudio.nams.common.material.regelwerk.Pruefliste_Test;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis_Test;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk_Test;
import org.csstudio.nams.common.material.regelwerk.StringRegel_Test;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegelAlarmBeiBestaetigung_Test;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel_Test;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Suite.class)
@SuiteClasses( { CommonActivator_Test.class,

Contract_Test.class,

StandardAblagekorb_Test.class,
Vorgangsmappe_Test.class,
Vorgangsmappenkennung_Test.class,

Millisekunden_Test.class,
MessageKeyEnum_Test.class,

AlarmNachricht_Test.class,
Regelwerkskennung_Test.class,

AbstractNodeVersandRegel_Test.class,
NichtVersandRegel_Test.class,
OderVersandRegel_Test.class,
ProcessVariableRegel_Test.class,
Pruefliste_Test.class,
RegelErgebnis_Test.class,
Regelwerkskennung_Test.class,
StandardRegelwerk_Test.class,
StringRegel_Test.class,
TimeBasedRegel_Test.class,
TimeBasedRegelAlarmBeiBestaetigung_Test.class,
UndVersandRegel_Test.class,
WeiteresVersandVorgehen_Test.class,
})
public class AllTests {
}
