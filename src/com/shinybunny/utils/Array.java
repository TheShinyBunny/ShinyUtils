package com.shinybunny.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an array of objects, now with more operations and methods!<br/>
 * Example features:
 * <ul>
 *     <li>Toggleable throw on out of bounds - Whether to throw an exception when the array is accessed by an index out of the array bounds.</li>
 *     <li>{@link #reverse()} - Order all items in the array the other way around.</li>
 *     <li>{@link #splice(int, int, Object[])} - Add, remove and replace items in the array simultaneously.</li>
 *     <li>{@link #find(Predicate)} - search for items that meet a condition</li>
 *     <li>+ Some more javascript functions brought to java!</li>
 * </ul>
 * @param <T> The type of items of this array.
 */
public class Array<T> implements Iterable<T> {

    private boolean oobe;
    private T[] data;
    private int length;
    private int initialCapacity;

    //<editor-fold desc="Constructors">
    public Array() {
        this(true);
    }

    public Array(int capacity) {
        this(capacity,true);
    }

    public Array(boolean outOfBoundsException) {
        this(16,outOfBoundsException);
    }

    public Array(int capacity, boolean outOfBoundsException) { // DESTINATION CONSTRUCTOR
        this.oobe = outOfBoundsException;
        this.data = (T[]) new Object[capacity];
        this.initialCapacity = capacity;
        init();
    }

    public Array(Iterable<T> values) {
        this();
        this.addAll(values);
    }

    public Array(Iterable<T> values, boolean outOfBoundsException) {
        this(values);
        this.oobe = outOfBoundsException;
    }

    public Array(Array<T> values) { // DESTINATION CONSTRUCTOR
        this.oobe = values.oobe;
        this.data = values.data;
        this.length = values.length;
        this.initialCapacity = values.initialCapacity;
        init();
    }

    public Array(Array<T> values, boolean outOfBoundsException) {
        this(values);
        this.oobe = outOfBoundsException;
    }

    @SafeVarargs
    public Array(T... values) {
        this(values.length,true);
        this.addAll(values);
    }

    @SafeVarargs
    public Array(boolean outOfBoundsException, T... values) {
        this(values);
        this.oobe = outOfBoundsException;
    }


    //</editor-fold>

    //<editor-fold desc="Private Helpers">

    protected void init() {

    }

    private void outOfBounds(int index) {
        if (oobe) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    private static boolean equals(Object a, Object b, boolean identity) {
        return identity ? a == b : Objects.equals(a,b);
    }

    protected <R> R modify(Supplier<R> modification) {
        return modification.get();
    }

    private void modify(Runnable modification) {
        modify(()->{modification.run();return null;});
    }
    //</editor-fold>

    //<editor-fold desc="Public Helpers">

    @SafeVarargs
    public static <T> Array<T> of(T... items) {
        return new Array<>(items);
    }

    public static <T> Array<T> empty() {
        return new Array<>();
    }

    public static <T> Array<T> fromStream(Stream<T> stream) {
        return new Array<>(stream.toArray()).cast();
    }

    public boolean isInRange(int index) {
        return index >= 0 && index < length;
    }

    /**
     *
     * @return The length of the Array
     */
    public int length() {
        return length;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public void setThrowOnOutOfBounds(boolean oobe) {
        this.oobe = oobe;
    }

    public boolean throwsIndexOutOfBounds() {
        return oobe;
    }

    public Class<T> getItemType() {
        return (Class<T>) data.getClass().getComponentType();
    }
    //</editor-fold>

    //<editor-fold desc="Basic Operations">
    /**
     * Get the element at the specified index
     * @param index The index in the array to look for
     * @return The element at that position
     */
    public T get(int index) {
        if (isInRange(index)) {
            return data[index];
        } else {
            outOfBounds(index);
        }
        return null;
    }

    /**
     * A safe way to get a value in the array.
     * will try to get the element at the specified index, and if the index is in bounds, will activate <code>resultConsumer</code> and return true.
     * @param index The index to get
     * @param resultConsumer The job to preform with the element when found
     * @return true if the element was found
     */
    public boolean tryGet(int index, Consumer<T> resultConsumer) {
        try {
            T t = get(index);
            if (t == null) return false;
            resultConsumer.accept(t);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Replaces the value in the array at the specified index. If the index is out of the array size, it will not increase the size.
     * @param index The index to change
     * @param value The item to put there
     */
    public void set(int index, T value) {
        modify(()-> {
            if (isInRange(index)) {
                data[index] = value;
            } else {
                outOfBounds(index);
            }
        });
    }

    /**
     * Adds an item to the array, at the end of the array.
     */
    public void add(T obj) {
        modify(()-> {
            ensureCapacity(length + 1);
            data[length] = obj;
            length++;
        });
    }

    private void ensureCapacity(int capacity) {
        if (capacity > data.length) {
            T[] newArr = (T[]) new Object[capacity];
            System.arraycopy(data, 0, newArr, 0, data.length);
            data = newArr;
        }
    }

    /**
     * Whether the array is in full capacity, and requires to increase the array size in order to insert more items.
     * @return <code>{@link #length() length} >= data.length</code>
     */
    public boolean inFullCapacity() {
        return length >= data.length;
    }

    /**
     * Checks if the specified object is included in this array.
     * @param obj The item to be checked
     * @param identity True to use <code>t == obj</code>, or false to use <code>Objects.equals(t, obj)</code>.
     * @return True if the specified object exists in the array.
     */
    public boolean contains(T obj, boolean identity) {
        for (T t : this) {
            if (equals(t,obj,identity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds all objects from the <code>Iterable</code> to this array, one after another.
     * @param values The iterable to get the items from.
     */
    public void addAll(Iterable<T> values) {
        for (T t : values) {
            add(t);
        }
    }

    /**
     * Adds all objects from the provided array to this array, one after another.
     * @param items The items to add.
     */
    @SafeVarargs
    public final void addAll(T... items) {
        for (T t : items) {
            add(t);
        }
    }

    /**
     * Removes the element at the specified index.
     * @param index The index to remove
     * @return The removed element
     */
    public T remove(int index) {
        return this.splice(index,1).first();
    }

    /**
     * Removes the specified element from the array.
     * @param t The element to remove
     * @return The index of that element
     */
    public boolean remove(T t, boolean identity) {
        if (this.contains(t,identity)) {
            modify(()-> {
                int index = indexOf(t, identity);
                if (index != -1) {
                    this.remove(index);
                }
            });
            return true;
        }
        return false;
    }

    /**
     * @return The element at the end of the array
     */
    public T last() {
        return get(length-1);
    }

    /**
     *
     * @return The element at the onStart of the array
     */
    public T first() {
        return get(0);
    }

    /**
     * Removes the last element
     * @return the element that was removed
     */
    public T pop() {
        return remove(length-1);
    }

    /**
     * Inserts all items at the start of the array.
     */
    @SafeVarargs
    public final void unshift(T... items) {
        splice(0,0,items);
    }

    /**
     * Removes the first element
     * @return The element that was removed
     */
    public T shift() {
        return remove(0);
    }

    /**
     * Inserts an item, without replacing any, in the specified index.
     * @param index The index to insert the item. The previous item in that index is shifted to the next index, while pushing every item after it forward.
     * @param item The item to add
     */
    public void insert(int index, T item) {
        splice(index,0,item);
    }

    /**
     * Inserts all given items at the specified index, and pushing the previous items forward.
     * @param index
     * @param items
     */
    @SafeVarargs
    public final void insertAll(int index, T... items) {
        splice(index,0,items);
    }

    /**
     * Clears the array
     */
    public void clear() {
        modify(()-> {
            if (isEmpty()) return;
            data = (T[]) new Object[initialCapacity];
            length = 0;
        });
    }

    /**
     * Adds/removes/sets elements in the array.
     * The index is the start of the operation, and it'll remove <code>removeCount</code> elements starting at that position.
     * Then, inserts all items passed in <code>insert</code> starting at the specified index.
     * @param index The index to start from
     * @param removeCount The amount of items to remove
     * @param insert The items to insert
     * @return The removed items
     */
    @SafeVarargs
    public final Array<T> splice(int index, int removeCount, T... insert) {
        Array<T> removed = new Array<>();
        modify(()-> {
            if (isInRange(index)) {
                T[] newArr = (T[]) new Object[length - removeCount + insert.length];
                int j = 0;
                for (int i = 0; i < length; i++) {
                    if (i < index || i >= index + removeCount) {
                        newArr[j] = data[i];
                        j++;
                    } else {
                        removed.add(data[i]);
                    }
                }
                T[] after = (T[]) new Object[newArr.length - index];
                if (newArr.length - index > 0) {
                    System.arraycopy(newArr, index, after, 0, newArr.length - index);
                }
                int insertIndex = 0;
                int i = index;
                for (; i < index + insert.length; i++, insertIndex++) {
                    newArr[i] = insert[insertIndex];
                }
                int k = 0;
                for (int m = i; m < newArr.length; m++, k++) {
                    newArr[m] = after[k];
                }
                data = newArr;
                length = length - removeCount + insert.length;
            } else {
                outOfBounds(index);
            }
        });
        return removed;
    }
    //</editor-fold>

    /**
     * Returns a slice of the array
     * @param start The start index of the sub-array
     * @return a new array from <code>start</code> to the end of this array
     */
    public Array<T> slice(int start) {
        return slice(start,length);
    }

    /**
     * Returns a slice of the array
     * @param start The start index of the sub-array
     * @param end The last index of the items, excluding.
     * @return a new array from <code>start</code> to <code>end - 1</code> of this array
     */
    public Array<T> slice(int start, int end) {
        Array<T> subArr = new Array<>();
        for (int i = start; i < end && i < length(); i++) {
            subArr.add(get(i));
        }
        return subArr;
    }

    /**
     * Reverses all elements in the array
     */
    public void reverse() {
        Array<T> arr = this.copy();
        this.clear();
        for (int i = arr.length - 1; i >= 0; i--) {
            this.add(arr.get(i));
        }
    }

    /**
     * Copies the array
     * @return A copy of this array.
     */
    public Array<T> copy() {
        return new Array<>(this);
    }

    public T[] toArray() {
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(getItemType(),length);
        if (length >= 0) System.arraycopy(data, 0, arr, 0, length);
        return arr;
    }

    /**
     *
     * @return An ArrayList containing all items of this array
     */
    public List<T> toList() {
        return new ArrayList<>(Arrays.asList(data).subList(0, length));
    }

    /**
     * Maps all elements in the array to a string using {@link String#valueOf(Object)}
     * @return An {@link Iterable} of the string representations of all elements from the array.
     */
    public Iterable<String> toStringAll() {
        return map(String::valueOf);
    }

    /**
     * Joins the string representations of all elements ({@link #toStringAll()} by the specified <code>separator</code>
     * @param separator The string to insert between all elements.
     * @return A string result from {@link String#join(CharSequence, CharSequence...) String.join(separator, mapToString())}.
     */
    public String join(String separator) {
        return String.join(separator,this.toStringAll());
    }

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    /**
     * Maps the array to new item types using the specified function.
     * @param func The function to apply to each item to get the new item.
     * @param <N> The new type of items
     * @return a new array of all returned values of the function
     */
    public <N> Array<N> map(Function<T,N> func) {
        Array<N> newArr = new Array<>();
        for (T t : this) {
            newArr.add(func.apply(t));
        }
        return newArr;
    }

    /**
     * Tests whether every element in the array satisfies the given condition
     * @param cond The condition {@link Predicate} to {@link Predicate#test(Object) test} for each item
     * @return <code>false</code> if at least one element did not match the condition.
     */
    public boolean every(Predicate<T> cond) {
        for (T t : this) {
            if (!cond.test(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fills the array with the specified value, repeated <code>count</code> times.
     * @param value The value
     * @param count The number of items to add
     */
    public void fill(T value, int count) {
        fill(value,0,count);
    }

    /**
     * Fills the array using the given function from index 0 to the count - 1.
     * @param values The function to apply for each index
     * @param count The number of times to use the function
     */
    public void fill(Function<Integer,T> values, int count) {
        fill(values,0,count);
    }

    /**
     * Fills the array with the specified value repeatedly, starting at the specified <ocde>start</ocde> index up to <code>end - 1</code>
     * @param value The value
     * @param start The starting index to set items in
     * @param end The index to stop inserting the items
     */
    public void fill(T value, int start, int end) {
        fill(i->value,start,end);
    }

    /**
     * Fills the array using the given function from the start index to the end index.
     * @param values The function to apply for each index
     * @param start The starting index
     * @param end The last index - 1
     */
    public void fill(Function<Integer,T> values, int start, int end) {
        ensureCapacity(end);
        for (int i = start; i < end; i++) {
            this.set(i,values.apply(i));
        }
    }

    /**
     * Filters the array using the given {@link Predicate}, and returns a new array containing only the matching values.
     * @param filter The filter to test for each item
     * @return A new Array with all matching items. the current array will not be modified.
     */
    public Array<T> filter(Predicate<T> filter) {
        Array<T> newArr = new Array<>();
        for (T t : this) {
            if (filter.test(t)) {
                newArr.add(t);
            }
        }
        return newArr;
    }

    /**
     * Finds the first item that matches the given condition
     * @param cond The {@link Predicate} to test for each item until an item returns true.
     * @return The first found item, or null if none found.
     */
    public T find(Predicate<T> cond) {
        return findOrDefault(cond,null);
    }

    public <P> T find(Function<T,P> propertyGetter, P match) {
        return find(Lambda.propertyEquals(propertyGetter,match));
    }

    /**
     * Finds the first item that matches the given condition
     * @param cond The {@link Predicate} to test for each item until an item returns true.
     * @param def The default item to return if no item found.
     * @return The first found item, or def if none found.
     */
    public T findOrDefault(Predicate<T> cond, T def) {
        for (T t : this) {
            if (cond.test(t)) {
                return t;
            }
        }
        return def;
    }

    /**
     * Finds the first item that matches the given condition
     * @param cond The {@link Predicate} to test for each item until an item returns true.
     * @param fallback The {@link Supplier} to use if no item was found.
     * @return The first found item, or {@link Supplier#get()} if none found.
     */
    public T findOrUse(Predicate<T> cond, Supplier<T> fallback) {
        for (T t : this) {
            if (cond.test(t)) {
                return t;
            }
        }
        return fallback.get();
    }

    /**
     * Finds the first item that matches the given condition, or adds the item returned from the supplier.
     * @param cond The {@link Predicate} to test for each item until an item returns true.
     * @param newItem The {@link Supplier} to use if no item was found, and adds that item to the array.
     * @return The first found item, or {@link Supplier#get()} if none found.
     */
    public T findOrAdd(Predicate<T> cond, Supplier<T> newItem) {
        T find = find(cond);
        if (find == null) {
            find = newItem.get();
            add(find);
        }
        return find;
    }

    /**
     * Finds the index of the item that matches the specified condition, starting from the given index.
     * @param cond The condition to test
     * @param from The first index to test for
     * @return The index of the found item, or -1 if not found.
     */
    public int findIndex(Predicate<T> cond, int from) {
        int inc = 1;
        if (from < 0) {
            inc = -1;
            from = length + from;
        }
        for (int i = from; i < length && i >= 0; i+=inc) {
            if (cond.test(get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the index of the item that matches the specified condition
     * @param cond The condition to test
     * @return The index of the found item, or -1 if not found.
     */
    public int findIndex(Predicate<T> cond) {
        return findIndex(cond,0);
    }

    /**
     * Finds the index of the item that equals to the given item
     * @param item The item to match
     * @param identity true to use a == b or false to use {@link Objects#equals(Object, Object)}
     * @return The index of the found item, or -1 if not found.
     */
    public int indexOf(T item, boolean identity) {
        return indexOf(item,identity,0);
    }

    /**
     * Finds the index of the item that equals to the given item, starting from the given index
     * @param item The item to match
     * @param identity true to use a == b or false to use {@link Objects#equals(Object, Object)}
     * @param from The index to start in
     * @return The index of the found item, or -1 if not found.
     */
    public int indexOf(T item, boolean identity, int from) {
        return findIndex(i->equals(i,item,identity),from);
    }

    /**
     * Finds the last index of the item that equals to the given item
     * @param item The item to match
     * @param identity true to use a == b or false to use {@link Objects#equals(Object, Object)}
     * @return The index of the found item, or -1 if not found.
     */
    public int lastIndexOf(T item, boolean identity) {
        return findIndex(i->equals(i,item,identity),-1);
    }

    public int lastIndexOf(T item, int from, boolean identity) {
        return findIndex(i->equals(i,item,identity),-from - 1);
    }

    public boolean contains(Predicate<T> cond) {
        return findIndex(cond) != -1;
    }

    public void removeIf(Predicate<T> test) {
        for (T t : this) {
            if (test.test(t)) {
                remove(t,true);
            }
        }
    }

    public T append(T item) {
        add(item);
        return item;
    }

    public void sort(Comparator<T> comparator) {
        for (int i = 0; i < length-1; i++) {
            for (int j = 0; j < length - i - 1; j++) {
                T first = get(j);
                T second = get(j+1);
                if (comparator.compare(first,second) > 0) {
                    swap(j,j+1);
                }
            }
        }
    }

    public void swap(int i, int j) {
        modify(()->{
            T t = get(i);
            set(i,get(j));
            set(j,t);
        });
    }

    @Override
    public String toString() {
        return "[" + join(", ") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Array)) {
            return false;
        }
        Array<?> other = (Array<?>) obj;
        if (other.length != this.length) return false;
        for (int i = 0; i < length; i++) {
            if (!Objects.equals(other.get(i),this.get(i))) {
                return false;
            }
        }
        return true;
    }

    public <N> Array<N> cast() {
        return map(i->(N)i);
    }

    public void print() {
        System.out.println(this);
    }

    public void print(String prefix) {
        System.out.println(prefix + " " + this);
    }


    public <A> A reduce(BiFunction<A,T,A> callback, A initialValue) {
        A a = initialValue;
        for (T t : this) {
            a = callback.apply(a,t);
        }
        return a;
    }
}
