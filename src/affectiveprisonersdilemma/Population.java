package affectiveprisonersdilemma;

import ec.util.MersenneTwisterFast;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.display.Console;
import sim.engine.MakesSimState;
import sim.engine.SimState;
import static sim.engine.SimState.doLoop;
import sim.field.grid.DenseGrid2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Int2D;

/**
 *
 * @author Ben Armstrong
 */
public class Population extends SimState {
    
    /**
     * Seed used by random generator for Population. This is different than the
     * seed used by PopulationWithUI so simulations are only replicable when
     * using headless version.
     */
    public final long seed;
    
    /**
     * Seed used to generate EPA values only. If no value is given this will be
     * the same as the other seed.
     */
    public final long epaSeed;
    
    // Does using something in both a static and non-static method mean that I'm
    // a leet programmer now?
    private static String outputFileName = "out.csv";
    public String getOutputName() { return outputFileName; }
    
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
    
    int numOfEachType = 50;
    public int getNumOfEachType() { return numOfEachType; }
    public void setNumOfEachType(int num) { numOfEachType = num; }
    
    int numTypes = 9;
    public int getNumTypes() { return numTypes; }
    public void setNumTypes(int num) { numTypes = num; }
    
    private ArrayList<Player> players = new ArrayList<>();
    private EPA[] playerTypes;
    public EPA[] getPlayerTypes() { return playerTypes; }
    
    public DenseGrid2D grid;
    
    public PopulationWithUI gui;
    
    private DataGatherer dg;
    
    private MersenneTwisterFast epaRandom;
    
    /**
     * TODO: Make sure these values are alright.
     * Payoff matrix for PD game.
     * R S
     * T P
     * So defect = 0, cooperate = 1.
     */
    public int[][] payoffs = 
    {
        {0,5},
        {-1,3}
    };
    
    public Population(long seed) {
        super(seed);
        this.seed = seed;
        this.epaSeed = seed;
        grid = new DenseGrid2D(100, 100);
    }
    
    public Population(long seed, long epaSeed) {
        super(seed);
        this.seed = seed;
        this.epaSeed = epaSeed;
        grid = new DenseGrid2D(100, 100);
    }
    
    @Override
    public void finish() {
        super.finish();
        
        generateKeyFile();
        appendToKeyFile();
        
        generateDataFile(outputFileName);
        appendToDataFile(outputFileName);
        
        
        System.out.println("DONESKIS!");
    }
    
    
    @Override
    public void start() {
        
        super.start();
        
        grid.clear();
        players.clear();
        
        playerTypes = new EPA[numTypes];
        generatePlayers();
        
        
        // set positions of players
        // surprisingly, this seems to finish quickly enough to be entirely reasonable
        int i = 0;
        while( i < numTypes*numOfEachType ) {
            int x = random.nextInt(grid.getHeight());
            int y = random.nextInt(grid.getWidth());
            
            if( grid.getObjectsAtLocation(x, y) == null ) {
                players.get(i).setLocation(new Int2D(x, y));
                grid.addObjectToLocation(players.get(i), players.get(i).getLocation());
                ++i;
            }
        }
        
        // create data gatherer
        dg = new DataGatherer(this);
        schedule.scheduleRepeating(dg, Integer.MAX_VALUE, 1.0);
        
    }
    
