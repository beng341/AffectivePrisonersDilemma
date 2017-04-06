package affectiveprisonersdilemma;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.Grid2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author Ben Armstrong
 */
public class Player implements Steppable{
    
    private EPA epa;
    public final int typeNumber;
    
    // agent dies at 0 energy, reproduces at 100 energy
    private double energy = 50;
    
    private Int2D location;
    
    private long lastActionTime = 0;
    
    // 0 for defect, 1 for cooperate, 2 for moving
    private int lastAction = -1;
    public int getLastAction() { return lastAction; }
    
    private double lastPayoff = 0;
    public double getLastPayoff() { return lastPayoff; }
    
    private Player lastPartner = null;
    public Player getLastPartner() { return lastPartner; }
    
    private final Population pop;
    
    private Stoppable stoppable;
    
    private ArrayList<Player> neighbours = new ArrayList<>();
    private Player neighbour;
    
    private static final EPA collab = new EPA(1.44, 1.11, 0.61);
    private static final EPA abandon = new EPA(-2.28, -0.48, -0.84);
    
//    public Player(Population state) {
//        this.pop = state;
//        this.energy = pop.initialEnergy;
//        int y =  state.random.nextInt(state.grid.getWidth());
//        int x =  state.random.nextInt(state.grid.getHeight());
//        epa = new EPA(state.random);
//        
//        this.location = new Int2D(x, y);
//    }
    
    public Player(Population state, EPA epa, int typeNumber) {
        this.pop = state;
        this.epa = epa;
        this.typeNumber = typeNumber;
        this.energy = pop.initialEnergy;
        this.location = location;
    }
    
    @Override
    public void step(SimState state) {
        
        // If we've not already played this round, find a partner and play.
        
        if( lastActionTime < pop.schedule.getSteps() ) {
            
            neighbours.clear();
            neighbours.addAll(pop.grid.getMooreNeighbors(location.x, location.y, 1, Grid2D.BOUNDED, false));
            this.lastAction = -1;

            // remove from consideration any neighbours that have already played
            this.neighbours.removeIf(
                    p -> p.getLastActionTime() >= pop.schedule.getSteps());

            if( neighbours.isEmpty() &&
                    pop.grid.getMooreNeighbors(location.x, location.y, 1, Grid2D.BOUNDED, false).size() < 8) {

                // if empty spaces exist nearby, move into one of them
                // get list of neighbouring locations (if possible?)
                Int2D newLocation = findEmptyNeighbourLocation();
                if( newLocation != null ) {
                    pop.grid.moveObject(this, location, newLocation);
                    location = newLocation;
                    this.lastAction = 2;    // only recored movement if it actually happened
                } else {
                    this.lastAction = -1;
                }
                this.lastPayoff = 0;
                this.lastPartner = null;
                updateLastActionTime();
        //            System.out.println(this + " -> Moving");

            } else if( !neighbours.isEmpty() ) {
                // find neighbour and play with them
                neighbour = neighbours.get(pop.random.nextInt(neighbours.size()));
                this.interactWith(neighbour);
                neighbour.interactWith(this);
        //            System.out.println(this + " -> Playing");
            }
            
        }
        
        // Deal with Cost of Living
        energy -= pop.costOfLiving;
        
        // Try to reproduce
        attemptReproduction();
        
        // Try (not) to die
        attemptDeath();
        
        
//        System.out.println(state.schedule.getSteps() + " - Payoff = " + lastPayoff + "l type = " + typeNumber);
        
    }
    
    /**
     * Should play the PD game with given partner and do any relevant updates,
     * such as tracking time of last action.
     * TODO: Consider updating this so it is only called once, depending on how
     * much work is being repeated after I add affective bits here.
     * @param partner
     */
    private void interactWith(Player partner) {
        int strategy = 0;
        if( EPA.getDeflection(this.epa, abandon, partner.getEPA()) > 
                EPA.getDeflection(this.epa, collab, partner.getEPA())) {
            strategy = 1;
        }
        int partnerStrategy = 0;
        if( EPA.getDeflection(partner.getEPA(), abandon, this.epa) > 
                EPA.getDeflection(partner.getEPA(), collab, this.epa)) {
            partnerStrategy = 1;
        }
        
//        System.out.println("My deflection for abandon is: " + EPA.getDeflection(this.epa, abandon, partner.getEPA()));
//        System.out.println("My deflection for collaborate is: " + EPA.getDeflection(this.epa, collab, partner.getEPA()));
//        System.out.println("Partner's deflection for abandon is: " + EPA.getDeflection(partner.getEPA(), abandon, partner.getEPA()));
//        System.out.println("Partner's deflection for collaborate is: " + EPA.getDeflection(partner.getEPA(), collab, partner.getEPA()));
//        System.out.println("----------------------------------------------\n");
        
        this.lastAction = strategy;
        this.lastPartner = partner;
        this.lastPayoff = pop.payoffs[strategy][partnerStrategy];
        this.energy += lastPayoff;
        if( this.energy > pop.maxEnergy ) this.energy = pop.maxEnergy;
        updateLastActionTime();
    }
    
    private Int2D findEmptyNeighbourLocation() {
        
        ArrayList<Int2D> emptyLocations = new ArrayList<>();
        
        // first, make sure an empty cell exists
        for( int i = -1; i <= 1; ++i ) {
            
            if( i + location.x < 0  || i + location.x >= pop.grid.getWidth())
                continue;
            
            for( int j = -1; j <= 1; ++j ) {
                if( i == 0 && j == 0 )
                    continue;
                if( j + location.y < 0 || j + location.y >= pop.grid.getHeight())
                    continue;
                
                if( pop.grid.getObjectsAtLocation(i+location.x, j+location.y) == null ||
                        pop.grid.getObjectsAtLocation(i+location.x, j+location.y).isEmpty() ) {
                    emptyLocations.add(new Int2D(i+location.x, j+location.y));
                }
                
            }
        }
        
        if( emptyLocations.isEmpty() ) {
//            System.out.println("Moving to empty location!!!");
//            System.out.println(this + " -> Neighbours are:" + pop.grid.getMooreNeighbors(location.x, location.y, 1, Grid2D.BOUNDED, false));
            return null;
        }
            
        
        return emptyLocations.get(pop.random.nextInt(emptyLocations.size()));
    }
    
    private void attemptReproduction() {
        if( energy > pop.reproductionThreshold ) {
            Int2D childLocation = findEmptyNeighbourLocation();
            if( childLocation == null )
                return;
            
            Player child = new Player(pop, this.epa, typeNumber);
            child.location = childLocation;
            child.energy = this.energy/2;
            this.energy /= 2;
            
            pop.addPlayer(child);
        }
    }
    
    private void attemptDeath() {
        if( energy < pop.deathThreshold ) {
            pop.removePlayer(this);
        }
    }

    public double getEnergy() {
        return energy;
    }

    public Int2D getLocation() {
        return location;
    }
    public void setLocation(Int2D location) {
        this.location = location;
    }
    
    public long getLastActionTime() {
        return lastActionTime;
    }
    
    public EPA getEPA() {
        return epa;
    }
    
    public void updateLastActionTime() {
        if( pop == null) return;
        
        lastActionTime = pop.schedule.getSteps();
    }
    
    public void setStoppable(Stoppable stop) {
        if( stop != null )
            stoppable = stop;
    }
    
    public void stop() {
        if( stoppable != null ) {
            stoppable.stop();
        }
    }
    
}
