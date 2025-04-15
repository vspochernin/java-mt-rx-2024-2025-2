package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduledObserver<T> implements Observer<T> {

    private final Observer<T> observer;
    private final Scheduler scheduler;
    private final AtomicBoolean disposed;

    public ScheduledObserver(Observer<T> observer, Scheduler scheduler, AtomicBoolean disposed) {
        this.observer = observer;
        this.scheduler = scheduler;
        this.disposed = disposed;
    }

    @Override
    public void onNext(T item) {
        if (!disposed.get()) {
            scheduler.execute(() -> {
                if (!disposed.get()) {
                    observer.onNext(item);
                }
            });
        }
    }

    @Override
    public void onError(Throwable t) {
        if (!disposed.get()) {
            scheduler.execute(() -> {
                if (!disposed.get()) {
                    observer.onError(t);
                }
            });
        }
    }

    @Override
    public void onComplete() {
        if (!disposed.get()) {
            scheduler.execute(() -> {
                if (!disposed.get()) {
                    observer.onComplete();
                }
            });
        }
    }
} 