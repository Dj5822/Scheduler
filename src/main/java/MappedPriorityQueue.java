import java.util.HashMap;
import java.util.Set;

class MappedPriorityQueue {
    private BoundedNode[] heap;
    private int size;
    private int maxsize;
    private HashMap<BoundedNode,Integer> indexMap = new HashMap<BoundedNode,Integer>();

    private static final int FRONT = 0;

    public MappedPriorityQueue(int maxsize) {
        this.maxsize = maxsize;
        this.size = 0;

        heap = new BoundedNode[this.maxsize];
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

    private boolean isLeaf(int pos) {
        if (pos > ((size)/2) && pos <= size) {
            return true;
        } else {
            return false;
        }
    }

    private void swap(int fpos, int spos) {
        BoundedNode temp;
        temp = heap[fpos];
        heap[fpos] = heap[spos];
        heap[spos] = temp;

        if (heap[fpos] == null || heap[spos] == null) {
            System.out.println("oh no!");
        }

        indexMap.put(heap[fpos], fpos);
        indexMap.put(heap[spos], spos);
    }

    private void minHeapify(int pos) {
        if (!isLeaf(pos) && heap[leftChild(pos)] != null ) {
            if (costsLess(heap[leftChild(pos)], heap[pos])
             || costsLess(heap[rightChild(pos)], heap[pos])) {
                if (costsLess(heap[leftChild(pos)], heap[rightChild(pos)])) {
                    swap(pos, leftChild(pos));
                    minHeapify(leftChild(pos));
                } else {
                    swap(pos, rightChild(pos));
                    minHeapify(rightChild(pos));
                }
            }
        }
    }

    public void insert(BoundedNode node) {

        if (size >= maxsize) {
            return;
        }

        heap[size] = node;
        int current = size;
        indexMap.put(node, size);
        size++;

        while (costsLess(heap[current], heap[parent(current)])) {
            if (heap[current] == null || heap[parent(current)]== null)
            System.out.println("insert borked!");
            swap(current, parent(current));
            current = parent(current);
        }
    }

    protected boolean costsLess(BoundedNode task1, BoundedNode task2) {
        if (task2 == null) {
            return true;
        }
        return task1.getCost() < task2.getCost();
    }

    public BoundedNode pop() {
        BoundedNode popped = heap[FRONT];
        heap[FRONT] = heap[size--];
        indexMap.put(heap[FRONT], FRONT);
        indexMap.remove(popped);
        minHeapify(FRONT);

        return popped;
    }

    public BoundedNode peek() {
        return heap[FRONT];
    }

    public int size() {
        return this.size;
    }

    public void remove(BoundedNode node) {
        int index = indexMap.get(node);

        swap(index, size--);
        heap[size] = null;
        indexMap.remove(node);

        while (costsLess(heap[index], heap[parent(index)]) && index >= 1) {
            swap(index, parent(index));
            index = parent(index);
        }
        minHeapify(index);
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
    protected boolean costsLess(BoundedNode task1, BoundedNode task2) {
        return !super.costsLess(task1, task2);
    }
}