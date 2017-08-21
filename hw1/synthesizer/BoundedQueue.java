package synthesizer;

import java.util.Iterator;

/** Interface for bounded queue. Ts can only be enqueued at the back of the queue, and can
 *  only be dequeued from the front of the queue.
 *  @author moboa
 */
public interface BoundedQueue<T> extends Iterable<T> {

    /* Returns the maximum number of items the queue can hold. */
    int capacity();

    /* Returns the number of items in the queue */
    int fillCount();

    /* Adds an item x at the back of the queue. */
    void enqueue(T x);

    /* Deletes and returns the item at the front of the queue. */
    T dequeue();

    /* Returns the item at the front of the queue without deleting it. */
    T peek();

    /* Returns true if the queue is empty, false otherwise. */
    default boolean isEmpty() {
        return fillCount() == 0;
    }

    /* Returns true if the queue is full, false otherwise. */
    default boolean isFull() {
        return fillCount() == capacity();
    }

    Iterator<T> iterator();
}
