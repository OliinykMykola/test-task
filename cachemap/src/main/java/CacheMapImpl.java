import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HashTable based implementation of the CacheMap interface.
 * Delegates providing all of the default map operations
 * to the inner HashMap instance.
 *
 * @see CacheMap
 */

public class CacheMapImpl<KeyType, ValueType> implements CacheMap<KeyType, ValueType> {

    private long timeToLive = 1000;

    /**
     * Inner HashMap instance
     */
    private final Map<KeyType, ValueWrapper<ValueType>> innerMap;

    /**
     * The ValueWrapper class wraps a value of the
     * ValueType object for the inner HashMap instance
     */
    private class ValueWrapper<ValueType> {

        /**
         * Wrapped object
         */
        private final ValueType value;

        /**
         * Time, when ValueType object was putted in ValueWrapper object. Final field.
         */
        private final Long putTime;

        private ValueWrapper(ValueType value) {
            this.value = value;
            this.putTime = Clock.getTime();
        }

        @Override
        public boolean equals(Object obj) {
            return value.equals(obj);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        private Long getPutTime() {
            return putTime;
        }

        public ValueType getValue() {
            return value;
        }

    }

    public CacheMapImpl() {
        this.innerMap = new HashMap<>();
    }

    /**
     *
     * @param key Key of the checking instance
     * @return {@code true} if value which the given key is mapped is expired
     */
    private boolean isExpired(KeyType key) {
        if (key != null && innerMap.containsKey(key)) {
            Long time = innerMap.get(key).getPutTime();
            if ((time + timeToLive) > Clock.getTime()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public long getTimeToLive() {
        return timeToLive;
    }

    @Override
    public ValueType put(KeyType key, ValueType value) {
        if (value == null && get(key)!= null) {
            return remove(key);
        } else
            clearExpired();
            return getValueObject(innerMap.put(key, new ValueWrapper<>(value)));
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Override
    public void clearExpired() {
        Iterator<KeyType> iterator = innerMap.keySet().iterator();
        while (iterator.hasNext()) {
            KeyType key = iterator.next();
            if (isExpired(key)) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean containsKey(Object key) {
        clearExpired();
        return innerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        clearExpired();
        for (ValueWrapper v : innerMap.values()) {
            if (v.equals(value)) return true;
        }
        return false;
    }

    @Override
    public ValueType get(Object key) {
        clearExpired();
        return getValueObject(innerMap.get(key));
    }

    @Override
    public boolean isEmpty() {
        clearExpired();
        return innerMap.isEmpty();
    }

    @Override
    public ValueType remove(Object key) {
        clearExpired();
        return getValueObject(innerMap.remove(key));
    }

    /**
     * Unwrapping ValueType object from ValueWrapper object
     *
     * @param valueWrapper ValueWrapper object
     * @return ValueType object
     */
    private ValueType getValueObject(ValueWrapper<ValueType> valueWrapper) {
        return valueWrapper == null ? null : valueWrapper.getValue();
    }

    @Override
    public int size() {
        clearExpired();
        return innerMap.size();
    }

}