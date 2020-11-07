package com.saket.samplejobscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


/**
 * First create a class that extends JobService abstract class.
 * Then you create instance of JobInfo which sets the criteria for your job to run.
 * Then you pass your job to the JobSchedular service using JobSchedular.schedule()
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnStartSchedule = findViewById(R.id.btnStart);
        btnStartSchedule.setOnClickListener(v -> startSchedule());
        Button btnStopSchedule = findViewById(R.id.btnStopSchedule);
        btnStopSchedule.setOnClickListener(v -> stopSchedule());
    }


    private void startSchedule () {
        //ComponentName for JobService
        ComponentName componentName = new ComponentName(this,
                SampleJobService.class);
        //Define Job Info
        JobInfo myjobinfo = new JobInfo.Builder(1234, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) //WIFI
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        int resultcode = jobScheduler.schedule(myjobinfo);
        if (resultcode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled successfully ");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    private void stopSchedule () {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.cancel(1234);
        Log.d(TAG, "Job cancelled ");
    }
}