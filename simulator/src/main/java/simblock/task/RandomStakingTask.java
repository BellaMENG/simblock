package simblock.task;

import simblock.node.Node;
import simblock.transaction.Transaction;
import simblock.block.RandomPoSBlock;

import static simblock.simulator.Timer.getCurrentTime;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;

public class RandomStakingTask extends AbstractMintingTask{
	private final BigInteger difficulty;
	private final int numberOfTx;

	public RandomStakingTask(Node minter, long interval, BigInteger difficulty, int numberOfTx) {
		super(minter, interval, numberOfTx);
		this.difficulty = difficulty;
		this.numberOfTx = numberOfTx;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		List<Transaction> transactions = new ArrayList<Transaction>();
		RandomPoSBlock createdBlock = new RandomPoSBlock((RandomPoSBlock) this.getParent(), this.getMinter(), getCurrentTime(), this.numberOfTx, transactions, this.difficulty);
		this.getMinter().receiveBlock(createdBlock);
	}

}
