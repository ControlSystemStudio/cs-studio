package org.csstudio.diag.postanalyser;


public class GaussCalculation {
	double[] gaussArray;
	double Background;
	double Ampl;
	double mean;
	double delta;
	
	double BackgroundCur;
	double AmplCur;
	double meanCur;
	double deltaCur;
	final boolean debug=false;
	
	public double getDelta() {return delta;}
	public double getAmpl() {return Ampl;}
	public double getMean() {return mean;}
	public double getBackground() {return Background;}
	public double[] getGauss() {return gaussArray;}
	
	public  boolean GaussCalculationProc(double[] rawX,double[] timeX) {
		int i;
		int len=rawX.length;
		double[] raw =  new double[len];
		double[] time=	new double[len];
		double rMin=rawX[0];
		double rMax=rawX[0];
		double tMin=timeX[0];
		double tMax=timeX[0];
		int indexMin=0;
		int indexMax=0;
		boolean error0,error1,error2;
		
		for(i=0;i<len;i++) {
			if (rawX [i] > rMax ) { rMax= rawX[i]; indexMax=i;}
			if (timeX[i] > tMax ) tMax=timeX[i];
			if (rawX [i] < rMin ) {rMin= rawX[i];indexMin=i;}
			if (timeX[i] < tMin ) tMin=timeX[i];
		}
		for(i=0;i<len;i++) {
			time[i]= ( (timeX[i] - tMin) / (tMax-tMin)) - 0.5;
			raw[i]=     (rawX[i] - rMin) / (rMax-rMin) ;
		}
		

		data d0=null,d1=null,d2=null;
		double calc0= (double) Math.pow(10,32);
		double calc1= (double) Math.pow(10,32);
		double calc2= (double) Math.pow(10,32);
		
		
		double[] shortFit;
		if ( (shortFit=GaussCalculationProcShortArea(raw,time,0.0))==null ) {
			System.out.println("GaussCalculationProcShortArea failed");
			error0=true;
		} else {
			error0=false;
			d0=new data(shortFit,BackgroundCur,AmplCur,meanCur,deltaCur );
			calc0= d0.calcLSF(raw);
		}
		
		if ( (shortFit=GaussCalculationProcShortArea(raw,time,time[indexMax]))==null ) {
			System.out.println("GaussCalculationProcShortArea failed");
			error1=true;
		} else {
			error1=false;
			d1=new data(shortFit,BackgroundCur,AmplCur,meanCur,deltaCur );
			calc1= d1.calcLSF(raw);
		}
		if ( (shortFit=GaussCalculationProcShortArea(raw,time,time[indexMin]))==null ) {
			System.out.println("GaussCalculationProcShortArea failed");
			error2=true;
		} else {
			error2=false;
			d2=new data(shortFit,BackgroundCur,AmplCur,meanCur,deltaCur );
			calc2= d2.calcLSF(raw);
		}
		
		
		if (error0&&error1&&error2) {
			System.out.println("bad fitting!");
			return false;
		}
		
		double calc =  Math.min(calc0, calc1);
		calc =  Math.min(calc, calc2);
		data d=d0;
		if (calc0==calc)  d= d0; 
		else if (calc1==calc)  d= d1; 
		else if (calc2==calc)  d= d2; 
		else System.out.println("bad Calc="+calc);
		if (d==null) 	{
			System.out.println("Bad fitting!");
			return false;
		}
		
		shortFit=d.getGauss();
		Background=d.getBackground();
		Ampl=d.getAmpl();
		mean=d.getMean();
		delta=d.getDelta();
		
		
		if(debug) {
			System.out.println("calcGauss INSIDE11" + "B="+Background+" A="+Ampl+" m="+mean+" d="+delta);
			System.out.println("shortFit="+shortFit[0]);
		}
		
		gaussArray=new double[len];
		for(i=0;i<len;i++) {
			gaussArray[i]=  rMin + ( (rMax-rMin)*shortFit[i] ) ;
			if(debug) System.out.print(" "+gaussArray[i]);
		}
		if(debug) System.out.println("\n");
		return true;	
	}
	
	
	  double[] GaussCalculationProcShortArea(double[] raw,double[] time, double meanEst)	{
		int i;
		int len=raw.length;
		double[] gaussArrayShort  = new double[len];
		double deltaEst;
		double av=0.0;
		for(i=0;i<len;i++) av+=raw[i];
		av= av/len;
		double d=0.0;
		for(i=0;i<len;i++) d += (raw[i]-av)*(raw[i]-av) ;
		d = d/len;
		deltaEst = Math.sqrt(d);
	
		if(deltaEst==0.0) {
			System.out.println("GaussCalculation Error -  Zero delta");
			return null;
		}
	
		

		double S_y  =0.0, S_g=0.0 ,S_yg=0.0 , S_g_2=0.0; 
		
		for(i=0;i<len;i++) {
			S_y += (raw[i]);
			S_g += GaussExp(time[i],deltaEst,meanEst);
			S_yg +=  ( raw[i]*GaussExp(time[i],deltaEst,meanEst) );
			S_g_2 += ( GaussExp(time[i],deltaEst,meanEst)*GaussExp(time[i],deltaEst,meanEst) );
		}

		
		if(S_g_2==0.0) {
			System.out.println("GaussCalculation Error -  Zero values");
			return null;
		}
		if( S_g_2-(S_g)*(S_g) == 0) {
			System.out.println("GaussCalculation Error -  Zero values");
			return null;
		}
		double AmplEst= (S_yg - (S_y * S_g) ) /( S_g_2-(S_g)*(S_g) );
		
		double BackgroundEst = S_y - (AmplEst * S_g); 

		
		
		for(i=0;i<len;i++) {
			gaussArrayShort[i]= BackgroundEst + AmplEst* GaussExp(time[i],deltaEst,meanEst);
			if(debug) System.out.print(" "+gaussArrayShort[i]);
		}
		if(debug)System.out.println("calcGauss INSIDE" + "B="+BackgroundEst+" A="+AmplEst+" m="+meanEst+" d="+deltaEst);
		BackgroundCur=BackgroundEst;
		AmplCur=AmplEst;
		meanCur=meanEst;
		deltaCur=deltaEst;		
		return gaussArrayShort;
	}
	
