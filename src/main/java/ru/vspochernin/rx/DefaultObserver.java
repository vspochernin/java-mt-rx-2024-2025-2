package ru.vspochernin.rx;

import java.util.ArrayList;
import java.util.List;

public class DefaultObserver<T> implements Observer<T> {

    private final List<T> items = new ArrayList<>();
    private Throwable error;
    private boolean completed;

    @Override
    public void onNext(T item) {
        if (!completed && error == null) {
            items.add(item);
        }
    }

    @Override
    public void onError(Throwable t) {
        if (!completed) {
            error = t;
        }
    }

    @Override
    public void onComplete() {
        if (error == null) {
            completed = true;
        }
    }

    public List<T> getItems() {
        return items;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isCompleted() {
        return completed;
    }
} 