package simblock.transaction;

import simblock.node.Node;

public class Transaction {
	private Node sender;
	private Node receiver;
	private int transactionFee;
	private int value;
	
	public Transaction(Node sender, Node receiver, int value, int transactionFee) {
		this.sender = sender;
		this.receiver = receiver;
		this.transactionFee = transactionFee;
	}
	
	public int getValue() {
		return value;
	}

	public Node getSender() {
		return this.sender;
	}
	
	public Node getReceiver() {
		return this.receiver;
	}
	
	public int getTransactionFee() {
		return this.transactionFee;
	}
}
