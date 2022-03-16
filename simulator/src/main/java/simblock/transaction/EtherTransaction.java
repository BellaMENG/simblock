package simblock.transaction;


public class EtherTransaction extends Transaction {
	private int startGas;
	private int gasPrice;
	
	public EtherTransaction(int value, int startGas, int gasPrice) {
		
		super(startGas * gasPrice, value);
		this.startGas = startGas;
		this.gasPrice = gasPrice;
		
	}
	
	public int getStartGas() {
		return startGas;
	}

	public int getGasPrice() {
		return gasPrice;
	}
}