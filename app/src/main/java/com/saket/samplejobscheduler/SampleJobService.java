package com.saket.samplejobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

/**
 * Created by sshriwas on 2019-12-31
 *
 * Note that this service runs on the main thread. So if you
 * want to perform a long running background task, then run it on a
 * separate thread.
 *
 * The system provides a wake lock to the JobService until it returns a false from onStartJob.
 * If you want to run a long background task and need to keep the wake lock longer
 * then onStartJob should return true. But in this case its your responsibility to
 * invoke jobFinished() to indicate that the task is complete and allow the system to release
 * the wake lock.
 *
 * The system may still stop the service and release its wake lock in the event if any of the
 * JobInfo criteria is no longer valid. For eg. if user un-plugs the device from charging. In
 * this event, the onStopJob() method is invoked.
 *
 * onStopJob method returns false if the job does not need to be re-run. Or it can return true if
 * the job has to be re-run.
 *
 * Observations -
 * Thread.start() creates a separate thread to perform the operation. Thread.run() basically
 * runs on the same thread.
 *
 * if onStartJob() returns true and jobScheduler.cancel() is called from MainActivity, then onStopJob()
 * gets invoked. However, if onStartJob() returns false and jobSchedular.cancel() is called,
 * then onStopJob() is not invoked.
 *
 * Returning false from onStartJob() means your job is already finished. The
 * system's wakelock for the job will be released, and onStopJob(JobParameters) will not be invoked.
 */
public class SampleJobService extends JobService {
    private static final String TAG = SampleJobService.class.getSimpleName() ;
    boolean isJobCancelled = false;


    /**
     *
     * @param jobParameters Parameters specifying info about this job, including the optional
     *      extras configured with JobInfo.Builder#setExtras(android.os.PersistableBundle.
     *      This object serves to identify this specific running job instance when calling
     *      {@link #jobFinished(JobParameters, boolean)}.
     * @return {@code true} if your service will continue running, using a separate thread
     *      when appropriate.  {@code false} means that this job has completed its work.
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.v(TAG, "onStartJob");
        someBackgroundTask(jobParameters);
        return true;
    }


    private void someBackgroundTask(JobParameters parameters) {
        new Thread(() -> {

            for (int i = 0; i< 10 ; i++ ) {
                Log.v(TAG, "i = " + i);
                //If job is cancelled then exit this method...
                if (isJobCancelled) {
                    return;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Tell system that job is finished..
            jobFinished(parameters, false);
            Log.d(TAG, "Job Finished");
        }).start();
    }

    /**
     * This method is called if the system has determined that you must stop execution of your job
     * even before you've had a chance to call {@link #jobFinished(JobParameters, boolean)}.
     *
     * <p>This will happen if the requirements specified at schedule time are no longer met. For
     * example you may have requested WiFi with
     * {@link android.app.job.JobInfo.Builder#setRequiredNetworkType(int)}, yet while your
     * job was executing the user toggled WiFi. Another example is if you had specified
     * {@link android.app.job.JobInfo.Builder#setRequiresDeviceIdle(boolean)}, and the phone left its
     * idle maintenance window. You are solely responsible for the behavior of your application
     * upon receipt of this message; your app will likely start to misbehave if you ignore it.
     * <p>
     * Once this method returns, the system releases the wakelock that it is holding on
     * behalf of the job.</p>
     *
     * @param jobParameters The parameters identifying this job, as supplied to
     *               the job in the {@link #onStartJob(JobParameters)} callback.
     * @return {@code true} to indicate to the JobManager whether you'd like to reschedule
     * this job based on the retry criteria provided at job creation-time; or {@code false}
     * to end the job entirely.  Regardless of the value returned, your job must stop executing.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        isJobCancelled = true;
        Log.d(TAG, "onStopJob: Job stopped before completion");
        //return true if you want the jobScheduler to reschedule the job
        //after it has been stopped/cancelled
        return true;
    }
}