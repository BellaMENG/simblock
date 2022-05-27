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

import static simblock.settings.SimulationConfiguration.NUM_OF_NODES;
import static simblock.simulator.Main.FILE_NAME;
import static simblock.simulator.Timer.getCurrentTime;
import static simblock.settings.SimulationConfiguration.OBSERVED_BLOCKS_SIZE;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import simblock.block.Block;
import simblock.node.Node;
import simblock.node.routing.Directory;


/**
 * The type Simulator is tasked with maintaining the list of simulated nodes and managing the
 * block interval. It observes and manages the arrival of new blocks at the simulation level.
 */
public class Simulator {

	/**
	 * A list of nodes that will be used in a simulation.
	 */
	private static final ArrayList<Node> simulatedNodes = new ArrayList<>();

	private static ArrayList<Directory> directories = new ArrayList<>();
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
			String fileName = FILE_NAME;
			OUT_TXT_FILE = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(OUT_FILE_URI.resolve(fileName)))));
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

	public static void shuffle() {
      Collections.shuffle(simulatedNodes);
	}

	public static ArrayList<Directory> getDirectories() { return directories; }

	public static void addToDirectory(Directory dir) {
		directories.add(dir);
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
	 * Add node to the list of simulated nodes and immediately try to add the new node as a
	 * neighbor to all simulated
	 * nodes.
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
	 * A list of observed block propagation times. The map key represents the id of the node that
	 * has seen the
	 * block, the value represents the difference between the current time and the block minting
	 * time, effectively
	 * recording the absolute time it took for a node to witness the block.
	 */
	private static final ArrayList<LinkedHashMap<Integer, Long>> observedPropagations =
			new ArrayList<>();

	/**
	 * Handle the arrival of a new block. For every observed block, propagation information is
	 * updated, and for a new
	 * block propagation information is created.
	 *
	 * @param block the block
	 * @param node  the node
	 */
	public static void arriveBlock(Block block, Node node) {
		// If block is already seen by any node
		if (observedBlocks.contains(block)) {
			// Get the propagation information for the current block
			LinkedHashMap<Integer, Long> propagation = observedPropagations.get(
					observedBlocks.indexOf(block)
			);
			// Update information for the new block
			propagation.put(node.getNodeID(), getCurrentTime() - block.getTime());
		} else {
			// If the block has not been seen by any node and there is no memory allocated
			if (observedBlocks.size() > OBSERVED_BLOCKS_SIZE) {
				// After the observed blocks limit is reached, log and remove old blocks by FIFO principle
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

	/**
	 * Print propagation information about the propagation of the provided block  in the format:
	 *
	 * <p><em>node_ID, propagation_time</em>
	 *
	 * <p><em>propagation_time</em>: The time from when the block of the block ID is generated to
	 * when the
	 * node of the <em>node_ID</em> is reached.
	 *
	 * @param block       the block
	 * @param propagation the propagation of the provided block as a list of {@link Node} IDs and
	 *                    propagation times
	 */
	public static void printPropagation(Block block, LinkedHashMap<Integer, Long> propagation) {
		// Print block and its height
		//TODO block does not have a toString method, what is printed here
		//System.out.println(block + ":" + block.getHeight());
		ArrayList<Long> propagations = new ArrayList<>();
		long average = 0;
		int count = 0;
		//float avg = 0;
		for (Map.Entry<Integer, Long> timeEntry : propagation.entrySet()) {
			//System.out.println(timeEntry.getKey() + "," + timeEntry.getValue());
			long prop = timeEntry.getValue();
			propagations.add(prop);
			//average += timeEntry.getValue();
			count += 1;
		}
		//avg = average / count;
		//System.out.println("count is " + count);
		//System.out.println(avg);
		// save the output to the file

		if (count == NUM_OF_NODES) {
			Collections.sort(propagations);
			double lower = propagations.get(propagations.size() / 2 - 1);
			double upper = propagations.get(propagations.size() / 2);
			OUT_TXT_FILE.println((lower + upper)/2.0);
		}
		//System.out.println();
	}

	/**
	 * Print propagation information about all blocks, internally relying on
	 * {@link Simulator#printPropagation(Block, LinkedHashMap)}.
	 */
	public static void printAllPropagation() {

		for (int i = 0; i < observedBlocks.size(); i++) {
			//printPropagation(observedBlocks.get(i), observedPropagations.get(i));
		}
	}
}
