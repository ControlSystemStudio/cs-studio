package org.csstudio.diag.postanalyser;

public class FFTcalculations {
  static double twopi = 2.*Math.PI, sq2pi = 1./Math.sqrt(twopi); 
                   
  public static void fourier(int N, double[] signal, double dftreal[], double[] dftimag ,double[] dftamp)
  {
    double real, imag;                                // Calc & plot Y(w)
    int n, k; 
    for ( n = 0;  n < N;  n++ ) {                     // Loop on frequency
      real = imag = 0. ;                              // Clear variables
      for ( k = 0;  k < N;  k++ ){                    // Loop for sums
        real += signal[k]*Math.cos((twopi*k*n)/N); 
        imag += signal[k]*Math.sin((twopi*k*n)/N); 
      }
      dftreal[n] = 2*real /N ; 
      if(n==0) dftreal[n] /= 2;
      dftimag[n] = 2*imag /N ;
      dftamp[n]=Math.sqrt(dftreal[n]*dftreal[n] +  dftimag[n] *dftimag[n] );
    }
    
  }

  public static void main(String[] argv){
	    int Np=1024;
	    double w=2.0;
	    double Amp=5.0;
	    double Offset=2.0;
	    double dftreal[] = new double[Np]; 
	    double dftimag[] = new double[Np]; 
	    double dftamp[] = new double[Np]; 
	    double Signal[] = new double[Np]; 
	    for(int i=0; i<Np; i++) {
	    	Signal[i]=Offset + Amp*Math.cos(w*twopi*i/Np); 
	    }
	    fourier(Np, Signal, dftreal, dftimag ,dftamp);
	    System.out.println("Start:");
	    for(int i=0; i<Np/2; i++) {
	    	if(Math.abs(dftamp[i]) >0.01) System.out.println("i="+i+
	    			"Amp="+dftamp[i]+ "Re="+dftreal[i]+ "Im="+dftimag[i]);
	    }
	    
	  }

  
  public static double[] dftCalc(double[] rawData,int num,int windowFun) {
	 double[] amp=new double[num];
	  double[] dftreal= new double[num];
	  double[] dftimag= new double[num];
	  if (windowFun == 0) {
		  fourier(num, rawData, dftreal, dftimag ,amp); 
	  } else {
			double[] win_coeffs = new double[num];
			for(int i=0; i<num; i++) win_coeffs[i]=1.0;
			win_coeffs = window_coefficients( windowFun, win_coeffs );			
			double[] new_spectrum = new double[num];
			for(int i=0; i<num; i++)
				new_spectrum[i] = rawData[i] * win_coeffs[i];
			fourier(num, new_spectrum, dftreal, dftimag ,amp); 
	  }
	  
    return amp;
  }
  
 public  static double[] window_coefficients( int win_type, double[] coeffs )
  {
  	int n;
  	int m;
  	double twopi;

  	m = coeffs.length;
  	twopi = 2.*Math.PI;
  	switch( win_type )
  	{
  		case 1:  /* Hamming   */
  			for( n = 0; n < m; n++ )
  			{
  				coeffs[n] = 0.54 - 0.46*Math.cos( twopi*n/(m-1) );					
  				coeffs[n] *= 0.5*(1. - Math.cos(twopi*n/(m-1)) );
  			}
  			break;
  		case 2:  /* von Hann (sometimes improperly called Hanning)  */
  			for( n = 0; n < m; n++ )
  			{
  				coeffs[n] = 0.5*(1.0 - Math.cos(twopi*n/(m-1)) );
  			}
  			break;
  		case 3:  /* Blackman  */
  			for( n = 0; n < m; n++ )
  			{
  				coeffs[n] = 0.42 - 0.5*Math.cos(twopi*n/(m-1)) +
  					       0.08*Math.cos(2.*twopi*n/(m-1));
  			}
  		case 4:  /* Bartlett  */
  			for( n = 0; n <= (m-1)/2; n++ )
  			{
  				coeffs[n] = 2.*n/(m-1);
  			}
  			for( n = (m-1)/2; n < m; n++ )
  			{
  				coeffs[n] = 2. - 2.*n/(m-1);
  			}
  			break;
  		case 5: /* 4 term Blackman-Harris  */
  			double a0;
  			double a1;
  			double a2;
  			double a3;
  			
  			a0 = 0.35875;
  			a1 = 0.48829;
  			a2 = 0.14128;
  			a3 = 0.01168;

  			for( n = 0; n < m; n++ )
  			{
  				coeffs[n] = a0 - a1* Math.cos(twopi*(double)(n+0.5)/m) +
  				             a2*Math.cos(twopi*2.*(double)(n+0.5)/m) - 
  				             a3*Math.cos(twopi*3.*(double)(n+0.5)/m);
  			}
  			break;
  		default:
  			break;
  	}
  	return coeffs;
  }

}
