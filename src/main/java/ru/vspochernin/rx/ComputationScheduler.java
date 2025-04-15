package ru.vspochernin.rx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComputationScheduler extends AbstractScheduler {
    private static final ComputationScheduler INSTANCE = new ComputationScheduler();

    private ComputationScheduler() {
        super(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    public static ComputationScheduler getInstance() {
        return INSTANCE;
    }
} 