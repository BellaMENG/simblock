package simblock.node.routing;

import simblock.node.Node;

import java.util.ArrayList;
import java.util.Collections;

import static simblock.settings.SimulationConfiguration.*;
import static simblock.simulator.Simulator.getSimulatedNodes;

/**
 * The implementation of the {@link AbstractRoutingTable} representing the Bitcoin core routing
 * table.
 */
public class ShardedRoutingTable extends AbstractRoutingTable {

	/**
	 * List of neighbors within the same directory.
	 */
	private final ArrayList<Integer> committeeMembers = new ArrayList<>();

	/**
	 * The list of outbound connections.
	 */
	private final ArrayList<Node> outbound = new ArrayList<>();

	/**
	 * The list of inbound connections.
	 */
	private final ArrayList<Node> inbound = new ArrayList<>();

	/**
	 * Each committee has a leader randomly assigned.
	 */
	private final int committeeLeaderID;

	/**
	 * The id of the committee.
	 */
	private final int committeeID;

	/**
	 * Instantiates a new Abstract routing table.
	 *
	 * @param selfNode          the self node
	 */
	public ShardedRoutingTable(Node selfNode) {
		super(selfNode);
		int nodeID = selfNode.getNodeID();
		if (nodeID <= NUM_OF_DIRECTORY_NODES) {
			this.committeeID = -1;
			this.committeeLeaderID = 0;
		} else {
			// 100 nodes, 5 committee, committee size: 20
			// for node 7, committeeID = 0 = (7-5)/20 = 0, committeeLeaderID = 20*0 + 5 = 5
			// for node 27, committeeID = (27-5)/20 = 1, committeeLeaderID = 20*1+5 = 25
			int committeeSize = NUM_OF_NORMAL_NODES/NUM_OF_COMMITTEES;
			this.committeeID = (nodeID-1 - NUM_OF_DIRECTORY_NODES)/committeeSize;
			this.committeeLeaderID = this.committeeID * committeeSize + NUM_OF_DIRECTORY_NODES;
		}
	}

	@Override
	public void initTable() {
		//System.out.println("Node id is: " + this.getSelfNode().getNodeID() + ", and committeeID is " + committeeID);
		int committeeSize = NUM_OF_NORMAL_NODES/NUM_OF_COMMITTEES;
		//System.out.print("Node: " + this.getSelfNode().getNodeID() + "committeeID is " + committeeID + ", committeeMembers are: ");
		for (int i = committeeLeaderID; i < Math.min(committeeLeaderID + committeeSize, NUM_OF_NODES); ++i) {
			committeeMembers.add(i);
			//System.out.print(i + ", ");
		}
		//System.out.println();
		Collections.shuffle(committeeMembers);
		/*
		for (int i = 0; i < NUM_OF_DIRECTORY_NODES; ++i) {
			this.addNeighbor(getSimulatedNodes().get(i));
		}

		 */
		this.addNeighbor(getSimulatedNodes().get(this.committeeID));
		for (int member : committeeMembers) {
			if (this.outbound.size() < (this.getNumConnection() + 1)) {
				this.addNeighbor(getSimulatedNodes().get(member));
			} else {
				break;
			}
		}
		//System.out.println("NumConnection count is " + this.getNumConnection());
	}

	@Override
	public void initTable(int committeeID) {}

	@Override
	public ArrayList<Node> getNeighbors() {
		ArrayList<Node> neighbors = new ArrayList<>();
		neighbors.addAll(outbound);
		neighbors.addAll(inbound);
		return neighbors;
	}

	@Override
	public boolean addNeighbor(Node node) {
		if (node == getSelfNode() || this.outbound.contains(node) || this.inbound.contains(
				node) || this.outbound.size() >= this.getNumConnection()) {
			return false;
		} else if (this.outbound.add(node) && node.getRoutingTable().addInbound(getSelfNode())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeNeighbor(Node node) {
		if (this.outbound.remove(node) && node.getRoutingTable().removeInbound(getSelfNode())) {
			return true;
		}
		return false;
	}

	/**
	 * Adds the provided node as an inbound connection.
	 *
	 * @param from the node to be added as an inbound connection
	 * @return the success state of the operation
	 */
	public boolean addInbound(Node from) {
		if (this.inbound.add(from)) {
			return true;
		}
		return false;
	}

	/**
	 * Removes the provided node as an inbound connection.
	 *
	 * @param from the node to be removed as an inbound connection
	 * @return the success state of the operation
	 */
	public boolean removeInbound(Node from) {
		if (this.inbound.remove(from)) {
			return true;
		}
		return false;
	}

}
