# Cron Scheduler Project

## Overview

This project is a simple cron scheduler implemented in Java that allows users to schedule periodic job executions.
Users define job classes in the `Jobs.java` file and provide execution details in a configuration file.
The scheduler is then invoked via the Main method from the command line.

## Features

- **Periodic Job Scheduling:** Users can schedule jobs to run periodically by providing details in a configuration file.
- **Flexible Timing:** Jobs are executed based on user-defined intervals and frequencies.
- **ThreadPool Configuration:** Users can configure the size of the thread pool used for multithreading.
- **Out-of-Order Execution:** Jobs are executed independently, allowing for out-of-order execution. The scheduler does not enforce any specific order of execution.

## Project Structure

```
project-root/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/cronscheduler/
│   │   │   ├── CronScheduler.java
│   │   │   ├── Jobs.java
│   │   │   └── Main.java
│   │
│   ├── test/
│   │   ├── java/com/example/cronscheduler/
│   │   │   └── CronSchedulerTest.java
│   │
└── config.txt            # Sample configuration file for job scheduling
```

## Getting Started

1. **Define Job Classes:**
   - Define job classes in the `Jobs.java` file. Each job class must implement the `Runnable` interface.

2. **Configure Jobs:**
   - Edit the `config.txt` file to specify the following details of each job's execution.
   - Each line represents one job:

   ```<jobClassName> <jobId>, <intervalInMinutes>, <Frequency>```

   -Example: 
   ```
   PrintLineJob jobId1, 2, HOURLY
   ```
3. **Compile Project:**
   - Compile the project using the following command:

     ```bash
      javac -d bin Main.java CronScheduler.java Jobs.java
     ```

4. **Run Scheduler:**
   - Run the scheduler with the desired thread pool size:

     ```bash
     java -cp bin com.example.cronscheduler.Main <poolSize>
     ```

   - Replace `<poolSize>` with the desired size of the thread pool.

5. **View Scheduled Jobs:**
   - The scheduler will log information about the scheduled jobs, including job class names, job IDs, and elapsed time and other real-time events.
   - The information will also be logged to a separate file 'output.log'

## Sample Configuration File (config.txt)

```plaintext
com.example.Job1 JobId1, 5, MINUTELY
com.example.Job2 JobId2, 10, HOURLY
com.example.Job3 JobId3, 15, DAILY
```

## Assumptions made

- Periodic job execution continues indefinitely until the scheduler is interrupted.
- Interval is the delay before the execution of job.
- Interval must be provided, putting 0 will set no interval.
- There is no guaranteed order of execution.
- The user will provide all jobs in a single configuration file.

## Further Work

The current implementation provides a solid foundation for a cron scheduler, but there are other areas of improvement that can be achieved.

- **Interactive GUI:**
  Implementing an interactive graphical user interface to enhance user experience.

- **Auto-generated JobID:**
  Automate the assignment of JobIDs to avoid potential conflicts when users manually assign JobIDs.

- **Enhanced Visibility:**
  Provide detailed logging, real-time updates, and error logs for better user insight.

- **Data Storage with Tables:**
  Integrate a database to maintain a history of scheduled jobs for managament, querying, and analysis.

- **Job Timeout Option:**
  Implement an option for specifying a timeout for individual jobs.

- **Flexible Configuration:**
  Allow users the flexibility to choose not to provide either the "frequency" or "interval" for a job which would allow for one-time jobs.

- **Dynamic Job Scheduling:**
  Enable users to dynamically add or remove jobs without stopping the scheduler, this would work especially well with a GUI.

- **Error Handling and Notifications:**
  Implement a more robust error-handling mechanism to gracefully handle exceptions during execution.



## Dependencies

- Java (JDK 8 or higher)