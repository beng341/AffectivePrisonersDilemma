package affectiveprisonersdilemma;

/**
 *
 * @author Ben Armstrong
 */
public class EPATest {
    public static void main(String[] args) {
        
        
        EPA a = new EPA(2.75, 1.88, 1.38);
        EPA collab = new EPA(1.44, 1.11, 0.61);
        EPA abandon = new EPA(-2.28, -0.48, -0.84);
        EPA o = new EPA(2.75, 1.88, 1.38);
        
        System.out.println("Deflection is: " + EPA.getDeflection(a, collab, o));
    }
}
