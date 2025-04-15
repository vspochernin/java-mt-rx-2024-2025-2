package ru.vspochernin.rx;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {
    private final Consumer<Observer<T>> source;

    private Observable(Consumer<Observer<T>> source) {
        this.source = source;
    }

    public static <T> Observable<T> create(Consumer<Observer<T>> source) {
        return new Observable<>(source);
    }

    public void subscribe(Observer<T> observer) {
        try {
            source.accept(observer);
        } catch (Throwable t) {
            observer.onError(t);
        }
    }

    public <R> Observable<R> map(Function<T, R> mapper) {
        return new Observable<>(observer -> 
            subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    try {
                        observer.onNext(mapper.apply(item));
                    } catch (Throwable t) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            })
        );
    }

    public Observable<T> filter(Predicate<T> predicate) {
        return new Observable<>(observer ->
            subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    try {
                        if (predicate.test(item)) {
                            observer.onNext(item);
                        }
                    } catch (Throwable t) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            })
        );
    }
} 