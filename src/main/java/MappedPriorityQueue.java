import java.util.HashMap;

class MappedPriorityQueue {
    private TaskNode[] heap;
    private int size;
    private int maxsize;
    private HashMap<TaskNode,Integer> indexMap;

    private static final int FRONT = 0;

    public MappedPriorityQueue(int maxsize) {
        this.maxsize = maxsize;
        this.size = 0;

        heap = new TaskNode[this.maxsize+1];
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
        if (pos > ((size-1)/2) && pos <= size) {
            return true;
        } else {
            return false;
        }
    }

    private void swap(int fpos, int spos) {
        TaskNode temp;
        temp = heap[fpos];
        heap[fpos] = heap[spos];
        heap[spos] = temp;
        indexMap.put(heap[fpos], fpos);
        indexMap.put(heap[spos], spos);
    }

    private void minHeapify(int pos) {
        if (!isLeaf(pos)) {
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

    public void insert(TaskNode node) {
        if (size >= maxsize) {
            return;
        }

        heap[size] = node;
        int current = size;
        indexMap.put(node, size);
        size++;

        while (costsLess(heap[current], heap[parent(current)])) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    protected boolean costsLess(TaskNode task1, TaskNode task2) {
        return task1.getCost() < task2.getCost();
    }

    public TaskNode pop() {
        TaskNode popped = heap[FRONT];
        heap[FRONT] = heap[size--];
        indexMap.remove(popped);
        minHeapify(FRONT);

        return popped;
    }

    public TaskNode peek() {
        return heap[FRONT];
    }

    public int size() {
        return this.size;
    }

    public void remove(TaskNode node) {
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

    public boolean contains(TaskNode node) {
        return indexMap.containsKey(node);
    }
}

class MappedCullQueue extends MappedPriorityQueue {
    public MappedCullQueue(int maxsize) {
        super(maxsize);
    }
    
    @Override
    protected boolean costsLess(TaskNode task1, TaskNode task2) {
        return !super.costsLess(task1, task2);
    }
}