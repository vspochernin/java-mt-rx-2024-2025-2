package ru.vspochernin.rx;

import java.util.function.Consumer;

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
} 