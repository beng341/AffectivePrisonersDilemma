package affectiveprisonersdilemma;

import ec.util.MersenneTwisterFast;
import java.awt.Color;

/**
 *
 * @author Ben Armstrong
 */
public class EPA {
    
    
    private static boolean[][] terms = 
    {
        {false, false, false, false, false, false, false, false, false},
        {true, false, false, false, false, false, false, false, false},
        {false, true, false, false, false, false, false, false, false},
        {false, false, true, false, false, false, false, false, false},
        {false, false, false, true, false, false, false, false, false},
        {false, false, false, false, true, false, false, false, false},
        {false, false, false, false, false, true, false, false, false},
        {false, false, false, false, false, false, true, false, false},
        {false, false, false, false, false, false, false, true, false},
        {false, false, false, false, false, false, false, false, true},
        {true, false, false, true, false, false, false, false, false},
        {true, false, false, false, false, false, false, true, false},
        {false, true, false, false, true, false, false, false, false},
        {false, false, true, false, false, true, false, false, false},
        {false, false, false, true, false, false, true, false, false},
        {false, false, false, true, false, false, false, true, false},
        {false, false, false, false, true, false, true, false, false},
        {false, false, false, false, true, false, false, true, false},
        {true, false, false, true, false, false, true, false, false},
        {true, false, false, true, false, false, false, true, false}
    };
    private static double[][] coefficients = 
    {
        { -0.26, -0.10,  0.14, -0.19,  0.06,  0.11, -0.11, -0.37,  0.02 },
        {  0.41,  0.00,  0.05,  0.11,  0.00,  0.02,  0.00,  0.00,  0.00 },
        {  0.00,  0.56,  0.00,  0.00,  0.16, -0.06,  0.00,  0.00,  0.00 },
        {  0.00,  0.06,  0.64,  0.00,  0.00,  0.27,  0.00,  0.00,  0.00 },
        {  0.42, -0.07, -0.06,  0.53, -0.13,  0.04,  0.11,  0.18,  0.02 },
        { -0.02,  0.44,  0.00,  0.00,  0.70,  0.00,  0.00, -0.11,  0.00 },
        { -0.10,  0.00,  0.29, -0.12,  0.00,  0.64,  0.00,  0.00,  0.00 },
        {  0.03,  0.04,  0.00,  0.00,  0.03,  0.00,  0.61, -0.08,  0.03 },
        {  0.06,  0.00,  0.00,  0.05,  0.01,  0.00,  0.00,  0.66, -0.05 },
        {  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.03,  0.07,  0.66 },
        {  0.05,  0.00,  0.00,  0.00,  0.01,  0.00,  0.03,  0.00,  0.00 },
        {  0.03,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00 },
        {  0.00, -0.05,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00 },
        {  0.00,  0.00, -0.06,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00 },
        {  0.12,  0.01,  0.00,  0.11,  0.03,  0.00,  0.04,  0.03,  0.00 },
        { -0.05,  0.00,  0.00, -0.05,  0.00,  0.00,  0.00,  0.03,  0.00 },
        { -0.05,  0.00,  0.00, -0.02,  0.00,  0.00, -0.03,  0.00,  0.00 },
        {  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00, -0.05,  0.00 },
        {  0.03,  0.00,  0.00,  0.02,  0.00,  0.00,  0.00,  0.00,  0.00 },
        { -0.02,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00 }
    };
    
    double[] epa;
    
    public EPA(MersenneTwisterFast random) {
        epa = new double[]{ randomValue(random), randomValue(random), randomValue(random) };
    }
    
    public EPA(double e, double p, double a) {
        epa = new double[]{e, p, a};
    }
    
    /**
     * Returns the vector difference between the first and second EPA values
     * given. (Actually returns the square of the difference for speed.)
     * @return 
     */
    public static double distance(EPA f, EPA s) {
        double sum = 0;
        for(int i = 0; i < 3; ++i) {
            sum += (f.epa[i] - s.epa[i])*(f.epa[i] - s.epa[i]);
        }
        return sum;
    }
    
    /**
     * Uses USA ABOmale coefficients found in Coefficients.java file included
     * with Interact to find the EPA location of the deflection minimizing 
     * action for the given actor to take.
     * @param actor
     * @param object
     * @return 
     */
    public static EPA getDeflectionMinimizingAction(EPA actor, EPA object) {
        
        
        
        return actor;
    }
    
    
    
    /**
     * Uses USA male coefficients (Indiana 2002?) to get the deflection based on
     * the given actor, behaviour, and object.
     * @param actor
     * @param behaviour
     * @param object
     * @return 
     */
    public static double getDeflection(EPA actor, EPA behaviour, EPA object) {
        
        // naive but it works so shut up
        double[] fundamentals = new double[9];
        for(int i = 0; i < 3; ++i) {
            fundamentals[i] = actor.epa[i];
        }
        for(int i = 0; i < 3; ++i) {
            fundamentals[i+3] = behaviour.epa[i];
        }
        for(int i = 0; i < 3; ++i) {
            fundamentals[i+6] = object.epa[i];
        }
        
        // some intermediate values, combinations of certain fundamentals
        double[] t = new double[20];
        t[0] = 1;
        for(int i = 1; i < 1+fundamentals.length; ++i) {
            t[i] = fundamentals[i-1];
//            System.out.print("t["+i+"]: "+t[i]+ ", ");
        }
        for(int row = 10; row < terms.length; ++row) {
            t[row] = 1;
            for(int col = 0; col < terms[row].length; ++col) {
                if(terms[row][col]) {
                    t[row] *= t[col+1];
                }
            }
//            System.out.print("t["+row+"]: "+t[row]+ ", ");
        }
        
//        System.out.print("\n\n");
        double[] tau = new double[9];   // contains transients
        for(int col = 0; col < 9; col++) {
            tau[col] = 0;
            for(int i = 0; i < coefficients.length; ++i) {
                tau[col] += t[i] * coefficients[i][col];
            }
//            System.out.print("tau["+col+"]: "+tau[col]+ ", ");
        }
        
        double deflection = 0;
        for(int i = 0; i<tau.length; ++i) {
            deflection += (fundamentals[i]-tau[i])*(fundamentals[i]-tau[i]);
        }
        
        return deflection;
    }
    
    /**
     * Get a random EPA value. Technically not uniformly distributed but close
     * enough.
     * @param random
     * @return 
     */
    public static double randomValue(MersenneTwisterFast random) {
        return (random.nextDouble()-0.5)*8.6;
    }
    
    public Color colorForEPA() {
        return new Color((float)((epa[0]+4.3)/8.6), (float)((epa[1]+4.3)/8.6), (float)((epa[2]+4.3)/8.6));
    }
    
    public String toString() {
        return "("+epa[0]+", "+epa[1]+", "+epa[2]+")";
    }
}