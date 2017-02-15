package affectiveprisonersdilemma;

import java.awt.Color;
import java.util.ArrayList;
import sim.engine.SimState;
import sim.field.grid.DenseGrid2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Int2D;

/**
 *
 * @author Ben Armstrong
 */
public class Population extends SimState {
    
    public final int COOP = 0;
    public final int DEFECT = 1;
    
    
//    int populationSize = 50;
//    public int getPopulationSize() { return populationSize; }
//    public void setPopulationSize(int size) { populationSize = size; }
    
    double costOfLiving = 0.5;
    public double getCostOfLiving() { return costOfLiving; }
    public void setCostOfLiving(double costOfLiving) { this.costOfLiving = costOfLiving; }
    
    double reproductionThreshold = 100.0;
    public double getReproductionThreshold() { return reproductionThreshold; }
    public void setReproductionThreshold(double reproductionThreshold) { this.reproductionThreshold = reproductionThreshold; }
    
    double deathThreshold = 0;
    public double getDeathThreshold() { return deathThreshold; }
    public void setDeathThreshold(double deathThreshold) { this.deathThreshold = deathThreshold; }
    
    double initialEnergy = 50.0;
    public double getInitialEnergy() { return initialEnergy; }
    public void setInitialEnergy(double initial) { this.initialEnergy = initial; }
    
    double maxEnergy = 150.0;
    public double getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(double initial) { this.maxEnergy = initial; }
    
    int numCooperators = 100;
    public int getNumCooperators() { return numCooperators; }
    public void setNumCooperators(int size) { numCooperators = size; }
    
    int numDefectors = 100;
    public int getNumDefectors() { return numDefectors; }
    public void setNumDefectors(int size) { numDefectors = size; }
    
    private ArrayList<Player> players = new ArrayList<>();
    
    public DenseGrid2D grid;
    
    public PopulationWithUI gui;
    
    /**
     * TODO: Make sure these values are alright.
     * Payoff matrix for PD game.
     * R S
     * T P
     */
    public int[][] payoffs = 
    {
        {3,-1},
        {5,0}
    };
    
    public Population(long seed) {
        super(seed);
        grid = new DenseGrid2D(100, 100);
    }
    
    
    @Override
    public void start() {
        
        super.start();
        
        grid.clear();
        players.clear();
        
        
        for( int i = 0; i < numCooperators; ++i ) {
            Player p = new Player(this, 0);
            players.add(p);
            p.setStoppable(schedule.scheduleRepeating(p));
        }
        for( int i = 0; i < numDefectors; ++i ) {
            Player p = new Player(this, 1);
            players.add(p);
            p.setStoppable(schedule.scheduleRepeating(p));
        }
        
        // surprisingly, this seems to finish in less than a second with 10,000
        // agents on a grid of 10,000 cells.
        int i = 0;
        while( i < numCooperators+numDefectors ) {
            int x = random.nextInt(grid.getHeight());
            int y = random.nextInt(grid.getWidth());
            
            if( grid.getObjectsAtLocation(x, y) == null ) {
                players.get(i).setLocation(new Int2D(x, y));
                grid.addObjectToLocation(players.get(i), players.get(i).getLocation());
                ++i;
            }
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    /**
     * Schedule a child player, add to grid, if necessary add it to the gui.
     * @param p 
     */
    public void addPlayer(Player p) {
        
        p.setStoppable(schedule.scheduleRepeating(p));
        grid.addObjectToLocation(p, p.getLocation());
        
        
        if( gui != null ) {
            if(p.getStrategy() == 0 ) {
                gui.gridPortrayal.setPortrayalForObject(p, new RectanglePortrayal2D(Color.blue));
            } else {
                gui.gridPortrayal.setPortrayalForObject(p, new RectanglePortrayal2D(Color.red));
            }
        }
    }
    
    public void removePlayer(Player p) {
        
        p.stop();
        grid.removeObjectAtLocation(p, p.getLocation());
    }
    
}