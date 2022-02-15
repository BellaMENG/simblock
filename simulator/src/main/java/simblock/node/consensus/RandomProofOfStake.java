package simblock.node.consensus;

import static simblock.simulator.Main.random;

import java.math.BigInteger;

import simblock.block.Block;
import simblock.block.RandomPoSBlock;
import simblock.node.Node;
import simblock.task.RandomStakingTask;

public class RandomProofOfStake extends AbstractConsensusAlgo {

	public RandomProofOfStake(Node selfNode) {
		super(selfNode);
	}

	@Override
	public RandomStakingTask minting() {
		// TODO: implement random staking minting task
		Node selfNode = this.getSelfNode();
		RandomPoSBlock parent = (RandomPoSBlock) selfNode.getBlock();
		BigInteger difficulty = parent.getNextDifficulty();
		// TODO: should I determine number of transactions by this?
		int numberOfTx = parent.getNumberOfTx();
		double p = 1.0 / difficulty.doubleValue();
		double u = random.nextDouble();
		return p <= Math.pow(2, -53) ? null
				: new RandomStakingTask(selfNode, (long) (Math.log(u) / Math.log(1.0 - p) * 1000), difficulty, numberOfTx);
	}

	@Override
	public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
		if (!(receivedBlock instanceof RandomPoSBlock)) {
			return false;
		}
		RandomPoSBlock recPoSBlock = (RandomPoSBlock) receivedBlock;
		RandomPoSBlock currPoSBlock = (RandomPoSBlock) currentBlock;
		int receivedBlockHeight = receivedBlock.getHeight();
		RandomPoSBlock receivedBlockParent = receivedBlockHeight == 0 ? null
				: (RandomPoSBlock) receivedBlock.getBlockWithHeight(receivedBlockHeight - 1);

		boolean rec = (receivedBlockHeight == 0
				|| recPoSBlock.getDifficulty().compareTo(receivedBlockParent.getNextDifficulty()) >= 0);
		boolean curr = (currentBlock == null
				|| recPoSBlock.getTotalDifficulty().compareTo(currPoSBlock.getTotalDifficulty()) > 0);
		return rec && curr;
	}

	@Override
	public RandomPoSBlock genesisBlock() {
		return RandomPoSBlock.genesisBlock(this.getSelfNode());
	}
}
