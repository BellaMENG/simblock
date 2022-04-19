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

package simblock.simulator;

import static simblock.simulator.Timer.getCurrentTime;

import static simblock.settings.SimulationConfiguration.OBSERVEDBLOCKSLIMIT;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import simblock.block.Block;
import simblock.node.Node;
import simblock.transaction.Transaction;
import simblock.block.ProofOfWorkBlock;

/**
 * The type Simulator is tasked with maintaining the list of simulated nodes and
 * managing the block interval. It observes and manages the arrival of new
 * blocks at the simulation level.
 */
public class Simulator {

	/**
	 * A list of nodes that will be used in a simulation.
	 */
	private static final ArrayList<Node> simulatedNodes = new ArrayList<>();

	//private static ArrayList<Transaction> mempool = new ArrayList<>();

	/**
	 * The target block interval in milliseconds.
	 */
	private static long targetInterval;

	public static URI CONF_FILE_URI;
	public static URI OUT_FILE_URI;
	static {
		try {
			CONF_FILE_URI = ClassLoader.getSystemResource("simulator.conf").toURI();
			OUT_FILE_URI = CONF_FILE_URI.resolve(new URI("../output/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	// TODO:
	// this printwriter out.txt stores the propagation time
	public static PrintWriter OUT_TXT_FILE;
	static {
		try {
			OUT_TXT_FILE = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(OUT_FILE_URI.resolve("./out.txt")))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static PrintWriter OUT_TXN_TXT_FILE;
	static {
		try {
			OUT_TXN_TXT_FILE = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(OUT_FILE_URI.resolve("./out_tx.txt")))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get simulated nodes list.
	 *
	 * @return the array list
	 */
	public static ArrayList<Node> getSimulatedNodes() {
		return simulatedNodes;
	}


	/**
	 * Get target block interval.
	 *
	 * @return the target block interval in milliseconds
	 */
	public static long getTargetInterval() {
		return targetInterval;
	}

	/**
	 * Sets the target block interval.
	 *
	 * @param interval - block interval in milliseconds
	 */
	public static void setTargetInterval(long interval) {
		targetInterval = interval;
	}

	/**
	 * Add node to the list of simulated nodes.
	 *
	 * @param node the node
	 */
	public static void addNode(Node node) {
		simulatedNodes.add(node);
	}

	/**
	 * Remove node from the list of simulated nodes.
	 *
	 * @param node the node
	 */
	@SuppressWarnings("unused")
	public static void removeNode(Node node) {
		simulatedNodes.remove(node);
	}

	/**
	 * Add node to the list of simulated nodes and immediately try to add the new
	 * node as a neighbor to all simulated nodes.
	 *
	 * @param node the node
	 */
	@SuppressWarnings("unused")
	public static void addNodeWithConnection(Node node) {
		node.joinNetwork();
		addNode(node);
		for (Node existingNode : simulatedNodes) {
			existingNode.addNeighbor(node);
		}
	}

	/**
	 * A list of observed {@link Block} instances.
	 */
	private static final ArrayList<Block> observedBlocks = new ArrayList<>();
	
	/**
	 * A list of observed {@link Transaction} instances.
	 */
	private static final ArrayList<Transaction> observedTxs = new ArrayList<>();
	

	/**
	 * A list of observed block propagation times. The map key represents the id of
	 * the node that has seen the block, the value represents the difference between
	 * the current time and the block minting time, effectively recording the
	 * absolute time it took for a node to witness the block.
	 */
	private static final ArrayList<LinkedHashMap<Integer, Long>> observedPropagations = new ArrayList<>();
	
	/**
	 * Similar to the block propagation map, a list of tx propagation pairs.
	 */
	private static final ArrayList<LinkedHashMap<Integer, Long>> observedTxPropagations = new ArrayList<>();

	/**
	 * Handle the arrival of a new block. For every observed block, propagation
	 * information is updated, and for a new block propagation information is
	 * created.
	 *
	 * @param block the block
	 * @param node  the node
	 */
	public static void arriveBlock(Block block, Node node) {
		// If block is already seen by any node
		if (observedBlocks.contains(block)) {
			// Get the propagation information for the current block
			LinkedHashMap<Integer, Long> propagation = observedPropagations.get(observedBlocks.indexOf(block));
			// Update information for the new block
			propagation.put(node.getNodeID(), getCurrentTime() - block.getTime());
		} else {
			// If the block has not been seen by any node and there is no memory allocated
			if (observedBlocks.size() > OBSERVEDBLOCKSLIMIT) {
				// After the observed blocks limit is reached, log and remove old blocks by FIFO
				// principle
				printPropagation(observedBlocks.get(0), observedPropagations.get(0));
				observedBlocks.remove(0);
				observedPropagations.remove(0);
			}
			// If the block has not been seen by any node and there is additional memory
			LinkedHashMap<Integer, Long> propagation = new LinkedHashMap<>();
			propagation.put(node.getNodeID(), getCurrentTime() - block.getTime());
			// Record the block as seen
			observedBlocks.add(block);
			// Record the propagation time
			observedPropagations.add(propagation);
		}
	}
	
	public static void arriveTx(Transaction tx, Node node) {
		if (observedTxs.contains(tx)) {
			LinkedHashMap<Integer, Long> propagation = observedTxPropagations.get(observedTxs.indexOf(tx));
			propagation.put(node.getNodeID(), getCurrentTime() - tx.getTime());
		} else {
			if (observedTxs.size() > 1000) {
				printTxPropagation(observedTxs.get(0), observedTxPropagations.get(0));
				observedTxs.remove(0);
				observedTxPropagations.remove(0);
			}
			LinkedHashMap<Integer, Long> propagation = new LinkedHashMap<>();
			propagation.put(node.getNodeID(), getCurrentTime() - tx.getTime());
			observedTxs.add(tx);
			observedTxPropagations.add(propagation);
		}
	}

	/**
	 * Print propagation information about the propagation of the provided block in
	 * the format:
	 *
	 * <p>
	 * <em>node_ID, propagation_time</em>
	 *
	 * <p>
	 * <em>propagation_time</em>: The time from when the block of the block ID is
	 * generated to when the node of the <em>node_ID</em> is reached.
	 *
	 * @param block       the block
	 * @param propagation the propagation of the provided block as a list of
	 *                    {@link Node} IDs and propagation times
	 */
	public static void printPropagation(Block block, LinkedHashMap<Integer, Long> propagation) {
		// Print block and its height
		// TODO block does not have a toString method, what is printed here
		System.out.println(block.getClass().getSimpleName() + ": " + block.getHeight());
		//System.out.println(block.getTime());
		System.out.println("Minter is " + block.getMinter().getNodeID());
		long average = 0;
		int count = 0;
		for (Map.Entry<Integer, Long> timeEntry : propagation.entrySet()) {
			// System.out.println(timeEntry.getKey() + "," + timeEntry.getValue());
			count += 1;
			average += timeEntry.getValue();
		}
		float avg = average/count;
		System.out.println(avg);
		OUT_TXT_FILE.println(avg);
		System.out.println();
	}

	
	public static void printTxPropagation(Transaction tx, LinkedHashMap<Integer, Long> propagation) {
		System.out.println(tx.getClass().getSimpleName() + ": " + tx.getId());
		long average = 0;
		int count = 0;
		for (Map.Entry<Integer, Long> timeEntry: propagation.entrySet()) {
			//System.out.println(timeEntry.getKey() + ", " + timeEntry.getValue());
			count += 1;
			average += timeEntry.getValue();
		}
		float avg = average/count;
		OUT_TXN_TXT_FILE.println(avg);
	}
	/**
	 * Print propagation information about all blocks, internally relying on
	 * {@link Simulator#printPropagation(Block, LinkedHashMap)}.
	 */
	public static void printAllPropagation() {
		for (int i = 0; i < observedBlocks.size(); i++) {
			printPropagation(observedBlocks.get(i), observedPropagations.get(i));
		}
	}
}
