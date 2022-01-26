package simblock.transaction;

import java.util.Queue;
import java.util.LinkedList;

public class Mempool {
	
	private Queue<Transaction> transactionPool;
	
	public Mempool(Transaction[] transactions) {
		transactionPool = new LinkedList<Transaction>();
		for (Transaction tx: transactions) {
			transactionPool.add(tx);
		}
	}
	
	public Transaction get() {
		Transaction tx = transactionPool.poll();
		return tx;
		// return the next transaction in the queue
	}
	
	public void put(Transaction tx) {
		// push a transaction to the queue
	}
	
	public int size() {
		return this.transactionPool.size();
	}
}
