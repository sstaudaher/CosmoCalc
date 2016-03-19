
public class CosmoCalc {
	public double angularDiameterDist;
	public double lumDistMeters;
	public double lumDistPC;
	public double distModulus;
	public double lookbackTime;
	public Cosmology cosmo;
	
	public CosmoCalc(Cosmology c) {
		cosmo = c;
		double[] paramArr = getParams(cosmo);
		angularDiameterDist = paramArr[0];
		lumDistMeters = paramArr[1];
		lumDistPC = paramArr[2];
		distModulus = paramArr[3];
		lookbackTime = paramArr[4];
	}
	
	private double[] getParams(Cosmology c) {
		double LIGHT_SPEED = 299792458.0; //m s^{-1}
		double M_PER_PC = 3.08568025e16d;
		double KM_PER_MPC = 3.08568025e19d;
		
		double h0t = c.h0/KM_PER_MPC;	//s^{-1}
		double dH = LIGHT_SPEED/h0t;	//m
		double tH = 1.0/h0t;			//s
		
		double[] zInt = getZInt(c);
		double[] eTotal = getE(c, zInt);
		double dM = getDM(c, zInt, eTotal, dH);
		
		double angularDiameterDist = dM/(1.0 + c.z);
		double lumDistMeters = dM*(1.0 + c.z);
		double lumDistPC = lumDistMeters/M_PER_PC;
		double distModulus = 5.0*Math.log10(lumDistPC) - 5.0;
		double lookbackTime = getLookbackTime(zInt, eTotal, tH);
		double[] returnArr = {angularDiameterDist, lumDistMeters, lumDistPC,
				  distModulus, lookbackTime};
		return returnArr;
	}

	private double[] getZInt(Cosmology c) {
		int N_ELEMENTS = 10001;
		double zDiff = c.z/(N_ELEMENTS - 1);
		return genDoubleX(zDiff, N_ELEMENTS);
	}

	private double[] getE(Cosmology c, double[] zInt) {
		double[] eTotal = new double[zInt.length];
		for (int i = 0; i < zInt.length; i++) {
			double eOmegaMatter = c.omegaMatter*Math.pow(zInt[i] + 1.0, 3.0);
			double eOmegaK = c.omegaK*Math.pow(zInt[i] + 1.0, 2.0);
			eTotal[i] = Math.pow(eOmegaMatter + eOmegaK + c.omegaLambda, 0.5);			
		}
		return eTotal;
	}
	
	private double getDM(Cosmology c, double[] zInt, double[] eTotal, double dH) {
		double partialDC = integrateTrapezoid(zInt, powerArray(eTotal, -1.0));
		double dM = 0;
		if (c.omegaK > 0.0) {
			double sqrtOmegaK = Math.pow(c.omegaK, 0.5);
			double sinhInside = sqrtOmegaK*partialDC;
			dM = dH/sqrtOmegaK*Math.sinh(sinhInside);
		}
		if (c.omegaK == 0.0) {
			dM = dH*partialDC;
		}
		if (c.omegaK < 0.0) {
			double sqrtOmegaK = Math.pow(Math.abs(c.omegaK), 0.5);
			double sinhInside = sqrtOmegaK*partialDC;
			dM = dH/sqrtOmegaK*Math.sinh(sinhInside);
		}		
		return dM;
	}
	
	private double getLookbackTime(double[] zInt, double[] eTotal, double tH) {
		double S_PER_YEAR = 31558150.0;
		double[] yInt = new double[zInt.length];
		for (int i = 0; i < zInt.length; i++) {
			yInt[i] = 1.0/(1.0+zInt[i])/eTotal[i];
		}
		return tH*integrateTrapezoid(zInt, yInt)/S_PER_YEAR;
	}
	
	private double[] genDoubleX(double xDiff, int N_ELEMENTS) {
		double[] newArray = new double[N_ELEMENTS];
		for (int i = 0; i < N_ELEMENTS; i++) {
			newArray[i] = xDiff*i;
		}
		return newArray;
	}

	private double integrateTrapezoid(double x[], double y[]) {
		double sum = 0.0;
		for (int i = 0; i < x.length - 1; i++) {
			double baseLength = x[i+1] - x[i];
			double triangleArea = Math.abs(y[i] - y[i+1])*baseLength/2.0;
			double baseArea = Math.min(y[i], y[i+1])*baseLength;
			sum += triangleArea + baseArea;
		}
		return sum;
	}
	
	private double[] powerArray(double[] arr, double power) {
		double[] arrPower = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			arrPower[i] = Math.pow(arr[i], power);
		}
		return arrPower;
	} 
}
