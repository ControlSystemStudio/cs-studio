program myControlStateMachine("xxx=xxx")

/*typedef enum {errlogInfo, errlogMinor, errlogMajor, errlogFatal} errlogSevEnum;
epicsShareFunc int errlogSevPrintf(
    const errlogSevEnum severity,const char *pformat, ...) EPICS_PRINTF_STYLE(2,3); */
%%#include <errlog.h>
foreign errlogInfo, errlogMinor, errlogMajor, errlogFatal;

%%#include <math.h> 
%%double sin(double x);
foreign sin;

%%epicsTimeStamp ts;
foreign ts;
foreign secPastEpoch;
/*=================== declarations =========================*/
short waveform;
assign waveform to "CWS-TCPH-F{xxx}:WAVEFORM-TYPE";
monitor waveform;
evflag waveformFlag; sync waveform waveformFlag;

short NONE;
short SINE;
short SQUARE;
short TRIANGLE;

float ao_monitor;
assign ao_monitor to "CWS-TCPH-F{xxx}:SIG-GENERATED";
monitor ao_monitor;

float ao_generated;
assign ao_generated to "CWS-TCPH-F{xxx}:SIG-GENERATED";

float LOLO;
assign LOLO to "CWS-TCPH-F{xxx}:SIG-GENERATED.LOLO";
monitor LOLO;
float LOW;
assign LOW to "CWS-TCPH-F{xxx}:SIG-GENERATED.LOW";
monitor LOW;
float HIGH;
assign HIGH to "CWS-TCPH-F{xxx}:SIG-GENERATED.HIGH";
monitor HIGH;
float HIHI;
assign HIHI to "CWS-TCPH-F{xxx}:SIG-GENERATED.HIHI";
monitor HIHI;
float freq;
assign freq to "CWS-TCPH-F{xxx}:FREQ";
monitor freq;
float ampl;
assign ampl to "CWS-TCPH-F{xxx}:AMPL";
monitor ampl;
 
short start_acq;
assign start_acq to "CWS-TCPH-F{xxx}:START-ACQ";
 
short generator_state;
assign generator_state to "CWS-TCPH-F{xxx}:GENERATOR-STATE";

short NOTREADY;
short READY;
short GENERATING;

short limit_state;
assign limit_state to "CWS-TCPH-F{xxx}:SIG-STATE";

float delta;
float rate;
short prev_status;
short channel_status;

evflag connectedFlag;
evflag disconnectedFlag;

/* Trace message record limited to 40 characters */
string msg;
assign msg to "CWS-TCPH-F{xxx}:TRACEMSG";

/* State to detect alarm limits */
ss limit_detection {

  state init {
    entry {
		NONE=0; SINE=1; SQUARE=2; TRIANGLE=3;
		NOTREADY=0; READY=1; GENERATING=2;
    }
    
    /* Initialize */
    when (pvConnectCount() == pvChannelCount() ) {
		strcpy(msg, "All channels connected"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);
			
	    generator_state = READY;
	    pvPut(generator_state);
	    efClear(disconnectedFlag);
	    efSet(connectedFlag);
    } state low

    when (delay(5.0)) {
		strcpy(msg, "...waiting"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);
    } state init
  }

  state low {
    when (ao_monitor > HIGH) {
		strcpy(msg, "High"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

	    limit_state = TRUE;
	    pvPut(limit_state);
    } state high

    when (pvConnectCount() < pvChannelCount() ) {
		strcpy(msg, "Connection lost"); 
		errlogSevPrintf(errlogMajor, "%s\n",msg);
		pvPut(msg);

	    efClear(connectedFlag);
	    efSet(disconnectedFlag);
    } state init
  }

  state high {
    when (ao_monitor < LOW) {
		strcpy(msg, "Low"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

	    limit_state = FALSE;
	    pvPut(limit_state);
    } state low

    when (pvConnectCount() < pvChannelCount() ) {
      efSet(disconnectedFlag);
    } state init
  }
}

/* Signal Generator */
ss generate_signal {
  state init {
	entry {
		generator_state = GENERATING;
		pvPut(generator_state);
		
		if (waveform != NONE ) {
			strcpy(msg, "Stop EPICS Sine Calculation"); 
			errlogSevPrintf(errlogInfo, "%s\n",msg);
			pvPut(msg);
			
			start_acq = FALSE;
			pvPut(start_acq);
		}
	}
	
    when (efTest(connectedFlag) && efTest(waveformFlag) && waveform == NONE ) {
		strcpy(msg, "No waveform selected"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);
	    efClear(waveformFlag);
    } state init
       
    when (efTest(connectedFlag) && waveform == SINE ) {
		strcpy(msg, "start sine signal"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

        delta = 0;
    } state sine
    
    when (efTest(connectedFlag) && waveform == SQUARE ) {
		strcpy(msg, "start square signal"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

        delta = 0;
    } state square
    
    when (efTest(connectedFlag) && waveform == TRIANGLE ) {
		strcpy(msg, "start triangle signal"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

        delta = 0.2;
    } state triangle
  }
  
/* Generate a sine */
  state sine {
    when (delay(0.1)) {
      delta += 1;
      ao_generated = ampl * sin(freq*delta);
      pvPut(ao_generated);
    } state sine

    when (efTestAndClear(waveformFlag) || efTestAndClear(disconnectedFlag)) {
    } state init
  }
  
/* Generate a square */
  state square {
    when (delay(0.1)) {
      delta += 1;
      /* A square waveform can be defined as the sign function of a sinusoid */
      ao_generated = ampl * sin(freq*delta);
      ao_generated = ((ao_generated > 0) - (ao_generated < 0)) * ampl;
      pvPut(ao_generated);
    } state square

    when (efTestAndClear(waveformFlag) || efTestAndClear(disconnectedFlag)) {
    } state init
  }

/* Generate a ramp up/down */
  state triangle {
    when (delay(0.1)) {
      rate = 0.4;
      if ( ao_generated >= ampl ) delta = -rate;
      if ( ao_generated <= -ampl ) delta = rate; 

      ao_generated += delta;
      pvPut(ao_generated);
    } state triangle

    when (efTestAndClear(waveformFlag) || efTestAndClear(disconnectedFlag)) {
    } state init
  }
}

/* Check for channel status; print exceptions */
ss channel_status_check {
  state init {
    when (efTest(connectedFlag)) {
		strcpy(msg, "start channel_status_check"); 
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

	    prev_status = pvStatus(ao_monitor);
    } state status_check
  }

  state status_check {
    when ((channel_status = pvStatus(ao_monitor)) != prev_status) {
	    /* epicsTimeStamp: secPastEpoch
	    The number of seconds since January 1, 1990 
	    nsec - nanoseconds within a second */
        ts = pvTimeStamp(ao_monitor);
		sprintf(msg, "AO Status=%d - Severity=%d at %d", \
			pvStatus(ao_monitor), \
			pvSeverity(ao_monitor), \
			ts.secPastEpoch);
		errlogSevPrintf(errlogInfo, "%s\n",msg);
		pvPut(msg);

	    prev_status = channel_status;
    } state status_check
  }
}