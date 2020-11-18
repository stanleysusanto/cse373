package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;
    private int size;

    // You may add extra fields or helper methods though!

    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     * <p>
     * Note that each element in the array will initially be null.
     * <p>
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        V value = null;
        for (SimpleEntry<K, V> map : entries) {
            if (map != null && ((key == map.getKey()) || (key.equals(map.getKey())))) {
                value = map.getValue();
            }
        }

        return value;
    }

    @Override
    public V put(K key, V value) {
        int index = 0;
        boolean keyExisted = false;
        V addedVal = null;
        SimpleEntry<K, V> entry = new SimpleEntry<>(key, value);
        // if ( > 0) {
        //     return addedVal;
        // }
        if (size == entries.length) {
            SimpleEntry<K, V>[] entriesTemp = createArrayOfEntries(2 * entries.length);
            for (SimpleEntry<K, V> map : entries) {
                if (map != null && (key == map.getKey() || map.getKey().equals(key))) {
                    keyExisted = true;
                    break;
                }
                entriesTemp[index] = map;
                index++;
            }
            if (!keyExisted) {
                entries = entriesTemp;
            }
        } else {
            for (SimpleEntry<K, V> map : entries) {
                if (map != null && (key == map.getKey() || map.getKey().equals(key))) {
                    keyExisted = true;
                    break;
                }
                index++;
            }
        }
        if (!keyExisted && entries.length != 0) {
            entries[size] = entry;
            size++;
        } else if (entries.length != 0) {
            addedVal = entries[index].getValue();
            entries[index] = entry;
        }
        return addedVal;
    }

    @Override
    public V remove(Object key) {
        V value = null;
        int index = 0;
        if (entries.length > 0) {
            for (SimpleEntry<K, V> map : entries) {
                if (map != null && (key == map.getKey() || map.getKey().equals(key))) {
                    value = map.getValue();
                    entries[index] = entries[size - 1];
                    entries[size - 1] = null;
                    size--;
                    break;
                }
                index++;
            }
        }
        return value;
    }

    @Override
    public void clear() {
        int index = 0;
        for (SimpleEntry<K, V> map : entries) {
            if (map != null) {
                entries[index] = null;
            }
            size = 0;
            index++;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        for (SimpleEntry<K, V> map : entries) {
            if (map != null && (key == map.getKey() || key.equals(map.getKey()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int index = 0;
        // You may add more fields and constructor parameters

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
        }

        @Override
        public boolean hasNext() {
            if (entries.length != 0 && index < entries.length) {
                return entries[index] != null;
            } else {
                return false;
            }
        }

        @Override
        public Map.Entry<K, V> next() {
            if (entries.length != 0 && index < entries.length && hasNext()) {
                Map.Entry<K, V> entry = entries[index];
                index++;
                return entry;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
