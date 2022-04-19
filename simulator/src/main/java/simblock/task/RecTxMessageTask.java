package simblock.task;

import simblock.block.Block;
import simblock.node.Node;
import simblock.transaction.Transaction;

public class RecTxMessageTask extends AbstractMessageTask {
	/**
	 * The txn to be received
	 */
	private final Transaction tx;
	
	
	/**
	 * Initiate a task
	 * @param from Node
	 * @param to Node
	 * @param tx the transaction to be sent
	 */
	public RecTxMessageTask(Node from, Node to, Transaction tx) {
		super(from, to);
		this.tx = tx;
	}
	
	public Transaction getTx() {
		return this.tx;
	}
}
