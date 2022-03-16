package simblock.transaction;

public class Transaction {
	private static int id = 0;
	private int transactionFee;
	private int value;
	
	public Transaction(int transactionFee, int value) {
		this.transactionFee = transactionFee;
		this.value = value;
		id += 1;
	}
	
	public int getValue() {
		return value;
	}

	public int getTransactionFee() {
		return this.transactionFee;
	}
	
	public int getId() {
		return Transaction.id;
	}
}
