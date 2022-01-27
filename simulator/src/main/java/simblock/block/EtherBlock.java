package simblock.block;

import java.util.ArrayList;

//import simblock.block.Block;
import simblock.node.Node;
import simblock.transaction.Transaction;

public class EtherBlock extends Block {
	
	private int gasLimit;
	private int gasUsed;
	
	
	public EtherBlock(Block parent, Node minter, long time, ArrayList<Transaction> transactions, int gasLimit, int gasUsed) {
		super(parent, minter, time, transactions);
		// TODO Auto-generated constructor stub
		this.gasLimit = gasLimit;
		this.gasUsed = gasUsed;
	}


	public int getGasLimit() {
		return gasLimit;
	}


	public void setGasLimit(int gasLimit) {
		this.gasLimit = gasLimit;
	}


	public int getGasUsed() {
		return gasUsed;
	}


	public void setGasUsed(int gasUsed) {
		this.gasUsed = gasUsed;
	}
	
	
}