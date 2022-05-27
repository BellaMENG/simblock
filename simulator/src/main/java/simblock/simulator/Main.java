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


import static simblock.settings.SimulationConfiguration.*;
import static simblock.simulator.Network.getDegreeDistribution;
import static simblock.simulator.Network.getRegionDistribution;
import static simblock.simulator.Network.printRegion;
import static simblock.simulator.Simulator.*;
import static simblock.simulator.Timer.getCurrentTime;
import static simblock.simulator.Timer.getTask;
import static simblock.simulator.Timer.putTask;
import static simblock.simulator.Timer.putTaskAbsoluteTime;
import static simblock.simulator.Timer.runTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import simblock.block.Block;
import simblock.node.Node;
import simblock.node.routing.Directory;
import simblock.task.AbstractMessageTask;
import simblock.task.AbstractMintingTask;
import simblock.task.RecMessageTask;


/**
 * The type Main represents the entry point.
 */
public class Main {
    /**
     * The constant to be used as the simulation seed.
     */
    public static Random random = new Random(10);

    /**
     * The initial simulation time.
     */
    public static long simulationTime = 0;
    /**
     * Path to config file.
     */
    public static URI CONF_FILE_URI;
    /**
     * Output path.
     */
    public static URI OUT_FILE_URI;

    static {
        try {
            CONF_FILE_URI = ClassLoader.getSystemResource("simulator.conf").toURI();
            OUT_FILE_URI = CONF_FILE_URI.resolve(new URI("../output/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * The output writer.
     */
    //TODO use logger
    public static PrintWriter OUT_JSON_FILE;

    /**
     * The constant STATIC_JSON_FILE.
     */
    //TODO use logger
    public static PrintWriter STATIC_JSON_FILE;

    static {
        try {
            OUT_JSON_FILE = new PrintWriter(
                    new BufferedWriter(new FileWriter(new File(OUT_FILE_URI.resolve("./output.json")))));
            STATIC_JSON_FILE = new PrintWriter(
                    new BufferedWriter(new FileWriter(new File(OUT_FILE_URI.resolve("./static.json")))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String FILE_NAME;

    /**
     * The entry point.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        BLOXROUTE = 0;
        BANDWIDTH_IMPROVEMENT = 1;

        //RESEARCH_PARAM = 0.8f;
        //FILE_NAME = "./out_relay_" + RELAY_NETWORK_RATE + "_" + BANDWIDTH_IMPROVEMENT + "_" + ENCRYPTION_TIME + ".txt";
        FILE_NAME = "./out2015.txt";
        final long start = System.currentTimeMillis();
        setTargetInterval(INTERVAL);

        //start json format
        OUT_JSON_FILE.print("[");
        OUT_JSON_FILE.flush();

        // Log regions
        printRegion();

        // Setup network
        constructNetworkWithAllNodes(NUM_OF_NODES);

        // Initial block height, we stop at END_BLOCK_HEIGHT
        int currentBlockHeight = 1;


        // Iterate over tasks and handle
        while (getTask() != null) {
            if (getTask() instanceof AbstractMintingTask) {
                //taskCount += 1;
                // create random number of blocks to be circulated in the network
                // first, randomly choose some nodes


                AbstractMintingTask task = (AbstractMintingTask) getTask();
                if (task.getParent().getHeight() == currentBlockHeight) {
                    currentBlockHeight++;
                }
                if (currentBlockHeight > END_BLOCK_HEIGHT) {
                    break;
                }
                // Log every 100 blocks and at the second block
                // TODO use constants here
                if (currentBlockHeight % 100 == 0 || currentBlockHeight == 2) {
                    writeGraph(currentBlockHeight);
                }
            }
            // Execute task
            runTask();
        }
        // Print propagation information about all blocks
        printAllPropagation();
        OUT_TXT_FILE.flush();
        //TODO logger
        System.out.println();

        Set<Block> blocks = new HashSet<>();

        // Get the latest block from the first simulated node
        Block block = getSimulatedNodes().get(0).getBlock();

        int counter1 = 0;
        double oldInterval = 0;
        double newInterval = 0;
        double totalInterval = 0;
        double myInterval = 0;
        //Update the list of known blocks by adding the parents of the aforementioned block
        while (block.getParent() != null) {
            blocks.add(block);
            oldInterval = block.getTime();
            block = block.getParent();
            newInterval = block.getTime();
            myInterval = (oldInterval - newInterval) / 1000; //convert to second
            totalInterval = totalInterval + myInterval;
            counter1 = counter1 + 1;
        }

        Set<Block> orphans = new HashSet<>();
        int averageOrphansSize = 0;
        // Gather all known orphans
        for (Node node : getSimulatedNodes()) {
            orphans.addAll(node.getOrphans());
            averageOrphansSize += node.getOrphans().size();
        }
        averageOrphansSize = averageOrphansSize / getSimulatedNodes().size();

        // Record orphans to the list of all known blocks
        blocks.addAll(orphans);
        System.out.println("orphans size: " + orphans.size() + ", and blocks size: " + blocks.size());
        ArrayList<Block> blockList = new ArrayList<>(blocks);

        //Sort the blocks first by time, then by hash code
        blockList.sort((a, b) -> {
            int order = Long.signum(a.getTime() - b.getTime());
            if (order != 0) {
                return order;
            }
            order = System.identityHashCode(a) - System.identityHashCode(b);
            return order;
        });

        //Log all orphans
        // TODO move to method and use logger
        for (Block orphan : orphans) {
            System.out.println(orphan + ":" + orphan.getHeight());
        }
        System.out.println(averageOrphansSize);

    /*
    Log in format:
     ＜fork_information, block height, block ID＞
    fork_information: One of "OnChain" and "Orphan". "OnChain" denote block is on Main chain.
    "Orphan" denote block is an orphan block.
     */
        // TODO move to method and use logger
        try {
            FileWriter fw = new FileWriter(new File(OUT_FILE_URI.resolve("./blockList.txt")), false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            for (Block b : blockList) {
                if (!orphans.contains(b)) {
                    pw.println("OnChain : " + b.getHeight() + " : " + b);
                } else {
                    pw.println("Orphan : " + b.getHeight() + " : " + b);
                }
            }
            pw.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Average interval: " + totalInterval/counter1);
        OUT_JSON_FILE.print("{");
        OUT_JSON_FILE.print("\"kind\":\"simulation-end\",");
        OUT_JSON_FILE.print("\"content\":{");
        OUT_JSON_FILE.print("\"timestamp\":" + getCurrentTime());
        OUT_JSON_FILE.print("}");
        OUT_JSON_FILE.print("}");
        //end json format
        OUT_JSON_FILE.print("]");
        OUT_JSON_FILE.close();


        long end = System.currentTimeMillis();
        simulationTime += end - start;
        // Log simulation time in milliseconds
        System.out.println(simulationTime);

    }


    //TODO　以下の初期生成はシナリオを読み込むようにする予定
    //ノードを参加させるタスクを作る(ノードの参加と，リンクの貼り始めるタスクは分ける)
    //シナリオファイルで上の参加タスクをTimer入れていく．

    // TRANSLATED FROM ABOVE STATEMENT
    // The following initial generation will load the scenario
    // Create a task to join the node (separate the task of joining the node and the task of
    // starting to paste the link)
    // Add the above participating tasks with a timer in the scenario file.

    /**
     * Populate the list using the distribution.
     *
     * @param distribution the distribution
     * @param facum        whether the distribution is cumulative distribution
     * @return array list
     */
    //TODO explanation on facum etc.
    public static ArrayList<Integer> makeRandomListFollowDistribution(double[] distribution, boolean facum) {
        ArrayList<Integer> list = new ArrayList<>();
        int index = 0;

        if (facum) {
            for (; index < distribution.length; index++) {
                while (list.size() <= NUM_OF_NODES * distribution[index]) {
                    list.add(index);
                }
            }
            while (list.size() < NUM_OF_NODES) {
                list.add(index);
            }
        } else {
            double acumulative = 0.0;
            for (; index < distribution.length; index++) {
                acumulative += distribution[index];
                while (list.size() <= NUM_OF_NODES * acumulative) {
                    list.add(index);
                }
            }
            while (list.size() < NUM_OF_NODES) {
                list.add(index);
            }
        }

        Collections.shuffle(list, random);
        return list;
    }

    /**
     * Populate the list using the rate.
     *
     * @param rate the rate of true
     * @return array list
     */
    public static ArrayList<Boolean> makeRandomList(float rate) {
        ArrayList<Boolean> list = new ArrayList<Boolean>();
        for (int i = 0; i < NUM_OF_NODES; i++) {
            list.add(i < NUM_OF_NODES * rate);
        }
        Collections.shuffle(list, random);
        return list;
    }

    /**
     * Generates a random mining power expressed as Hash Rate, and is the number of mining (hash
     * calculation) executed per millisecond.
     *
     * @return the number of hash  calculations executed per millisecond.
     */
    public static int genMiningPower() {
        double r = random.nextGaussian();

        return Math.max((int) (r * STDEV_OF_MINING_POWER + AVERAGE_MINING_POWER), 1);
    }

    /**
     * Construct network with the provided number of nodes.
     *
     * @param numNodes the num nodes
     */
    public static void constructNetworkWithAllNodes(int numNodes) {

        // Random distribution of nodes per region
        double[] regionDistribution = getRegionDistribution();
        List<Integer> regionList = makeRandomListFollowDistribution(regionDistribution, false);

        // Random distribution of node degrees
        double[] degreeDistribution = getDegreeDistribution();
        List<Integer> degreeList = makeRandomListFollowDistribution(degreeDistribution, true);

        // List of nodes using compact block relay.
        List<Boolean> useCBRNodes = makeRandomList(CBR_USAGE_RATE);

        // List of churn nodes.
        List<Boolean> churnNodes = makeRandomList(CHURN_NODE_RATE);
        List<Boolean> utilizingRelayNetwork = makeRandomList(RELAY_NETWORK_RATE);
        List<Boolean> minerNodes = makeRandomList(MINER_NODE_RATE);
        List<Boolean> researchParam = makeRandomList(RESEARCH_PARAM);
        List<Boolean> bloXRoute = makeRandomList(BLOXROUTE);

        for (int id = 1; id <= numNodes; id++) {
            // Each node gets assigned a region, its degree, mining power, routing table and
            // consensus algorithm
            Node node = new Node(
                    id, degreeList.get(id - 1) + 1, regionList.get(id - 1), genMiningPower(), TABLE,
                    ALGO, useCBRNodes.get(id - 1), churnNodes.get(id - 1), utilizingRelayNetwork.get(id-1), minerNodes.get(id-1),
                    researchParam.get(id-1), bloXRoute.get(id-1)
            );
            // Add the node to the list of simulated nodes
            addNode(node);

            OUT_JSON_FILE.print("{");
            OUT_JSON_FILE.print("\"kind\":\"add-node\",");
            OUT_JSON_FILE.print("\"content\":{");
            OUT_JSON_FILE.print("\"timestamp\":0,");
            OUT_JSON_FILE.print("\"node-id\":" + id + ",");
            OUT_JSON_FILE.print("\"region-id\":" + regionList.get(id - 1));
            OUT_JSON_FILE.print("}");
            OUT_JSON_FILE.print("},");
            OUT_JSON_FILE.flush();

        }

        // Link newly generated nodes
        if (!SHARDED) {
            for (Node node : getSimulatedNodes()) {
                node.joinNetwork();
                /*
                System.out.print("neighbors of " + node.getNodeID() + " are: ");
                for (Node nbr: node.getNeighbors()) {
                    System.out.print(nbr.getNodeID() + ", ");
                }
                System.out.println();

                 */
            }
        } else {
            for (int i = 0; i < NUM_OF_NODES; ++i) {
                int shardID = (i * NUM_OF_COMMITTEES) / NUM_OF_NODES;
                Node node = getSimulatedNodes().get(i);
                node.joinNetwork(i);
            }
        }

        // Designates a random node (nodes in list are randomized) to mint the genesis block
        /*
        if (SHARDED) {
            for (int i = 0; i < NUM_OF_COMMITTEES; ++i) {
                int id = i * (NUM_OF_NODES / NUM_OF_COMMITTEES);
                getSimulatedNodes().get(id).genesisBlock();
            }
        } else {
            getSimulatedNodes().get(0).genesisBlock();
        }

         */

        if (USING_RELAY_NETWORK) {
            // first, set up directory nodes
            System.out.println("using relay network");
            ArrayList<Integer> committeeLeaders = new ArrayList<>();
            for (int i = 0; i < NUM_OF_COMMITTEES; ++i) {
                committeeLeaders.add(i*(NUM_OF_NORMAL_NODES/NUM_OF_COMMITTEES)+NUM_OF_DIRECTORY_NODES);
            }
            for (int i = 0; i < NUM_OF_DIRECTORY_NODES; ++i) {
                Directory newDir = new Directory(i, committeeLeaders.get(i));
                addToDirectory(newDir);
                // set the node isDirNode to be true
                getSimulatedNodes().get(i).setIsDirNode();
                //System.out.println(getSimulatedNodes().get(i).getNodeID() + " setIsDirNode()");
            }
            for (Directory dir: getDirectories()) {
                //System.out.println("Directory member id: " + dir.getNodeID() + ", corresponding committee id: " + dir.getCommitteeLeader());
            }
        }
        getSimulatedNodes().get(0).genesisBlock();
    }

    /**
     * Network information when block height is <em>blockHeight</em>, in format:
     *
     * <p><em>nodeID_1</em>, <em>nodeID_2</em>
     *
     * <p>meaning there is a connection from nodeID_1 to right nodeID_1.
     *
     * @param blockHeight the index of the graph and the current block height
     */
    //TODO use logger
    public static void writeGraph(int blockHeight) {
        try {
            FileWriter fw = new FileWriter(
                    new File(OUT_FILE_URI.resolve("./graph/" + blockHeight + ".txt")), false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            for (int index = 1; index <= getSimulatedNodes().size(); index++) {
                Node node = getSimulatedNodes().get(index - 1);
                for (int i = 0; i < node.getNeighbors().size(); i++) {
                    Node neighbor = node.getNeighbors().get(i);
                    pw.println(node.getNodeID() + " " + neighbor.getNodeID());
                }
            }
            pw.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
