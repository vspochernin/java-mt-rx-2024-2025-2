package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class MappedObserver<T, R> implements Observer<T> {

    private final Observer<R> observer;
    private final Function<T, R> mapper;
    private final AtomicBoolean disposed;

    public MappedObserver(Observer<R> observer, Function<T, R> mapper, AtomicBoolean disposed) {
        this.observer = observer;
        this.mapper = mapper;
        this.disposed = disposed;
    }

    @Override
    public void onNext(T item) {
        if (!disposed.get()) {
            try {
                observer.onNext(mapper.apply(item));
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