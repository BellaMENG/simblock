package simblock.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.math.BigInteger;

import simblock.node.Node;
import simblock.transaction.EtherTransaction;

import static simblock.simulator.Simulator.getSimulatedNodes;
import static simblock.simulator.Simulator.getTargetInterval;

public class EtherBlock extends Block {
	
	private int gasLimit;
	private int gasUsed;
	
	private final BigInteger difficulty;
	private final BigInteger totalDifficulty;
	private final BigInteger nextDifficulty;
	private static BigInteger genesisNextDifficulty;
	
	private List<EtherTransaction> transactions;
	
	public EtherBlock(EtherBlock parent, Node minter, long time, int numberOfTx, List<EtherTransaction> transactions, BigInteger difficulty, int gasLimit, int gasUsed) {
		super(parent, minter, time, numberOfTx);
		// TODO Auto-generated constructor stub
		this.gasLimit = gasLimit;
		this.gasUsed = gasUsed;
		this.difficulty = difficulty;
		
		this.transactions = new ArrayList<EtherTransaction>();
	    if (transactions == null) {
	    	this.transactions = null;
	    }
	    else {
	    	Collections.copy(this.transactions, transactions);
	    }
	    
	    if (parent == null) {
	    	this.totalDifficulty = BigInteger.ZERO.add(difficulty);
	    	this.nextDifficulty = EtherBlock.genesisNextDifficulty;
	    } else {
	    	this.totalDifficulty = parent.totalDifficulty.add(difficulty);
	    	this.nextDifficulty = parent.getNextDifficulty();
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
	
	public BigInteger getDifficulty() {
		return this.difficulty;
	}
	
	public BigInteger getTotalDifficulty() {
		return this.totalDifficulty;
	}
	
	public BigInteger getNextDifficulty() {
		return this.nextDifficulty;
	}
	
	public static EtherBlock genesisBlock(Node minter) {
		long totalMiningPower = 0;
		for (Node node: getSimulatedNodes()) {
			totalMiningPower += node.getMiningPower();
		}
		genesisNextDifficulty = BigInteger.valueOf(totalMiningPower * getTargetInterval());
		return new EtherBlock(null, minter, 0, 0, null, BigInteger.ZERO, 0, 0);
	}
	
}