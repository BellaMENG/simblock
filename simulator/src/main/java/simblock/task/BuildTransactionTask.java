package simblock.task;

import simblock.transaction.Transaction;

import java.util.List;
import java.util.ArrayList;

public class BuildTransactionTask implements Task{

	private final long interval;
	private List<Transaction> transactions;
	private int numberOfTransactions;
	
	public BuildTransactionTask(long interval, int numberOfTransactions) {
		this.interval = interval;
		this.numberOfTransactions = numberOfTransactions;
	}
	
	@Override
	public long getInterval() {
		// execution duration of this task
		return this.interval;
	}
	
	public List<Transaction> getTransactions() {
		return this.transactions;
	}
	
	public int getNumberOfTransactions() {
		return this.numberOfTransactions;
	}
	
	@Override
	public void run() {
		// TODO: how to simulate the transactions in a block?
		// How to simulate the transaction value and transaction fee?
		this.transactions = new ArrayList<Transaction>();
		for (int i = 0; i < this.numberOfTransactions; ++i) {
			Transaction tx = new Transaction(1, 1);
			this.transactions.add(tx);
		}
	}

}
