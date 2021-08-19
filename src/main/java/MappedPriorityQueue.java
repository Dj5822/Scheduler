import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

class MappedPriorityQueue {
    private ArrayList<BoundedNode> heap;
    private int maxsize;
    private HashMap<BoundedNode,Integer> indexMap = new HashMap<BoundedNode,Integer>();

    public MappedPriorityQueue(int maxsize) {
        heap = new ArrayList<BoundedNode>(this.maxsize);
    }

    private int parent(int pos) {
        return (pos-1)/2 ;
    }

    private int leftChild(int pos) {
        return pos*2 + 1;
    }

    private int rightChild(int pos) {
        return pos*2 + 2;
    }

    private void swap(int i, int j) {
        BoundedNode temp = heap.get(i);
        heap.set(i, heap.get(j));
		heap.set(j, temp);

        indexMap.put(heap.get(i), i);
        indexMap.put(heap.get(j), j);
    }

    private void minHeapify(int i) {
        int left = leftChild(i);
		int right = rightChild(i);
        int smallest;

		if (left <= heap.size()-1 && costsLess(heap.get(left),heap.get(i))) {
            smallest = left;
        } else {
            smallest = i;
        }

		// Is there a right child and, if so, does the right child have an
		// element smaller than the smaller of node i and the left child?
		if (right <= heap.size()-1 && costsLess(heap.get(right),heap.get(smallest)))
			smallest = right;  // yes, so the right child is the smallest

		// If node i holds an element smaller than both the left and right
		// children, then the max-heap property already held, and we need do
		// nothing more.  Otherwise, we need to swap node i with the smaller
		// of the two children, and then recurse down the heap from the smaller child.
		if (smallest != i) {
			swap(i, smallest);
			minHeapify(smallest);
        }
    }

    public void insert(BoundedNode node) {

        heap.add(node);        // Put new value at end;
		int loc = heap.size()-1;  // and get its location
        indexMap.put(node, loc);

		siftup(loc);
    }

    public void siftup(int loc) {
        // Swap with parent until parent not larger
		while (loc > 0 && costsLess(heap.get(loc),heap.get(parent(loc)))) {
			swap(loc, parent(loc));
			loc = parent(loc);
		}
    }

    protected boolean costsLess(BoundedNode task1, BoundedNode task2) {
        if (task1.hasBeenExpanded() && !task2.hasBeenExpanded()) {
            return false;
        } else if (task2.hasBeenExpanded() && !task1.hasBeenExpanded()) {
            return true;
        }
        return task1.getCost() < task2.getCost();
    }

    public BoundedNode pop() {
        if (heap.size() <= 0)
			return null;
		else {
			BoundedNode minVal = heap.get(0);
			heap.set(0, heap.get(heap.size()-1));  // Move last to position 0
			heap.remove(heap.size()-1);
            indexMap.remove(minVal);
            if (size() > 0) {
                indexMap.put(heap.get(0),0);
            }
			minHeapify(0);
			return minVal;
		}
    }

    public BoundedNode peek() {
        return heap.get(0);
    }

    public int size() {
        return heap.size();
    }

    public void remove(BoundedNode node) {
        int index = indexMap.get(node);

        if (index == heap.size()-1) {
            heap.set(index, heap.get(heap.size()-1));
            heap.remove(heap.size()-1);
            indexMap.remove(node);
            return;
        }

        heap.set(index, heap.get(heap.size()-1));
        heap.remove(heap.size()-1);
        indexMap.remove(node);
        indexMap.put(heap.get(index),index);

        if (index > 0 && costsLess(heap.get(index),heap.get(parent(index)))) {
            siftup(index);
        } else if (index < heap.size()/2) {
            minHeapify(index);
        }
    }

    public boolean contains(BoundedNode node) {
        return indexMap.containsKey(node);
    }
    
    public Set<BoundedNode> getNodes() {
        return indexMap.keySet();
    }

}

class MappedCullQueue extends MappedPriorityQueue {
    public MappedCullQueue(int maxsize) {
        super(maxsize);
    }
    
    @Override
    protected boolean costsLess(BoundedNode i, BoundedNode j) {
        return i.getCost()/(Math.log(i.getSchedule().getScheduledTasks().size() + Math.exp(1))) < 
        j.getCost()/(Math.log(j.getSchedule().getScheduledTasks().size() + Math.exp(1)));
    }
}