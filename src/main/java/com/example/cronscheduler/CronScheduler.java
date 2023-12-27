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

// Enum for scheduling frequency
enum Frequency {
    MINUTELY, HOURLY, DAILY, WEEKLY
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

        executorService.scheduleAtFixedRate(
                new JobExecutor(job),
                intervalInMinutes,
                period,
                TimeUnit.MINUTES
        );

        CustomLogger.logInfo("Job %s scheduled with ID %s".formatted(
                job.getClass().getSimpleName(), jobId));
    }

}

// JobExecutor class to execute jobs
class JobExecutor implements Runnable {
    private final Runnable job;
    private volatile boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    JobExecutor(Runnable job) {
        this.job = job;
        this.isRunning = false;
    }

    @Override
    public void run() {
        if (isRunning) {
            CustomLogger.logError("Previous execution still in progress. Skipping this execution.");
            return;
        }
        try {
            setRunning(true);
            LocalDateTime startTime = LocalDateTime.now();
            job.run();
            LocalDateTime endTime = LocalDateTime.now();
            Duration executionDuration = Duration.between(startTime, endTime);
            String className = job.getClass().getSimpleName();
            CustomLogger.logInfo("Job %s completed in %d milliseconds".formatted(className, executionDuration.toMillis()));
            
        } catch (Exception e) {
            CustomLogger.logError("Error executing job: %d".formatted(e.getMessage()));
        } finally {
            setRunning(false);
        }
    }
}

// Threadsafe Logger class
class CustomLogger {
    private static final Logger logger = Logger.getLogger(CustomLogger.class.getName());
    static {
        try {
            final FileHandler fileHandler = new FileHandler("./com/example/cronscheduler/output.log");
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


class JobConfig {
    private String jobClassName;
    private String jobId;
    private Integer interval; // optional
    private String frequency; // optional

    public JobConfig(String jobClassName, String jobId, String frequency){
        this(jobClassName, jobId, 0, frequency);
    }
    public JobConfig(String jobClassName, String jobId, Integer interval, String frequency) {
        this.jobClassName = jobClassName;
        this.jobId = jobId;
        this.interval = interval;
        this.frequency = frequency;
        // Check if jobId and jobClassName are provided
        if (jobId == null || jobClassName == null) {
            throw new IllegalArgumentException("jobId and jobClassName are required");
        }

        // Validate that either interval or frequency is present
        if (interval == null && frequency == null) {
            throw new IllegalArgumentException("At least either interval or frequency must be provided for job: " + jobId);
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