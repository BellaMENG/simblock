package simblock.task;

import simblock.transaction.Transaction;
import simblock.node.Node;

public class InvTxMessageTask extends AbstractMessageTask {
	  /**
	   * Block to be advertised.
	   */
	  private final Transaction tx;

	  /**
	   * Instantiates a new Inv message task.
	   *
	   * @param from  the sender
	   * @param to    the receiver
	   * @param tx    the tx to be advertised
	   */
	  public InvTxMessageTask(Node from, Node to, Transaction tx) {
	    super(from, to);
	    this.tx = tx;
	  }

	  /**
	   * Gets block.
	   *
	   * @return the block
	   */
	  public Transaction getTx() {
	    return this.tx;
	  }

}
