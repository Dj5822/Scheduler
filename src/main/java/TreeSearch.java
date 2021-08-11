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

//    /**
//      * Memory-bounded variant of A*. Improvement over SMA*.
//      * @param nodeLimit maximum number of nodes allowed in memory
//      * @return a node that represents a complete schedule
//      */
//     public BoundedNode smastarplus(long nodeLimit) {
//         // add start task nodes to open list
//         DualOpenList openList = new DualOpenList(new NodeComparator<BoundedNode>());
//         for (Task task : graph.getStartTasks()) {
//             TaskVariant state = new TaskVariant(task);
//             BoundedNode rootNode = new BoundedNode(state);
//             rootNode.setCost((short) (state.getFinishTime() + rootNode.getBackwardsCost()));
//             openList.add(rootNode);
//         }

//         while (!openList.isEmpty()) {
//             // get best node in open list
//             BoundedNode bestNode = openList.poll();
//             Schedule schedule = getScheduleData(bestNode);

//             // check if node is goal
//             if (schedule.getScheduledTasks().size() == graph.getTasks().size()) {
//                 return bestNode;
//             // check if goal not found    
//             } else if (bestNode.getCost() == Short.MAX_VALUE) {
//                 return null;
//             }

//             // get successor nodes
//             ArrayList<BoundedNode> successorList = new ArrayList<BoundedNode>();
//             if (bestNode.hasBeenExpanded()) {
//                 successorList = bestNode.getForgottenSuccessors();
//             } else {
//                 successorList = bestNode.getSuccessors(schedule, processorCount);
//             }

//             // set cost of successor nodes and add to open list
//             for (BoundedNode successor : successorList) {
//                 Schedule successorSchedule = new Schedule(successor.getState().getTask(), successor.getState().getProcessor(), schedule);
//                 if (bestNode.hasForgottenSuccessor(successor)) {
//                     successor.setCost((short) bestNode.updateForgottenSuccessor(successor));
//                 } else if (successorSchedule.getScheduledTasks().size() != graph.getTasks().size() &&
//                  (successorSchedule.getSchedulableTasks().isEmpty() ||
//                   successorSchedule.getScheduledTasks().size() >= nodeLimit-1)) {
//                     successor.setCost((short) Short.MAX_VALUE);
//                 } else {
//                     successor.setCost((short) (successorSchedule.getBackwardsCost() + successorSchedule.getFinishTime()));
//                     if (bestNode.getCost() > successor.getCost()) {
//                         successor.setCost(bestNode.getCost());
//                     }
//                 }
//                 openList.add(successor);
//             }
            
//             // cull worst leaves
//             while (openList.size() > nodeLimit) {
//                 openList.cull();
//             }
//         }
//         return null;
//     }

//     private class DualOpenList extends PriorityQueue<BoundedNode>{
//         private PriorityQueue<BoundedNode> cullList = null;

//         public DualOpenList(NodeComparator<BoundedNode> nodeComparator) {
//             super(nodeComparator);
//         }

//         @Override
//         public BoundedNode poll() {
//             BoundedNode bestNode = super.poll();
//             if (cullList != null) {
//                 cullList.remove(bestNode);
//             }
//             return bestNode;
//         }

//         @Override
//         public boolean add(BoundedNode node) {
//             boolean success = super.add(node);
//             if (cullList != null) {
//                 success = success && cullList.add(node);
//             }
//             return success;
//         }

//         public void cull() {
//             if (cullList == null) {
//                 cullList = new PriorityQueue<BoundedNode>(new CullComparator());
//                 for (BoundedNode node : toArray(new BoundedNode[cullList.size()])) {
//                     cullList.add(node);
//                 }   
//             }

//             BoundedNode worstNode = cullList.poll();
//             // get second worst node if node is also best for safety
//             if (worstNode == super.peek()) {
//                 BoundedNode temp = worstNode;
//                 worstNode = cullList.poll();
//                 cullList.add(temp);
//             }
//             super.remove(worstNode);

//             BoundedNode parent = worstNode.getParent();
//             if (parent != null) {
//                 parent.addForgottenSuccessor(worstNode);
//                 add(parent);
//             }
//         }

//         /**
//          * SMA*+ inverse open list for efficient culling operations. 
//          */
//         private class CullComparator implements Comparator<BoundedNode> {
//             public int compare(BoundedNode node1, BoundedNode node2) {
//                 return - node1.getCost() + (node2.getCost());
//             }
//         }
//     }

    public TaskNode idaStar() {
        // add start task nodes to open list
        TaskNode dummy = new TaskNode(graph.getStartTasks());
        short bound = dummy.getSchedule().getBackwardsCost();
        dummy.setCost(dummy.getSchedule().getCost());
        LinkedList<TaskNode> path = new LinkedList<TaskNode>();
        path.add(dummy);
        while (true) {
            Short t = dfs(path, 0, bound);
            if (t == null) {
                return path.getLast();
            }
            if (t == Short.MAX_VALUE) {
                return null;
            }
            bound = t;
        }
    }

    private Short dfs(LinkedList<TaskNode> path, int g, short bound) {
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
            Short t = dfs(path, g + (successor.getSchedule().getFinishTime() - node.getSchedule().getFinishTime()), bound);
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

}