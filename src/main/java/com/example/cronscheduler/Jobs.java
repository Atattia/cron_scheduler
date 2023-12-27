package com.example.cronscheduler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class PrintLineJob implements Runnable {
    @Override
    public void run() {
        // Implement your job logic here
        CustomLogger.logInfo("Executing PrintLineJob: Hello, this is a simple line!");
    }
}

class CurrentTimeJob implements Runnable {
    @Override
    public void run() {
        // Implement your job logic here
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        CustomLogger.logInfo("Executing CurrentTimeJob, Current time is %s".formatted(formattedTime));
    }
}

class InfiniteSleepJob implements Runnable {
    @Override
    public void run() {
        // Implement your job logic here
        try {
            CustomLogger.logInfo("Executing InfiniteSleepJob, Zzzzzzzzzz ...");
            Thread.sleep(Long.MAX_VALUE); // Sleep infinitely
        } catch (InterruptedException e) {
            CustomLogger.logError("InfiniteSleepJob interrupted: %s".formatted(e.getMessage()));
        }
    }
}
