package simblock.transaction;

public class Transaction {
	private static int id = 0;
	private int transactionFee;
	private int value;
	private int startGas;
	private int gasPrice;
	private final long time;
	
	public Transaction(int transactionFee, int value, long time) {
		this.transactionFee = transactionFee;
		this.value = value;
		this.startGas = 0;
		this.gasPrice = 0;
		this.time = time;
		id += 1;
	}
	
	public Transaction(int transactionFee, int value, int startGas, int gasPrice, long time) {
		this.transactionFee = transactionFee;
		this.value = value;
		this.startGas = startGas;
		this.gasPrice = gasPrice;
		this.time = time;
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
	
	public long getTime() {
		return this.time;
	}
}
