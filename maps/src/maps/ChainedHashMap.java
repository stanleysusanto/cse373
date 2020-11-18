package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 0.75; //unchecked
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 16;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 4;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;
    private int size;
    private double loadFactorThreshold;
    private int chainInitial;
    private int mapCount;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.chains = this.createArrayOfChains(initialChainCount);
        loadFactorThreshold = resizingLoadFactorThreshold;
        chainInitial = chainInitialCapacity;
        size = 0;
        mapCount = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     * <p>
     * Note that each element in the array will initially be null.
     * <p>
     * Note: You do not need to modify this method.
     *
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     * <p>
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     * <p>
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        V value = null;
        int bucketKey = key == null ? 0 : Math.abs(key.hashCode()) % chains.length;
        AbstractIterableMap<K, V> chain = chains[bucketKey];
        if (chain != null && chain.containsKey(key)) {
            value = chain.get(key);
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
        V pastVal = null;
        int bucketKey = key == null ? 0 : Math.abs(key.hashCode()) % chains.length;
        AbstractIterableMap<K, V> chain = chains[bucketKey];
        if (chain != null && !chain.containsKey(key)) {
            chain.put(key, value);
            size++;
        } else if (chain != null && chain.containsKey(key)) {
            pastVal = chain.get(key);
            chain.remove(key);
            chain.put(key, value);
        } else if (chain == null) {
            chains[bucketKey] = createChain(10);
            chains[bucketKey].put(key, value);
            size++;
            mapCount++;
        }
        if ((size * 1.0) / chains.length >= loadFactorThreshold) {
            AbstractIterableMap<K, V>[] newChains = createArrayOfChains(2 * chains.length);
            size = 0;
            for (int i = 0; i < chains.length; i++) {
                if (this.chains[i] != null) {
                    for (Entry<K, V> entry : this.chains[i]) {
                        int newHashCode = entry.getKey() == null ? 0 : Math.abs(key.hashCode()) % (2 * chains.length);
                        if (newChains[newHashCode] == null) {
                            newChains[newHashCode] = this.createChain(chainInitial);
                        }
                        newChains[newHashCode].put(key, value);
                    }
                }
            }
        }

        return pastVal;
    }

    @Override
    public V remove(Object key) {
        V value = null;
        int bucketKey = key == null ? 0 : Math.abs(key.hashCode()) % chains.length;
        AbstractIterableMap<K, V> chain = chains[bucketKey];
        if (chain != null && chain.containsKey(key)) {
            value = chain.get(key);
            chain.remove(key);
            mapCount--;
        }
        return value;
    }

    @Override
    public void clear() {
        chains = createArrayOfChains(chains.length);
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int bucketKey = key == null ? 0 : Math.abs(key.hashCode()) % chains.length;
        AbstractIterableMap<K, V> chain = chains[bucketKey];
        if (chain == null) {
            return false;
        }
        return chain.containsKey(key);
    }

    @Override
    public int size() {
        return mapCount;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains, this.mapCount);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int chainIndex = 0;
        private int mapCount;
        private int index = 0;
        private Iterator<Map.Entry<K, V>> itr2;

        // You may add more fields and constructor parameters

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains, int mapCount) {
            this.chains = chains;
            this.mapCount = mapCount;
            for (int i = 0; i < chains.length; i++) {
                if (chains[i] != null) {
                    itr2 = chains[i].iterator();
                    index = i;
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return itr2 != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                Map.Entry<K, V> result = itr2.next();
                if (!itr2.hasNext()) {
                    itr2 = null;
                    for (int i = index + 1; i < chains.length; i++) {
                        if (chains[i] != null) {
                            itr2 = chains[i].iterator();
                            index = i;
                            break;
                        }
                    }
                }
                return result;
            }
        }
    }
}
