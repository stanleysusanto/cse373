package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 1;
    List<PriorityNode<T>> items;
    HashMap<T, Integer> itemsMap;

    private int size;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        itemsMap = new HashMap<>();
        size = 0;
        items.add(null);
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.
    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> temp = items.get(a);
        itemsMap.replace(items.get(b).getItem(), a);
        itemsMap.replace(items.get(a).getItem(), b);
        items.set(a, items.get(b));
        items.set(b, temp);
    }

    private void percolateUp(int valueIndex) {
        int parentIndex = (valueIndex == 1) ? valueIndex : valueIndex / 2;
        while (items.size() > 1 && items.get(parentIndex).getPriority() > items.get(valueIndex).getPriority()) {
            swap(valueIndex, parentIndex);
            valueIndex = parentIndex;
            parentIndex = (valueIndex == 1) ? valueIndex : valueIndex / 2;
            // itemsMap.replace(items.get(valueIndex).getItem(), parentIndex);
            // itemsMap.replace(items.get(parentIndex).getItem(), valueIndex);
        }
    }

    private void percolateDown(int valueIndex) {
        int leftChildIndex = 2 * valueIndex;
        int rightChildIndex = 2 * valueIndex + 1;
        int swapIndex = -1;
        if (items.size() > rightChildIndex && items.size() > leftChildIndex) {
            PriorityNode<T> left = items.get(leftChildIndex);
            PriorityNode<T> right = items.get(rightChildIndex);
            swapIndex = left.getPriority() < right.getPriority() ? leftChildIndex : rightChildIndex;
        } else if (items.size() > rightChildIndex) {
            swapIndex = rightChildIndex;
        } else if (items.size() > leftChildIndex) {
            swapIndex = leftChildIndex;
        }

        while (swapIndex != -1 && items.get(swapIndex).getPriority() < items.get(valueIndex).getPriority()) {
            swap(valueIndex, swapIndex);
            valueIndex = swapIndex;
            leftChildIndex = 2 * valueIndex;
            rightChildIndex = 2 * valueIndex + 1;
            // itemsMap.replace(items.get(valueIndex).getItem(), swapIndex);
            // itemsMap.replace(items.get(swapIndex).getItem(), valueIndex);
            swapIndex = -1;
            if (items.size() > rightChildIndex && items.size() > leftChildIndex) {
                PriorityNode<T> left = items.get(leftChildIndex);
                PriorityNode<T> right = items.get(rightChildIndex);
                swapIndex = left.getPriority() < right.getPriority() ? leftChildIndex : rightChildIndex;
            } else if (items.size() > rightChildIndex) {
                swapIndex = rightChildIndex;
            } else if (items.size() > leftChildIndex) {
                swapIndex = leftChildIndex;
            }
        }
    }

    @Override
    public void add(T item, double priority) {
        if (!contains(item)) {
            PriorityNode<T> value = new PriorityNode<>(item, priority);
            itemsMap.put(item, size + 1);
            items.add(value); //enter value to open space
            percolateUp(size + 1);
            size++;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean contains(T item) {
        return itemsMap.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (items.size() > 1) {
            return items.get(1).getItem();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T removeMin() {
        if (items.size() > 1) {
            // items.add(items.get(1)); //add to the last entry
            // items.set(1, items.get(size)); //swap the previous last entry to become the parent
            swap(1, size);
            T removed = items.remove(size).getItem(); //delete last entry of tree
            itemsMap.remove(removed);
            percolateDown(1);
            size--;
            return removed;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void changePriority(T item, double priority) {
        if (itemsMap.containsKey(item)) {
            int index = itemsMap.get(item);
            if (priority > items.get(index).getPriority()) {
                items.get(index).setPriority(priority);
                percolateDown(index);
            } else if (priority < items.get(index).getPriority()) {
                items.get(index).setPriority(priority);
                percolateUp(index);
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int size() {
        return size;
    }
}
