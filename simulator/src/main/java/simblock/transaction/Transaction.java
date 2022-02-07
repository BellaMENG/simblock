package simblock.transaction;

public class Transaction {
	private int id;
	private int transactionFee;
	private int value;
	
	public Transaction(int id, int transactionFee, int value) {
		this.id = id;
		this.transactionFee = transactionFee;
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public int getTransactionFee() {
		return this.transactionFee;
	}
	
	public int getId() {
		return this.id;
	}
}
