package com.example.cronscheduler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class CronSchedulerTest {
    @Test
    void scheduleJob_SchedulesJob() {
        // Mock job
        Runnable mockJob = mock(Runnable.class);

        // Create CronScheduler instance
        CronScheduler cronScheduler = new CronScheduler(1);

        // Schedule a job
        cronScheduler.scheduleJob("TestId", mockJob, Frequency.MINUTELY, 5);
        ScheduledThreadPoolExecutor mockExecutor = (ScheduledThreadPoolExecutor) cronScheduler.executorService;
        // Verify that the job was scheduled correctly
        assertEquals(1, mockExecutor.getQueue().size());
    }
}
class JobExecutorTest {

    @Test
    void run_Executes() {
        // Mock job, finishes instantly
        Runnable mockJob = mock(Runnable.class);

        // Create JobExecutor instance
        JobExecutor jobExecutor = new JobExecutor(mockJob, "testId");

        // Run the job
        jobExecutor.run();

        // Verify that the job's run method was called
        verify(mockJob, times(1)).run();

        // Ensure the the job is finished
        assertEquals(State.FINISHED, jobExecutor.getState());
    }

    @Test
    void run_FailsOnException() {
        // Mock job
        Runnable mockJob = mock(Runnable.class);

        // Create JobExecutor instance
        JobExecutor jobExecutor = new JobExecutor(mockJob, "testId");

        // Make run() throw an exception
        doThrow(new RuntimeException()).when(mockJob).run();

        jobExecutor.run();

        // Verify that the job failed
        assertEquals(State.FAILED, jobExecutor.getState());
    }

    @Test
    void run_Schedules() {
        // Mock job
        Runnable mockJob = mock(Runnable.class);

        // Create JobExecutor instance
        JobExecutor jobExecutor = new JobExecutor(mockJob, "testId");
        
        // Create CronScheduler instance
        CronScheduler cronScheduler = new CronScheduler(1);

        // Schedule a job with delay
        cronScheduler.scheduleJob("TestId", jobExecutor, Frequency.HOURLY, 5);

        // Verify that the job is scheduled
        assertEquals(State.SCHEDULED, jobExecutor.getState());
    }

    @Test
    void run_SetsRunning() {
        // Mock job
        Runnable mockJob = mock(Runnable.class);
        // Create JobExecutor instance
        JobExecutor jobExecutor = new JobExecutor(mockJob, "testId");
        
        doAnswer(invocation -> {
            Thread.sleep(Long.MAX_VALUE);
            return null;
        }).when(mockJob).run();

        // Create CronScheduler instance
        CronScheduler cronScheduler = new CronScheduler(1);

        // Schedule a job without delay (instantly)
        cronScheduler.scheduleJob("TestId", jobExecutor, Frequency.HOURLY, 0);

        // Verify that the job is running
        assertEquals(State.RUNNING, jobExecutor.getState());
    }

}
