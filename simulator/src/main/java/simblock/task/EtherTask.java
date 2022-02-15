package simblock.task;

import simblock.node.Node;
import simblock.block.EtherBlock;
import simblock.transaction.EtherTransaction;

import java.util.List;

import static simblock.simulator.Timer.getCurrentTime;

import java.util.ArrayList;


public class EtherTask extends AbstractMintingTask{
	
	private int gasLimit;
	private int gasUsed;
	private final int numberOfTx;

	public EtherTask(Node minter, long interval, int gasLimit, int gasUsed, int numberOfTx) {
		super(minter, interval, numberOfTx);
		// TODO Auto-generated constructor stub
		this.gasLimit = gasLimit;
		this.gasUsed = gasUsed;
		this.numberOfTx = numberOfTx;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		List<EtherTransaction> transactions = new ArrayList<EtherTransaction>();
		EtherBlock createdBlock = new EtherBlock((EtherBlock) this.getParent(), this.getMinter(), getCurrentTime(), this.numberOfTx, transactions,
				this.gasLimit, this.gasUsed);
		this.getMinter().receiveBlock(createdBlock);
	}

}
