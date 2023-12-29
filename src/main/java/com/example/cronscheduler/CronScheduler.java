package com.example.cronscheduler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// Scheduling Frequency
enum Frequency {
    MINUTELY, HOURLY, DAILY, WEEKLY
}

// JobExecutor State
enum State {
    SCHEDULED, RUNNING, FAILED, FINISHED
}

// CronScheduler class to schedule and manage jobs
class CronScheduler {
    final ScheduledExecutorService executorService;
    private final EnumMap<Frequency, Long> frequencyMap;

    CronScheduler(int poolSize) {
        this.executorService = Executors.newScheduledThreadPool(poolSize);
        this.frequencyMap = new EnumMap<>(Frequency.class);
        frequencyMap.put(Frequency.MINUTELY, TimeUnit.MINUTES.toMinutes(1));
        frequencyMap.put(Frequency.HOURLY, TimeUnit.HOURS.toMinutes(1));
        frequencyMap.put(Frequency.DAILY, TimeUnit.DAYS.toMinutes(1));
        frequencyMap.put(Frequency.WEEKLY, TimeUnit.DAYS.toMinutes(7));
    }

    public void scheduleJob(String jobId, Runnable job, Frequency freq, int intervalInMinutes) {
        long period = frequencyMap.get(freq);

        CustomLogger.logInfo("SCHEDULED: Job %s with ID %s".formatted(
                job.getClass().getSimpleName(), jobId));
        executorService.scheduleAtFixedRate(
                new JobExecutor(job, jobId),
                intervalInMinutes,
                period,
                TimeUnit.MINUTES);

    }

}

// JobExecutor class to execute jobs
class JobExecutor implements Runnable {
    private final Runnable job;
    private State state = State.SCHEDULED;
    private String jobId;

    public boolean isRunning() {
        return this.state == State.RUNNING;
    }

    public boolean isScheduled() {
        return this.state == State.SCHEDULED;
    }

    public boolean isFailed() {
        return this.state == State.FAILED;
    }

    public boolean isFinished() {
        return this.state == State.FINISHED;
    }

    public State getState() {
        return this.state;
    }

    JobExecutor(Runnable job, String jobId) {
        this.job = job;
        this.state = State.SCHEDULED;
        this.jobId = jobId;
    }

    @Override
    public void run() {
        try {
            // Before running the job, we calculate execution time and set the state.
            String jobClassString = job.getClass().getSimpleName();
            this.state = State.RUNNING;
            LocalDateTime startTime = LocalDateTime.now();
            CustomLogger.logInfo("STARTING: %s of ID:%s".formatted(jobClassString, this.jobId));
            job.run();
            LocalDateTime endTime = LocalDateTime.now();
            Duration executionDuration = Duration.between(startTime, endTime);

            CustomLogger.logInfo(
                    "COMPLETED: Job %s in %d milliseconds".formatted(this.jobId, executionDuration.toMillis()));

        } catch (Exception e) {
            CustomLogger.logError("FAILED: job %s: %s".formatted(this.jobId, e.getMessage()));
            this.state = State.FAILED;
        } finally {
            if (!isFailed()) {
                // After run is done, the job is either finished or failed.
                this.state = State.FINISHED;
            }
        }
    }
}

// Threadsafe Logger that saves output logs
class CustomLogger {
    private static final Logger logger = Logger.getLogger(CustomLogger.class.getName());
    static {
        try {
            final FileHandler fileHandler = new FileHandler("./out/output.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Error initializing FileHandler: " + e.getMessage());
        }
    }

    static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    static void logError(String message) {
        logger.log(Level.SEVERE, message);
    }
}

// JobConfig class to facilitate the transport of job configs
class JobConfig {
    private String jobClassName;
    private String jobId;
    private Integer interval;
    private String frequency;

    public JobConfig(String jobClassName, String jobId, Integer interval, String frequency) {
        this.jobClassName = jobClassName;
        this.jobId = jobId;
        this.interval = interval;
        this.frequency = frequency;
        // Check if all params are provided
        if (jobId == null || jobClassName == null || interval == null || frequency == null) {
            throw new IllegalArgumentException("One or more input parameters are missing");
        }
    }

    // Getters
    public String getJobClassName() {
        return jobClassName;
    }

    public String getJobId() {
        return jobId;
    }

    public Integer getInterval() {
        return interval;
    }

    public String getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "JobConfig{" +
                "jobClassName='" + jobClassName + '\'' +
                ", jobId='" + jobId + '\'' +
                ", interval=" + interval +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}