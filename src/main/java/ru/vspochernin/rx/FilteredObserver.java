package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class FilteredObserver<T> implements Observer<T> {

    private final Observer<T> observer;
    private final Predicate<T> predicate;
    private final AtomicBoolean disposed;

    public FilteredObserver(Observer<T> observer, Predicate<T> predicate, AtomicBoolean disposed) {
        this.observer = observer;
        this.predicate = predicate;
        this.disposed = disposed;
    }

    @Override
    public void onNext(T item) {
        if (!disposed.get()) {
            try {
                if (predicate.test(item)) {
                    observer.onNext(item);
                }
            } catch (Throwable t) {
                observer.onError(t);
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        if (!disposed.get()) {
            observer.onError(t);
        }
    }

    @Override
    public void onComplete() {
        if (!disposed.get()) {
            observer.onComplete();
        }
    }
} 