	private static double GaussExp(double x,double d, double a) {
		double twopi = 2.*Math.PI;
		double r=x-a;
		double pre = (r*r) / (2*d*d) ;
		double y= Math.exp(-pre);
		double z = y/(twopi*d);
		if(false) System.err.println("\t\t!!!z="+z+" pre="+pre +" y="+y);
		return z;
	}
	

	class data {
		double[] gauss;
		double Background;
		double Ampl;
		double mean;
		double delta;
		double lsf;
		public void setGauss(double[] x) {  this.gauss = x;}
		public void setAmpl(double x) {  this.Ampl = x;}
		public void setBackground(double x) {  this.Background = x;}
		public void setMean(double x) {  this.mean = x;}
		public void setDelta(double x) {  this.delta = x;}
		public double[] getGauss() {  return gauss;}
		public double getAmpl() {  return Ampl; }
		public double getBackground() {  return Background;}
		public double getMean() {  return mean; }
		public double getDelta() {  return delta; }
		public double getlsf() {  return lsf; }
		public data(double[] g,double B, double A, double m, double d ) {
			gauss=g;
			Background=B;
			Ampl=A;
			mean=A;
			delta=A;
			lsf=0.0;
		}
		public double calcLSF(double[] raw) {
			lsf=0.0;
			for (int i=0;i<raw.length;i++) lsf += ( (raw[i]-gauss[i])*(raw[i]-gauss[i])   ) ;
			return lsf;
		}
	}
}

//org.csstudio.diag.postanalysis.GaussCalculations.Gauss(raw,time,gaussArray,B,A,m,d))