package simblock.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simblock.node.Node;
import simblock.transaction.EtherTransaction;

public class EtherBlock extends Block {
	
	private int gasLimit;
	private int gasUsed;
	private List<EtherTransaction> transactions;
	
	public EtherBlock(EtherBlock parent, Node minter, long time, int numberOfTx, List<EtherTransaction> transactions, int gasLimit, int gasUsed) {
		super(parent, minter, time, numberOfTx);
		// TODO Auto-generated constructor stub
		this.gasLimit = gasLimit;
		this.gasUsed = gasUsed;
		this.transactions = new ArrayList<EtherTransaction>();
	    if (transactions == null) {
	    	this.transactions = null;
	    }
	    else {
	    	Collections.copy(this.transactions, transactions);
	    }
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