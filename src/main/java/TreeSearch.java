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
     * For testing purposes.
     */
    public TaskNode bruteForceTest() {
        ArrayList<TaskNode> openNodeList = new ArrayList<>();

        for (Task task : graph.getStartTasks()) {
            State state = new State(task, (short) 0, (byte)0);
            openNodeList.add(new TaskNode(null, state.getFinishTime(), state));
        }

        while (!openNodeList.isEmpty()) {
            ArrayList<TaskNode> newOpenNodeList = new ArrayList<>();

            // Go through every node in the openNodeList.
            for (TaskNode node: openNodeList) {
                // Figure out which nodes are schedulable.
                Map<Task, State> scheduled = new HashMap<>();
                ArrayList<Task> schedulable = new ArrayList<>();
                short[] processorFinishTimes = new short[processorCount];

                TaskNode currentNode = node;

                // Get scheduled tasks.
                while (currentNode != null) {
                    scheduled.put(currentNode.getState().getTask(), currentNode.getState());
                    if (processorFinishTimes[currentNode.getState().getProcessor()] == 0) {
                        processorFinishTimes[currentNode.getState().getProcessor()] = currentNode.getState().getFinishTime();
                    }
                    currentNode = currentNode.getParent();
                }

                // Get schedulable tasks.
                for (Task task: scheduled.keySet()) {
                    for (Edge edge: task.getChildren()) {
                        if (checkParentsVisited(edge.getChild(), scheduled)) {
                            if (!scheduled.containsKey(edge.getChild())) {
                                schedulable.add(edge.getChild());
                            }
                        }
                    }
                }

                // Loop through schedulable tasks.
                for (Task task: schedulable) {
                    for (byte i=0; i<processorCount; i++) {
                        /*
                         1) Task must be scheduled after the finish time of the processor
                         it is going to get assigned to.

                         2) Task must be scheduled after the latest finish time
                         of all the parents.

                         3) If the task is being assigned to a process that is different
                         to that of it's parent, you must add in the communication time.
                         */
                        short startTime = processorFinishTimes[i];

                        for (Task parentTask: task.getParents()) {
                            short parentFinishTime = scheduled.get(parentTask).getFinishTime();
                            if (scheduled.get(parentTask).getProcessor() != i) {
                                parentFinishTime += task.getParentCommunicationTime(parentTask);
                            }
                            if (parentFinishTime > startTime) {
                                startTime = parentFinishTime;
                            }
                        }

                        State newState = new State(task, startTime, i);
                        short newCost = (short) Math.max(node.getCost(), newState.getFinishTime());

                        newOpenNodeList.add(new TaskNode(node, newCost, newState));
                    }
                }
            }
            if (newOpenNodeList.isEmpty()) {
                break;
            }
            else {
                openNodeList = newOpenNodeList;
            }
        }

        int bestTime = openNodeList.get(0).getCost();
        TaskNode bestNode = openNodeList.get(0);
        for (TaskNode node : openNodeList) {
               if (node.getCost() < bestTime) {
                   bestTime = node.getCost();
                   bestNode = node;
               }
        }
        System.out.println("Optimal length: " + bestTime);

        return bestNode;
    }

    // Parents visited constraint.
    private boolean checkParentsVisited(Task task, Map<Task, State> scheduled) {
        for (Task parent: task.getParents()) {
            if (!scheduled.containsKey(parent)) {
                return false;
            }
        }
        return true;
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
            TaskNode rootNode = new TaskNode(new State(startTask));
            rootNode.setCost((short) (rootNode.getBackwardsCost() + startTask.getWeight()));
            openList.add(rootNode);
        }
        while (!openList.isEmpty()) {
            TaskNode node = openList.poll();
            Schedule schedule = getScheduleData(node);
            if (schedule.getScheduledTasks().size() == graph.getTasks().size()) {
                return node;
            }

            node.expandNode(schedule, processorCount);
            node.setCost((short) (schedule.getBackwardsCost() + schedule.getFinishTime()));
        }
        return null;
    }


    /**
     * Move up schedule, creating map of scheduled tasks to states
     * and a set of children tasks
     * 
     * go through set of children, check if they exist in scheduled map
     * and if parents exist in scheduled
     * 
     * if all parents are scheduled and child isn't, add to schedulable list
     * 
     * @param node The node to get the lists of tasks from
     * 
     * @return A pair containing the list of all schedulable tasks and
     * a map of each scheduled task to its state
     */
    private Schedule getScheduleData(Node<?,?> node) {

        // initialise collections
        HashMap<Task, State> scheduled = new HashMap<>();
        Set<Task> children = new HashSet<Task>();
        short[] processorFinishTimes = new short[processorCount];
        ArrayList<Task> schedulable = new ArrayList<>();
        int backwardsCost = 0;

        // loop through all nodes in schedule
        while (node != null) {
            State state = node.getState();
            Task task = node.getState().getTask();
            // map scheduled tasks to states
            scheduled.put(task, state);

            // add child tasks to child set
            for (Edge childTaskEdge : task.getChildren()) {
                Task childTask = childTaskEdge.getChild();
                children.add(childTask);
            }

            // set processor finish time to the max finish time of index processor
            byte processor = state.getProcessor();
            if (state.getFinishTime() > processorFinishTimes[processor]) {
                processorFinishTimes[processor] = state.getFinishTime();
            }

            node = node.getParent();
        }

        // loop through set of children of scheduled tasks
        // if child is not scheduled and all parents are, child can be scheduled.
        for (Task child : children) {
            if (!scheduled.containsKey(child)) {
                boolean allParentsScheduled = true;
                for (Task parent : child.getParents()) {
                    if (!scheduled.containsKey(parent)) {
                        allParentsScheduled = false;
                        break;
                    }
                }

                if (allParentsScheduled) {
                    schedulable.add(child);
                }
            }
        }

        // add unscheduled start tasks to child set
        for (Task startTask : graph.getStartTasks()) {
            if (!scheduled.containsKey(startTask)) {
                schedulable.add(startTask);
            }
        }

        Schedule scheduleData = new Schedule(schedulable, scheduled, processorFinishTimes, backwardsCost);
        return scheduleData;
    }

    /**
     * The comparator for ordering of nodes in the priority queue
     * Nodes with a lower cost will be prioritised, putting them at
     * the front of the priority queue
     */
    private class NodeComparator<N extends Node<?,?>> implements Comparator<N> {
        public int compare(N node1, N node2) {
            return node1.getCost() - (node2.getCost());
        }
    }


    /**
     * Memory-bounded variant of A*. Improvement over SMA*.
     * @param nodeLimit maximum number of nodes allowed in memory
     * @return a node that represents a complete schedule
     */
    public BoundedNode smastarplus(long nodeLimit) {
        long nodeCount = 0;
        // add start task nodes to open list
        DualOpenList openList = new DualOpenList(new NodeComparator<BoundedNode>());
        for (Task task : graph.getStartTasks()) {
            State state = new State(task);
            openList.add(new BoundedNode(state));
            nodeCount++;
        }

        while (!openList.isEmpty()) {
            // get best node in open list
            BoundedNode bestNode = openList.poll();
            Schedule schedule = getScheduleData(bestNode);

            // check if node is goal
            if (schedule.getScheduledTasks().size() == graph.getTasks().size()) {
                return bestNode;
            // check if goal not found    
            } else if (bestNode.getCost() == Integer.MAX_VALUE) {
                return null;
            }

            // get successor nodes
            ArrayList<BoundedNode> successorList = new ArrayList<BoundedNode>();
            if (bestNode.hasBeenExpanded()) {
                successorList = bestNode.getForgottenSuccessors();
            } else {
                successorList = bestNode.getSuccessors(schedule, processorCount);
            }

            // set cost of successor nodes and add to open list
            for (BoundedNode successor : successorList) {
                Schedule successorSchedule = new Schedule(successor.getState().getTask(), successor.getState().getProcessor(), schedule);
                if (bestNode.hasForgottenSuccessor(successor)) {
                    successor.setCost((short) bestNode.updateForgottenSuccessor(successor));
                } else if (successorSchedule.getScheduledTasks().size() != graph.getTasks().size() &&
                 (successorSchedule.getSchedulableTasks().isEmpty() ||
                  successorSchedule.getScheduledTasks().size() >= nodeLimit-1)) {
                    successor.setCost((short) Integer.MAX_VALUE);
                } else {
                    successor.setCost((short) (successorSchedule.getBackwardsCost() + successorSchedule.getFinishTime()));
                    if (bestNode.getCost() > successor.getCost()) {
                        successor.setCost(bestNode.getCost());
                    }
                }
                openList.add(successor);
                nodeCount++;
            }
            
            // cull worst leaves
            while (nodeCount > nodeLimit) {
                openList.cull();
                nodeCount--;
            }
        }
        return null;
    }

    private class DualOpenList extends PriorityQueue<BoundedNode>{
        private PriorityQueue<BoundedNode> cullList = null;

        public DualOpenList(NodeComparator<BoundedNode> nodeComparator) {
            super(nodeComparator);
        }

        @Override
        public BoundedNode poll() {
            BoundedNode bestNode = super.poll();
            if (cullList != null) {
                cullList.remove(bestNode);
            }
            return bestNode;
        }

        @Override
        public boolean add(BoundedNode node) {
            boolean success = super.add(node);
            if (cullList != null) {
                success = success && cullList.add(node);
            }
            return success;
        }

        public void cull() {
            if (cullList == null) {
                cullList = new PriorityQueue<BoundedNode>(new CullComparator());
                for (BoundedNode node : toArray(new BoundedNode[cullList.size()])) {
                    cullList.add(node);
                }   
            }

            BoundedNode worstNode = cullList.poll();
            // get second worst node if node is also best for safety
            if (worstNode == super.peek()) {
                BoundedNode temp = worstNode;
                worstNode = cullList.poll();
                cullList.add(temp);
            }

            super.remove(worstNode);

            BoundedNode parent = worstNode.getParent();
            parent.addForgottenSuccessor(worstNode);
            if (!contains(parent)) {
                add(parent);
            }
        }

        /**
         * SMA*+ inverse open list for efficient culling operations. 
         */
        private class CullComparator implements Comparator<BoundedNode> {
            public int compare(BoundedNode node1, BoundedNode node2) {
                return - node1.getCost() + (node2.getCost());
            }
        }
    }
}