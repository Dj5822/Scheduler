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
    public Node bruteForceTest() {
        ArrayList<Node> openNodeList = new ArrayList<>();

        for (Task task : graph.getStartTasks()) {
            State state = new State(task, 0, 0);
            openNodeList.add(new Node(null, state.getFinishTime(), state));
        }

        while (!openNodeList.isEmpty()) {
            ArrayList<Node> newOpenNodeList = new ArrayList<>();

            // Go through every node in the openNodeList.
            for (Node node: openNodeList) {
                // Figure out which nodes are schedulable.
                Map<Task, State> scheduled = new HashMap<>();
                ArrayList<Task> schedulable = new ArrayList<>();
                int[] processorFinishTimes = new int[processorCount];

                Node currentNode = node;

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
                    for (int i=0; i<processorCount; i++) {
                        /*
                         1) Task must be scheduled after the finish time of the processor
                         it is going to get assigned to.

                         2) Task must be scheduled after the latest finish time
                         of all the parents.

                         3) If the task is being assigned to a process that is different
                         to that of it's parent, you must add in the communication time.
                         */
                        int startTime = processorFinishTimes[i];

                        for (Task parentTask: task.getParents()) {
                            int parentFinishTime = scheduled.get(parentTask).getFinishTime();
                            if (scheduled.get(parentTask).getProcessor() != i) {
                                parentFinishTime += task.getParentCommunicationTime(parentTask);
                            }
                            if (parentFinishTime > startTime) {
                                startTime = parentFinishTime;
                            }
                        }

                        State newState = new State(task, startTime, i);
                        int newCost = Math.max(node.getCost(), newState.getFinishTime());

                        newOpenNodeList.add(new Node(node, newCost, newState));
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
        Node bestNode = openNodeList.get(0);
        for (Node node : openNodeList) {
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
    public Node aStar() {
        PriorityQueue<Node> openList = new PriorityQueue<>(new NodeComparator());
        for (Task startTask : graph.getStartTasks()) {
            Node rootNode = new Node(startTask);
            rootNode.setCost(getBackwardsCost(rootNode) + startTask.getWeight());
            openList.add(rootNode);
        }
        while (!openList.isEmpty()) {
            Node node = openList.poll();
            ScheduleData schedule = getScheduleData(node);
            if (schedule.scheduled.size() == graph.getTasks().size()) {
                return node;
            }

            openList.addAll(expandNode(node, schedule));
        }
        return null;
    }

    /**
     * Expands a node, adding children nodes to the priority queue
     * child nodes are every viable schedule that we can reach by adding one
     * additional task to the current schedule (the input node)
     * Number of child nodes of a node is 
     * (number of non-empty processors +1) * (number of schedulable tasks)
     * 
     * @param queue the priority queue of nodes
     * @param node the node to be expanded
     */
    private ArrayList<Node> expandNode(Node node, ScheduleData schedule) {
        int processorsInUse = 0;
        ArrayList<Node> nodeList = new ArrayList<Node>();
        for (int i = 0; i < schedule.processorFinishTimes.length; i++) {
            if (schedule.processorFinishTimes[i] > 0) {
                processorsInUse++;
            }
        }

        // attempt to minimise repeated branches by limiting duplicate empty processors
        if (processorsInUse < processorCount) {
            processorsInUse += 1;
        }

        // make a child node for every processor * schedulable task
        for (Task task : schedule.schedulable) {
            for (int processor = 0; processor < processorsInUse; processor++) {
                int startTime = schedule.processorFinishTimes[processor];
                
                // child should not be scheduled before parent finish time (+ communication time)
                for (Task parentTask : task.getParents()) {
                    State parentState = schedule.scheduled.get(parentTask);
                    int parentFinishTime = parentState.getFinishTime();
                    if (parentState.getProcessor() != processor) {
                        parentFinishTime += task.getParentCommunicationTime(parentTask);
                    }
                    if (startTime < parentFinishTime) {
                        startTime = parentFinishTime;
                    }
                }
                State state = new State(task, startTime, processor);
                Node newNode = new Node(node, state);

                // forward cost is finish time of new schedule
                int finishTime = state.getFinishTime();
                for (int i = 0; i < schedule.processorFinishTimes.length; i++ ) {
                    if (schedule.processorFinishTimes[i] > finishTime) {
                        finishTime = schedule.processorFinishTimes[i];
                    }
                }
                newNode.setCost(getBackwardsCost(newNode) + finishTime);
                nodeList.add(newNode);
            }
        }
        return nodeList;
    }

    /**
     * Calculates the backwards cost for the input node
     * The backwards cost is
     * Max (start time of scheduled tasks plus their bottom level)
     * Second example heuristic from lectures
     * @param node the node to appraise
     * @return the backwards cost of the node
     */
    private int getBackwardsCost(Node node) {
        int maxCost = 0;
        while (node != null) {
            int bottomLevel = node.getState().getTask().getBottomLevel();
            int startTime = node.getState().getStartTime();
            int pathCost = bottomLevel + startTime;

            if (pathCost > maxCost) {
                maxCost = pathCost;
            }
            node = node.getParent();
        }
        return maxCost;
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
    private ScheduleData getScheduleData(Node node) {

        // initialise collections
        Map<Task, State> scheduled = new HashMap<>();
        Set<Task> children = new HashSet<Task>();
        int[] processorFinishTimes = new int[processorCount];
        ArrayList<Task> schedulable = new ArrayList<>();

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
            int processor = state.getProcessor();
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

        ScheduleData scheduleData = new ScheduleData(schedulable, scheduled, processorFinishTimes);
        return scheduleData;
    }

    /**
     * An inner class that packages information about a schedule in a quickly accessible structure
     */
    private class ScheduleData {
        public ArrayList<Task> schedulable; // list of tasks able to be scheduled
        public Map<Task, State> scheduled; // map of scheduled tasks to their states
        public int[] processorFinishTimes; // the finish time of each processor, by index.

        public ScheduleData(ArrayList<Task> schedulable,Map<Task, State> scheduled, int[] processorFinishTimes){
            this.schedulable=schedulable;
            this.scheduled=scheduled;
            this.processorFinishTimes = processorFinishTimes;
        }
        public int getFinishTime() {
            int max = 0;
            for (int i : processorFinishTimes) {
                if (i > max) {
                    max = i;
                }
            }
            return max;
        }
    }

    /**
     * The comparator for ordering of nodes in the priority queue
     * Nodes with a lower cost will be prioritised, putting them at
     * the front of the priority queue
     */
    private class NodeComparator implements Comparator<Node> {
        public int compare(Node node1, Node node2) {
            return node1.getCost() - (node2.getCost());
        }
    }
    
    public Node idaStar() {
        // add start task nodes to open list
        Node dummy = new Node(graph.getDummyStart());
        int bound = getBackwardsCost(dummy);
        LinkedList<Node> path = new LinkedList<Node>();
        path.add(dummy);
        while (true) {
            Integer t = dfs(path, 0, bound);
            if (t == null) {
                return path.getLast();
            }
            if (t == Integer.MAX_VALUE) {
                return null;
            }
            bound = t;
        }
    }

    private Integer dfs(LinkedList<Node> path, int g, int bound) {
        Node node = path.getLast();
        Integer f = (g + getBackwardsCost(node));
        if (f > bound) {
            return f;
        }
        ScheduleData schedule = getScheduleData(node);
        if (schedule.scheduled.size() - 1 == graph.getTasks().size()) {
            return null;
        }
        Integer min = Integer.MAX_VALUE;
        for (Node successor : expandNode(node, schedule)) {
            path.add(successor);
            int successorCost = successor.getState().getFinishTime();
            if (schedule.getFinishTime() >= successorCost) {
                successorCost = schedule.getFinishTime();
            }
            Integer t = dfs(path, g - schedule.getFinishTime() + successorCost, bound);
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
