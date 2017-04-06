package affectiveprisonersdilemma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author Ben Armstrong
 */
public class DataGatherer implements Steppable {

    Population pop;
    /**
     * Used to store the total utility gained by each type in current round.
     * For simplicity with data analysis I just cast values to integers which
     * shouldn't be too bad since it was in increments of 0.5 before.
     */
    public int[] utilities;
    /**
     * Stores the population of each type in current round.
     */
    public int[] populations;
    
    public int[][] cooperations;
    public int[][] defections;
    public int movements;
    
    public DataGatherer(Population pop) {
        this.pop = pop;
        utilities = new int[pop.getNumTypes()];
        for(int i = 0; i < utilities.length; ++i) utilities[i] = 0;
        
        populations = new int[pop.getNumTypes()];
        for(int i = 0; i < populations.length; ++i) populations[i] = 0;
        
        cooperations = new int[pop.getNumTypes()][pop.getNumTypes()];
        defections = new int[pop.getNumTypes()][pop.getNumTypes()];
        movements = 0;
        
    }
    
    /**
     * Tracks data such as total utility gained by each player type. The data is
     * then output to a file at the end of the simulation.
     * @param state 
     */
    @Override
    public void step(SimState state) {
        ArrayList<Player> players = pop.getPlayers();
        
        for( Player p: players ) {
            utilities[p.typeNumber] += p.getLastPayoff();
            populations[p.typeNumber] += 1;
            
            if(null != p.getLastPartner() ) {
                if( p.getLastAction() == 0 ) {
                    defections[p.typeNumber][p.getLastPartner().typeNumber] += 1;
                } else if (p.getLastAction() == 1 ) {
                    cooperations[p.typeNumber][p.getLastPartner().typeNumber] += 1;
                }
            } else if( p.getLastAction() == 2 ) {
                movements += 1;
            }
        }
    }
    
}
