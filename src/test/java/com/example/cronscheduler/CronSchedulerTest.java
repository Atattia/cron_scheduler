package com.example.cronscheduler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class CronSchedulerTest {
    @Test
    void scheduleJob_SchedulesJobCorrectly() {
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
    void run_ExecutesSuccessfully() {
        // Mock job
        Runnable mockJob = mock(Runnable.class);

        // Create JobExecutor instance
        JobExecutor jobExecutor = new JobExecutor(mockJob);

        // Run the job
        jobExecutor.run();

        // Verify that the job's run method was called
        verify(mockJob, times(1)).run();
    }

}
