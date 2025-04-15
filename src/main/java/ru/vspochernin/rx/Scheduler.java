package ru.vspochernin.rx;

public interface Scheduler {
    void execute(Runnable task);
    void shutdown();
} 