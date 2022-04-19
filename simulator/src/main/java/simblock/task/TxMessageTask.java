package simblock.task;

import static simblock.simulator.Network.getLatency;

import simblock.node.Node;
import simblock.transaction.Transaction;

public class TxMessageTask extends AbstractMessageTask{
	
	/**
	 * The transaction that is sent.
	 */
	private final Transaction tx;
	
	/**
	 * The interval of sending this transaction.
	 */
	private final long interval;

	
	public TxMessageTask(Node from, Node to, Transaction tx, long delay) {
		super(from, to);
		this.tx = tx;
		this.interval = getLatency(this.getFrom().getRegion(), this.getTo().getRegion()) + delay;
		
	}
	
	@Override
	public long getInterval() {
		return this.interval;
	}
	
	@Override
	public void run() {
		this.getFrom().sendNextTxMessage();
		super.run();
	}
	
	public Transaction getTx() {
		return this.tx;
	}

}
