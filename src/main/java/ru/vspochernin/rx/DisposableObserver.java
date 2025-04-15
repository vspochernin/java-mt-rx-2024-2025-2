package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;

public class DisposableObserver<T> implements Observer<T> {
    private final Observer<T> observer;
    private final AtomicBoolean disposed;

    public DisposableObserver(Observer<T> observer, AtomicBoolean disposed) {
        this.observer = observer;
        this.disposed = disposed;
    }

    @Override
    public void onNext(T item) {
        if (!disposed.get()) {
            observer.onNext(item);
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