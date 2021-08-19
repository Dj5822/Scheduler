import java.util.*;

/**
 * The method responsible for the creation and traversal of the
 * schedule tree, a tree of each possible schedule and partial schedule
 */
public class TreeSearch {

    private Graph graph;
    private int processorCount;

    TreeSearch(Graph graph, int processorCount){
        this.graph = graph;
        this.processorCount = processorCount;
    }

    /**
     * Implementation of the A* algorithm, traversing the tree and creating a
     * viable schedule from it
     * 
     * @return The node that is at the end of the created schedule
     */
    public TaskNode aStar() {
        PriorityQueue<TaskNode> openList = new PriorityQueue<TaskNode>(new NodeComparator<TaskNode>());
        for (Task startTask : graph.getStartTasks()) {
            TaskNode rootNode = new TaskNode(startTask, graph.getStartTasks());
            rootNode.setCost(rootNode.getSchedule().getCost());
            openList.add(rootNode);
        }
        while (!openList.isEmpty()) {
            TaskNode node = openList.poll();
            if (node.getSchedule().getScheduledTasks().size() == graph.getTasks().size()) {
                return node;
            }

            ArrayList<TaskNode> successorList = node.getSuccessors(processorCount);
            for (TaskNode childNode : successorList) {
                childNode.setCost(childNode.getSchedule().getCost());
            }
            openList.addAll(successorList);
        }
        return null;
    }

    /**
     * The comparator for ordering of nodes in the priority queue
     * Nodes with a lower cost will be prioritised, putting them at
     * the front of the priority queue
     */
    private class NodeComparator<N extends Node<?>> implements Comparator<N> {
        public int compare(N node1, N node2) {
            return node1.getCost() - (node2.getCost());
        }
    }

   /**
     * Memory-bounded variant of A*. Improvement over SMA*.
     * @param nodeLimit maximum number of nodes allowed in memory
     * @return a node that represents a complete schedule
     */
    public BoundedNode smastarplus(int nodeLimit) {
        // add start task nodes to open list
        DualOpenList openList = new DualOpenList(nodeLimit);
        for (Task task : graph.getStartTasks()) {
            BoundedNode rootNode = new BoundedNode(task, graph.getStartTasks());
            rootNode.setCost(rootNode.getSchedule().getCost());
            openList.add(rootNode);
        }

        while (!openList.isEmpty()) {
            // get best node in open list
            BoundedNode bestNode = openList.poll();

            // check if node is goal
            if (bestNode.getSchedule().getScheduledTasks().size() == graph.getTasks().size()) {
                return bestNode;
            // check if goal not found    
            } else if (bestNode.getCost() == Short.MAX_VALUE) {
                return null;
            }

            // get successor nodes
            ArrayList<BoundedNode> successorList = new ArrayList<BoundedNode>();
            if (bestNode.getSchedule().hasBeenExpanded()) {
                successorList = bestNode.getForgottenSuccessors();
            } else {
                successorList = bestNode.getSuccessors(processorCount);
                bestNode.getSchedule().setExpanded();
            }

            // set cost of successor nodes and add to open list
            for (BoundedNode successor : successorList) {
                Schedule successorSchedule = successor.getSchedule();
                if (bestNode.hasForgottenSuccessor(successor)) {
                    bestNode.removeForgottenSuccessor(successor);
                } else if (successorSchedule.getScheduledTasks().size() != graph.getTasks().size() &&
                 (successorSchedule.getSchedulableTasks().isEmpty() ||
                  successorSchedule.getScheduledTasks().size() >= nodeLimit-1)) {
                    successor.setCost((short) Short.MAX_VALUE);
                } else {
                    successor.setCost(successorSchedule.getCost());
                    if (bestNode.getCost() > successor.getCost()) {
                        successor.setCost(bestNode.getCost());
                    }
                }

                openList.add(successor);
            }
            
            // cull worst leaves
            while (openList.size() > nodeLimit) {
                openList.cull();
            }
        }
        return null;
    }

