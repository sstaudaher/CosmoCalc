/* A class to hold our cosmology variables */
public class Cosmology {
	/* default values */
	public double z;
	public double omegaMatter = 0.3;
	public double omegaLambda = 0.7;
	public double omegaK = 0.0;
	public double h0 = 70.0;
	
	public Cosmology(double z) {
		this.z = z;
	}
	
	public Cosmology(double z, double oM, double oL) {
		this.z = z;
		omegaMatter = oM;
		omegaLambda = oL;
	}
	
	public Cosmology(double z, double oM, double oL, double oK) {
		this.z = z;
		omegaMatter = oM;
		omegaLambda = oL;
		omegaK = oK;
	}
	
	public Cosmology(double z, double oM, double oL, double oK, double h) {
		this.z = z;
		omegaMatter = oM;
		omegaLambda = oL;
		omegaK = oK;
		h0 = h;
	}
}
