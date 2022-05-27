package simblock.node.routing;

import simblock.node.Node;

import java.util.ArrayList;
import java.util.Collections;

import static simblock.settings.SimulationConfiguration.*;
import static simblock.simulator.Simulator.getDirectories;
import static simblock.simulator.Simulator.getSimulatedNodes;

/**
 * The implementation of the {@link AbstractRoutingTable} representing the Bitcoin core routing
 * table.
 */
public class DirectoryRoutingTable extends AbstractRoutingTable{
	/**
	 * The list of outbound connections.
	 */
	private final ArrayList<Node> outbound = new ArrayList<>();

	/**
	 * The list of inbound connections.
	 */
	private final ArrayList<Node> inbound = new ArrayList<>();


	private final ArrayList<Integer> committeeLeaders = new ArrayList<>();

	private final ArrayList<Integer> directoryMembers = new ArrayList<>();
	private final int committeeLeaderID;
	/**
	 * Instantiates a new Abstract routing table.
	 *  @param selfNode the self node
	 */
	public DirectoryRoutingTable(Node selfNode) {
		super(selfNode);
		int nodeID = selfNode.getNodeID();
		int committeeSize = NUM_OF_NORMAL_NODES/NUM_OF_COMMITTEES;
		this.committeeLeaderID = (nodeID-1) * committeeSize + NUM_OF_DIRECTORY_NODES;
	}

	@Override
	public void initTable() {
		/*
		ArrayList<Integer> dirMembers = new ArrayList<>();
		for (Directory dir: getDirectories()) {
			dirMembers.add(dir.getNodeID());
		}
		Collections.shuffle(dirMembers);
		for (int member: dirMembers) {
			if (this.outbound.size() < this.getNumConnection()) {
				this.addNeighbor(getSimulatedNodes().get(member));
			} else {
				break;
			}
		}

		 */
		ArrayList<Integer> dirMembers = new ArrayList<>();
		for (int i = 0; i < NUM_OF_DIRECTORY_NODES; ++i) {
			dirMembers.add(i);
		}
		Collections.shuffle(dirMembers);
		for (int member: dirMembers) {
			if (this.outbound.size() < (this.getNumConnection())) {
				this.addNeighbor(getSimulatedNodes().get(member));
			} else {
				break;
			}
		}
		//System.out.println("NumConnection count is " + this.getNumConnection());
		this.addNeighbor(getSimulatedNodes().get(this.committeeLeaderID));
	}

	@Override
	public void initTable(int committeeID) {

	}

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
