package simblock.node.consensus;

import simblock.block.Block;
import simblock.node.Node;
import simblock.task.EtherTask;
import simblock.block.EtherBlock;
import simblock.block.ProofOfWorkBlock;


import static simblock.simulator.Main.random;

import java.math.BigInteger;

public class EtherProofOfWork extends AbstractConsensusAlgo{

	public EtherProofOfWork(Node selfNode) {
		super(selfNode);
	}
	
	@Override
	public EtherTask minting() {
		Node selfNode = this.getSelfNode();
		EtherBlock parent = (EtherBlock) selfNode.getBlock();
		BigInteger difficulty = parent.getNextDifficulty();
		int numberOfTx = parent.getNumberOfTx();
		double p = 1.0/difficulty.doubleValue();
		double u = random.nextDouble();
		long interval = (long) (Math.log(u) / Math.log(1.0-p) / selfNode.getMiningPower());
		return p <= Math.pow(2, -53) ? null
				: new EtherTask(selfNode, (long) (Math.log(u) / Math.log(1.0 - p) / selfNode.getMiningPower()),
						difficulty, parent.getGasLimit(), parent.getGasUsed(), numberOfTx);
	}

	@Override
	public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
		if (!(receivedBlock instanceof EtherBlock)) {
			return false;
		}
		EtherBlock recPoWBlock = (EtherBlock) receivedBlock;
		EtherBlock currPoWBlock = (EtherBlock) currentBlock;
		int receivedBlockHeight = receivedBlock.getHeight();
		EtherBlock receivedBlockParent = receivedBlockHeight == 0 ? null
				: (EtherBlock) receivedBlock.getBlockWithHeight(receivedBlockHeight - 1);

		// TODO - dangerous to split due to short circuit operators being used,
		// refactor?
		return (receivedBlockHeight == 0
				|| recPoWBlock.getDifficulty().compareTo(receivedBlockParent.getNextDifficulty()) >= 0)
				&& (currentBlock == null
						|| recPoWBlock.getTotalDifficulty().compareTo(currPoWBlock.getTotalDifficulty()) > 0);
	}

	@Override
	public EtherBlock genesisBlock() {
		// TODO Auto-generated method stub
		return EtherBlock.genesisBlock(this.getSelfNode());
	}

}
