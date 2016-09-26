package cz.zcu.fav.remotestimulatorcontrol.util;

import android.databinding.ListChangeRegistry;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;

import java.util.AbstractList;

/**
 * Implementace pozorovatelné setříděné kolekce
 * Ve výchozím API se tato třída nevyskytuje
 *
 * @param <T>
 * @see <a href="http://stackoverflow.com/a/31522316">Zdrojový kód</a>
 */
public class ObservableSortedList<T> extends AbstractList<T> implements ObservableList<T> {

    private static final ThreadLocal<Boolean> sTlsUpdated = new ThreadLocal<>();
    // Reference na standartní SortedList
    private final SortedList<T> mList;
    // Register listenerů
    @Nullable
    private transient ListChangeRegistry mListeners = new ListChangeRegistry();

    /**
     * Konstruktor třídy {@link ObservableSortedList}
     *
     * @param klass Třída, která bude uložena v kolekci
     * @param callback {@link android.support.v7.util.SortedList.Callback}
     */
    public ObservableSortedList(final Class<T> klass, final Callback<T> callback) {
        mList = new SortedList<>(klass, new CallbackWrapper<>(callback));
    }

    /**
     * @see SortedList#beginBatchedUpdates()
     */
    public void beginBatchedUpdates() {
        mList.beginBatchedUpdates();
    }

    /**
     * @see SortedList#endBatchedUpdates()
     */
    public void endBatchedUpdates() {
        mList.endBatchedUpdates();
    }

    // region AbstractList override
    @Override
    public boolean add(final T item) {
        sTlsUpdated.set(false);
        mList.add(item);
        return sTlsUpdated.get();   // May be set by Callback.onInserted() or onChanged().
    }

    @Override
    public T set(final int location, final T object) {
        final T old = mList.get(location);
        mList.updateItemAt(location, cast(object));
        return old;
    }

    @Override
    public int indexOf(final Object object) {
        try {
            return mList.indexOf(cast(object));
        } catch (final ClassCastException ignored) {
            return -1;
        }
    }

    @Override
    public boolean remove(final Object object) {
        try {
            return mList.remove(cast(object));
        } catch (final ClassCastException ignored) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private T cast(final Object object) {
        return (T) object;
    }

    @Override
    public boolean contains(final Object object) {
        return indexOf(object) != SortedList.INVALID_POSITION;
    }

    @Override
    public T get(final int location) {
        return mList.get(location);
    }

    @Override
    public int size() {
        return mList.size();
    }
    // endregion

    // region ObservableList override
    /**
     * @see AbstractList#clear()
     */
    @Override
    public void clear() {
        mList.clear();
    }

    /**
     * @see AbstractList#remove(int)
     */
    @Override
    public T remove(final int location) {
        return mList.removeItemAt(location);
    }

    /**
     * @see ObservableList#addOnListChangedCallback(OnListChangedCallback)
     */
    @Override
    public void addOnListChangedCallback(final OnListChangedCallback<? extends ObservableList<T>> callback) {
        if (mListeners == null) {
            this.mListeners = new ListChangeRegistry();
        }

        mListeners.add(callback);
    }

    /**
     * @see ObservableList#removeOnListChangedCallback(OnListChangedCallback)
     */
    @Override
    public void removeOnListChangedCallback(final OnListChangedCallback<? extends ObservableList<T>> callback) {
        if (mListeners == null) {
            return;
        }

        mListeners.remove(callback);
    }

    // endregion
    /**
     * Odlehčená verze callbacku z třídy {@link SortedList.Callback}
     */
    public interface Callback<T2> {
        /**
         * @see android.support.v7.util.SortedList.Callback#compare(Object, Object)
         */
        int compare(T2 o1, T2 o2);

        /**
         * @see android.support.v7.util.SortedList.Callback#areItemsTheSame(Object, Object)
         */
        boolean areItemsTheSame(T2 item1, T2 item2);

        /**
         * @see android.support.v7.util.SortedList.Callback#areContentsTheSame(Object, Object)
         */
        boolean areContentsTheSame(T2 oldItem, T2 newItem);
    }

    /**
     * Callback wrapper (návrhový vzor adapter)
     * Implementace všech důležitých medot z {@link SortedList.Callback}
     *
     * @param <T2> Callback pro {@link ObservableSortedList}
     */
    public class CallbackWrapper<T2> extends SortedList.Callback<T2> {

        private final Callback<T2> mCallback;

        public CallbackWrapper(final Callback<T2> callback) {
            mCallback = callback;
        }

        /**
         * @see SortedList.Callback#onInserted(int position, int count)
         */
        @Override
        public final void onInserted(final int position, final int count) {
            sTlsUpdated.set(true);
            if (mListeners != null) {
                mListeners.notifyInserted(ObservableSortedList.this, position, count);
            }
        }

        /**
         * @see SortedList.Callback#onRemoved(int position, int count)
         */
        @Override
        public final void onRemoved(final int position, final int count) {
            if (mListeners != null) {
                mListeners.notifyRemoved(ObservableSortedList.this, position, count);
            }
        }

        /**
         * @see SortedList.Callback#onMoved(int fromPosition, int toPosition)
         */
        @Override
        public final void onMoved(final int fromPosition, final int toPosition) {
            if (mListeners != null) {
                mListeners.notifyMoved(ObservableSortedList.this, fromPosition, toPosition, 1);
            }
        }

        /**
         * @see SortedList.Callback#onChanged(int position, int count)
         */
        @Override
        public final void onChanged(final int position, final int count) {
            sTlsUpdated.set(true);
            if (mListeners != null) {
                mListeners.notifyChanged(ObservableSortedList.this, position, count);
            }
        }

        /**
         * @see SortedList.Callback#compare(T2 o1, T2 o2)
         */
        @Override
        public int compare(final T2 o1, final T2 o2) {
            return mCallback.compare(o1, o2);
        }

        /**
         * @see SortedList.Callback#areContentsTheSame(T2 oldItem, T2 newItem)
         */
        @Override
        public boolean areContentsTheSame(final T2 oldItem, final T2 newItem) {
            return mCallback.areContentsTheSame(oldItem, newItem);
        }

        /**
         * @see SortedList.Callback#areItemsTheSame(T2 item1, T2 item2)
         */
        @Override
        public boolean areItemsTheSame(final T2 item1, final T2 item2) {
            return mCallback.areItemsTheSame(item1, item2);
        }
    }
}
