/*
 * Copyright 2019 Distributed Systems Group
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simblock.task;

import static simblock.simulator.Timer.getCurrentTime;

import java.math.BigInteger;
import simblock.block.ProofOfWorkBlock;
import simblock.node.Node;
import simblock.transaction.Transaction;

import java.util.List;
import java.util.ArrayList;

/**
 * The type Mining task.
 */
public class MiningTask extends AbstractMintingTask {
  private final BigInteger difficulty;
  private final int numberOfTx;

  /**
   * Instantiates a new Mining task.
   *
   * @param minter     the minter
   * @param interval   the interval
   * @param difficulty the difficulty
   */
  //TODO how is the difficulty expressed and used here?
  public MiningTask(Node minter, long interval, BigInteger difficulty, int numberOfTx) {
    super(minter, interval, numberOfTx);
    this.difficulty = difficulty;
    this.numberOfTx = numberOfTx;
  }

  @Override
  public void run() {
	// TODO: first, make all the transactions empty;
	// TODO: implement transactions mempool and grab transactions from mempool;
	List<Transaction> transactions = new ArrayList<Transaction>();
	
    ProofOfWorkBlock createdBlock = new ProofOfWorkBlock(
        (ProofOfWorkBlock) this.getParent(), this.getMinter(), getCurrentTime(), this.numberOfTx, transactions,
        this.difficulty
    );
    this.getMinter().receiveBlock(createdBlock);
  }
}
