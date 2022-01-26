package simblock.transaction;

public class Transaction {
	private int transactionFee;
	private int value;
	
	public Transaction(int value, int transactionFee) {
		this.transactionFee = transactionFee;
	}
	
	public int getValue() {
		return value;
	}

	public int getTransactionFee() {
		return this.transactionFee;
	}
}
