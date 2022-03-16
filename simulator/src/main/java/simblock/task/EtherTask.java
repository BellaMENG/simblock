package simblock.task;

import simblock.node.Node;
import simblock.block.EtherBlock;
import simblock.transaction.EtherTransaction;

import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;

import static simblock.simulator.Timer.getCurrentTime;




public class EtherTask extends AbstractMintingTask{
	
	private int gasLimit;
	private int gasUsed;
	private final int numberOfTx;
	private final BigInteger difficulty;

	public EtherTask(Node minter, long interval, BigInteger difficulty, int gasLimit, int gasUsed, int numberOfTx) {
		super(minter, interval, numberOfTx);
		// TODO Auto-generated constructor stub
		this.difficulty = difficulty;
		this.gasLimit = gasLimit;
		this.gasUsed = gasUsed;
		this.numberOfTx = numberOfTx;
	}

	@Override
	public void run() {
		List<EtherTransaction> transactions = new ArrayList<EtherTransaction>();
		// TODO create transactions
		
		EtherBlock createdBlock = new EtherBlock((EtherBlock) this.getParent(), this.getMinter(), getCurrentTime(), this.numberOfTx, transactions,
				this.difficulty, this.gasLimit, this.gasUsed);
		this.getMinter().receiveBlock(createdBlock);
	}

}