    private class DualOpenList {
        private MappedCullQueue cullList = null;
        private MappedPriorityQueue openList;

        public DualOpenList(int nodeLimit) {
            this.openList = new MappedPriorityQueue(nodeLimit + graph.getTasks().size() * processorCount, graph);
        }

        public BoundedNode poll() {
            BoundedNode bestNode = openList.pop();
            if (cullList != null && cullList.contains(bestNode)) {
                cullList.remove(bestNode);
            }
            return bestNode;
        }

        public void add(BoundedNode node) {
            openList.insert(node);
            if (cullList != null && node.isLeafNode()) {
                cullList.insert(node);
            }
        }

        public void cull() {

            if (cullList == null) {
                System.out.println("cull creation");
                cullList = new MappedCullQueue(openList.size(), graph);
                for (BoundedNode node : openList.getNodes()) {
                    if (node.isLeafNode()) {
                        cullList.insert(node);
                    }
                }   
            }

            BoundedNode worstNode = cullList.pop();
            // get second worst node if node is also best for safety
            if (worstNode == openList.peek()) {
                BoundedNode temp = worstNode;
                worstNode = cullList.pop();
                cullList.insert(temp);
            }

            openList.remove(worstNode);

            BoundedNode parent = worstNode.getParent();
            if (parent != null) {
                parent.addForgottenSuccessor(worstNode);
                if (!openList.contains(parent)) {
                    add(parent);
                }
            }
        }

        public boolean isEmpty() {
            return openList.size() < 1;
        }

        public int size() {
            return openList.size();
        }
    }

    public TaskNode idaStar() {
        // add start task nodes to open list
        TaskNode dummy = new TaskNode(graph.getStartTasks());
        short bound = dummy.getSchedule().getBackwardsCost();
        dummy.setCost(dummy.getSchedule().getCost());
        LinkedList<TaskNode> path = new LinkedList<TaskNode>();
        path.add(dummy);
        while (true) {
            Short t = dfsida(path, 0, bound);
            if (t == null) {
                return path.getLast();
            }
            if (t == Short.MAX_VALUE) {
                return null;
            }
            bound = t;
        }
    }

    private Short dfsida(LinkedList<TaskNode> path, int g, short bound) {
        TaskNode node = path.getLast();
        Short f = (short) (g + node.getSchedule().getBackwardsCost());
        if (f > bound) {
            return f;
        }

        if (node.getSchedule().getScheduledTasks().size() == graph.getTasks().size()) {
            return null;
        }
        Short min = Short.MAX_VALUE;
        for (TaskNode successor : node.getSuccessors(processorCount)) {
            successor.setCost(successor.getSchedule().getCost());
            path.add(successor);
            Short t = dfsida(path, g + (successor.getSchedule().getFinishTime() - node.getSchedule().getFinishTime()), bound);
            if (t == null) {
                return null;
            }
            if (t < min) {
                min = t;
            }
            path.removeLast();
        }
        return min;
    }

    public TaskNode branchAndBound() {
        // add start task nodes to open list
        TaskNode dummy = new TaskNode(graph.getStartTasks());
        short upperBound = Short.MAX_VALUE;
        dummy.setCost(dummy.getSchedule().getCost());
        TaskNode currentBest = dummy;
        LinkedList<TaskNode> path = new LinkedList<>();
        path.add(dummy);

        while (!path.isEmpty()) {
            TaskNode node = path.pop();
            if (node.getSchedule().getScheduledTasks().size() == graph.getTasks().size()) {
                if (node.getSchedule().getCost() < upperBound) {
                    currentBest = node;
                    upperBound = node.getSchedule().getCost();
                }
            } else {
                for (TaskNode childNode : node.getSuccessors(processorCount)) {
                    if (childNode.getSchedule().getCost() <= upperBound) {
                        path.addFirst(childNode);
                    }
                }
            }
        }
        return currentBest;
        }

}