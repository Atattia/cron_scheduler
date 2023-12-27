package com.example.cronscheduler;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Properties;
public class Main {
    public static void main(String[] args){
        try {
            if (args.length != 1) {
                CustomLogger.logError("Usage: java Main <poolSize>");
                System.exit(1);
            }
            //Fetch Job details from config file
            ArrayList<JobConfig> jobList = fileReader();
            //poolSize set by user
            int poolSize = Integer.parseInt(args[0]);
            //initialize scheduler
            CronScheduler scheduler = new CronScheduler(poolSize);
            //schedule each job
            for(JobConfig jobConfig: jobList){
                //reflection
                Class<?> jobClass = Class.forName(jobConfig.getJobClassName());
                Constructor<?> jobConstructor = jobClass.getDeclaredConstructor();
                Runnable job = (Runnable) jobConstructor.newInstance();
                //begin scheduling
                scheduler.scheduleJob(jobConfig.getJobId(), job, Frequency.valueOf(jobConfig.getFrequency()), jobConfig.getInterval());
            }
        } catch (Exception e) {
            CustomLogger.logError("Error in main method: " + e.getMessage());
        }
    }

    public static ArrayList<JobConfig> fileReader(){
        String configFile = "./com/example/cronscheduler/config.txt";
        ArrayList<JobConfig> jobList = new ArrayList<>();
        try (FileInputStream input = new FileInputStream(configFile)) {
            Properties properties = new Properties();
            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                String[] values = properties.getProperty(key).split(",");
                String jobClassName = key;
                String jobId = values[0].trim();
                Integer interval = Integer.parseInt(values[1].trim());
                String frequency = values[2].trim();

                // Create JobConfig object
                JobConfig jobConfig = new JobConfig(jobClassName, jobId, interval, frequency);
                jobList.add(jobConfig);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jobList;
    }
}