    private void generatePlayers() {
        
        // With the GUI this should now give random values between runs. Since 
        // the console will only run one trial at a time this will give the same
        // values for the same epa seed
        // TODO: Test and make sure it's working
        if(epaRandom == null) {
            epaRandom = new MersenneTwisterFast(epaSeed);
        }
//        epaRandom = new MersenneTwisterFast(epaSeed);
        // need to make sure generator is primed
        for(int i = 0; i < 200; ++i) {
            double nextDouble = epaRandom.nextDouble();
        }
        
//        for (int i = 0; i < numTypes; ++i ) {
//            EPA type = new EPA(epaRandom);

        EPA[] epas = {new EPA(0.45, -2.13, -4.03), new EPA(-3.22, 2.93, 2.91),
            new EPA(-2.82, 3.26, 3.27), new EPA(-2.3, 3.98, -2.74),
            new EPA(2.72, 3.50, 0.36), new EPA(4.08, 3.00, 2.39),
            new EPA(0.32, 3.99, -3.82), new EPA(0.52, 2.99, 0.19),
            new EPA(3.12, 3.57, -2.32) };
        
        for (int i = 0; i < 9; ++i ) {
//            EPA type = new EPA(epaRandom);

            EPA type = epas[i];

            playerTypes[i] = type;
            for( int j = 0; j < numOfEachType; ++j ) {
                Player p = new Player(this, type, i);
                players.add(p);
                p.setStoppable(schedule.scheduleRepeating(p));
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
        
        players.add(p);
        
        p.setStoppable(schedule.scheduleRepeating(p));
        grid.addObjectToLocation(p, p.getLocation());
        
        if( gui != null ) {
            gui.addPlayer(p);
        }
    }
    
    public void removePlayer(Player p) {
        
        p.stop();
        grid.removeObjectAtLocation(p, p.getLocation());
        players.remove(p);
    }
    
    public static void main(String[] args)
    {
        
        long tmpSeed = -1;
        for(int x=0;x<args.length-1;x++)
        {
            if (args[x].equalsIgnoreCase("-EPA"))
                tmpSeed = Long.parseLong(args[x+1]);
            if (args[x].equalsIgnoreCase("-output"))
                outputFileName = args[x+1];
        }
            
        final long epaSeed = tmpSeed;
        
        doLoop(new MakesSimState()
            {
            public SimState newInstance(long seed, String[] args)
                {
                try
                    {
                        if(epaSeed == -1)
                            return new Population(seed, seed);
                        else
                            return new Population(seed, epaSeed);
                    }
                catch (Exception e)
                    {
                    throw new RuntimeException("Exception occurred while trying to construct the simulation " + Population.class + "\n" + e);
                    }
                }
            public Class simulationClass() { return Population.class; }
            }, args);
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////         THIS SECTION IS TO DO WITH DATA OUTPUT      ////////////
    ////////////////////////////////////////////////////////////////////////////
    
    
    
    /**
     * If it does not already exist, generates a key file and the header for the
     * file. Make sure the stuff in the header is updated when any metrics are
     * changed.
     */
    private void generateKeyFile() {
        
        if(new File("key_"+this.getNumTypes()+"_types.csv").exists() ) {
            return;
        }
        
        StringBuilder builder = new StringBuilder();
        
        // need to set up file header
        builder.append("seed");
        builder.append(",epa_seed");
        for(int i = 0; i < this.getNumTypes(); ++ i) {
            builder.append(",epa_"+i);
        }
        builder.append("\n");

        try {
            PrintWriter kw = new PrintWriter(new FileOutputStream( new File("key_"+this.getNumTypes()+"_types.csv"), true ));
            kw.write(builder.toString());
            kw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataGatherer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Appends a single row to the key file. You had better have called 
     * generateKeyFile() before calling this.
     */
    private void appendToKeyFile() {
        
        PrintWriter kw = null;
        StringBuilder builder = new StringBuilder();
        
        try {
            kw = new PrintWriter(new FileOutputStream( new File("key_"+this.getNumTypes()+"_types.csv"), true ));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataGatherer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // append data for this trial to output file
        builder.append(this.seed);
        builder.append(","+this.epaSeed);
        for(int i = 0; i < this.getNumTypes(); ++ i) {
            builder.append(","+this.getPlayerTypes()[i]);
        }
        
        builder.append("\n");
        kw.write(builder.toString());
        kw.flush();
    }
    
    
    private void generateDataFile(String filename) {
        
        if(new File(filename).exists() ) {
            return;
        }
        
        PrintWriter pw = null;
        StringBuilder sb = new StringBuilder();
        
        
        try {
            pw = new PrintWriter(new FileOutputStream( new File(filename), true ));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataGatherer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sb = new StringBuilder();
        sb.append("seed");
        sb.append(",epa_seed");
        for(int i = 0; i < dg.utilities.length; ++i) {
            sb.append(",utility_"+i);
        }
        for(int i = 0; i < dg.populations.length; ++i) {
            sb.append(",pop_"+i);
        }
        
        for(int i = 0; i < this.getNumTypes(); ++i ) {
            for(int j = 0; j < this.getNumTypes(); ++j ) {
                sb.append(",defects_"+i+"_vs_"+j);
                sb.append(",coops_"+i+"_vs_"+j);
            }
        }
        
        sb.append(",moves");
        
        sb.append("\n");
        pw.write(sb.toString());
        pw.flush();
    }
    
    private void appendToDataFile(String filename) {
        
        PrintWriter pw = null;
        StringBuilder sb = new StringBuilder();
        
        try {
            pw = new PrintWriter(new FileOutputStream( new File(filename), true ));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataGatherer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        sb.append(this.seed);
        sb.append(","+epaSeed);
        
        for(int i = 0; i < dg.utilities.length; ++i) {
            sb.append(","+dg.utilities[i]);
        }
        for(int i = 0; i < dg.populations.length; ++i) {
            sb.append(","+dg.populations[i]);
        }
        
        for(int i = 0; i < this.getNumTypes(); ++i ) {
            for(int j = 0; j < this.getNumTypes(); ++j ) {
                
                sb.append(","+dg.defections[i][j]);
                sb.append(","+dg.cooperations[i][j]);
                
            }
        }
        sb.append(","+dg.movements);
        
        
        sb.append("\n");
        pw.write(sb.toString());
        pw.flush();
    }
    
}