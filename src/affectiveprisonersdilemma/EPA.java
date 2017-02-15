package affectiveprisonersdilemma;

/**
 *
 * @author Ben Armstrong
 */
public class EPA {
    
    /**
     * Evaluation component; how "good" something is.
     */
    double e = 0;
    /**
     * Potency component; how powerful something is.
     */
    double p = 0;
    /**
     * Activity component; how active something is.
     */
    double a = 0;
    
    public EPA(double e, double p, double a) {
        this.e = e;
        this.p = p;
        this.a = a;
    }
    
    /**
     * TODO: Check if this is the right formula...
     * Returns the vector difference between the first and second EPA values given.
     * @return 
     */
    public static double deflection(EPA f, EPA s) {
        return Math.sqrt(f.e*f.e -s.e*s.e) + Math.sqrt(f.p*f.p -s.p*s.p) + Math.sqrt(f.a*f.a -s.a*s.a);
    }
    
    public static boolean shouldCooperate(EPA f, EPA s) {
        
        
        return true;    // you should always cooperate!
    }
    
}
