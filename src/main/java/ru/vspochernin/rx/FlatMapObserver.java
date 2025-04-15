package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class FlatMapObserver<T, R> implements Observer<T> {

    private final Observer<R> observer;
    private final Function<? super T, ? extends Observable<? extends R>> mapper;
    private final AtomicBoolean disposed;
    private final AtomicBoolean hasError;
    private final AtomicInteger activeCount;

    public FlatMapObserver(Observer<R> observer, Function<? super T, ? extends Observable<? extends R>> mapper,
            AtomicBoolean disposed)
    {
        this.observer = observer;
        this.mapper = mapper;
        this.disposed = disposed;
        this.hasError = new AtomicBoolean(false);
        this.activeCount = new AtomicInteger(1);
    }

    @Override
    public void onNext(T item) {
        if (!disposed.get() && !hasError.get()) {
            try {
                Observable<? extends R> innerObservable = mapper.apply(item);
                activeCount.incrementAndGet();

                Observer<R> innerObserver = new Observer<>() {
                    @Override
                    public void onNext(R innerItem) {
                        if (!disposed.get() && !hasError.get()) {
                            observer.onNext(innerItem);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (hasError.compareAndSet(false, true) && !disposed.get()) {
                            observer.onError(t);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (activeCount.decrementAndGet() == 0 && !hasError.get() && !disposed.get()) {
                            observer.onComplete();
                        }
                    }
                };

                ((Observable<R>) innerObservable).subscribe(innerObserver);
            } catch (Throwable t) {
                if (hasError.compareAndSet(false, true) && !disposed.get()) {
                    observer.onError(t);
                }
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        if (hasError.compareAndSet(false, true) && !disposed.get()) {
            observer.onError(t);
        }
    }

    @Override
    public void onComplete() {
        if (activeCount.decrementAndGet() == 0 && !hasError.get() && !disposed.get()) {
            observer.onComplete();
        }
    }
} 