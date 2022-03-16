package simblock.task;

import simblock.node.Node;

import static simblock.simulator.Simulator.getSimulatedNodes;
import static simblock.settings.SimulationConfiguration.NUM_OF_NODES;

import java.util.ArrayList;
import java.util.Collections;

public class CommitteeFormationTask {
	
	// instead of actually solving a PoW solution, we assign a delay to this task
	private long formationDelay;
	    
    public void assignCommitteeID() {
    	ArrayList<Integer> IDs = new ArrayList<>(NUM_OF_NODES);
        
        // Fill the vector with the values
        // 1, 2, 3, ..., n
        for (int i = 0; i < NUM_OF_NODES; i++)
            IDs.add(i + 1);
        Collections.shuffle(IDs);
        
        int i = 0;
        for (Node node: getSimulatedNodes()) {
        	node.setCommitteeID(IDs.get(i));
        	i += 1;
        }
    }
}
