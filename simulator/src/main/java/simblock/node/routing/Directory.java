package simblock.node.routing;

import java.util.ArrayList;

public class Directory {
	private final int committeeLeader;
	private ArrayList<Integer> directoryMembers;
	private final int nodeID;

	public Directory(int nodeID, int committeeLeader) {
		this.nodeID = nodeID;
		this.committeeLeader = committeeLeader;
	}

	public int getNodeID() { return this.nodeID; }

	public int getCommitteeLeader() { return this.committeeLeader; }
}
