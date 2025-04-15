package ru.vspochernin.rx;

import java.util.concurrent.Executors;

public class SingleThreadScheduler extends AbstractScheduler {

    private static final SingleThreadScheduler INSTANCE = new SingleThreadScheduler();

    private SingleThreadScheduler() {
        super(Executors.newSingleThreadExecutor());
    }

    public static SingleThreadScheduler getInstance() {
        return INSTANCE;
    }
} 