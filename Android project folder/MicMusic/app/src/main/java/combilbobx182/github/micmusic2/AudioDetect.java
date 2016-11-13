package combilbobx182.github.micmusic2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class AudioDetect extends AppCompatActivity
{
    static TextView micstatus;
    protected double amps=0;
    double sum =0;
    protected MediaRecorder mediarec = null;
    int MAX_AMP=32767;
    float percent=0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detect);
        micstatus = (TextView) findViewById(R.id.micstat);
        boolean mictest = mictest(getApplicationContext());
        //testing to see if the android device has a microphone.

        Intent myIntent = getIntent(); // gets the previously created intent
        String firstKeyName = myIntent.getStringExtra("sensitivity");
        Log.d("AUDIO",firstKeyName + "VALUE PASSED IN");
        Log.d("AudioDetect.java 1)",firstKeyName);
        Log.d("AudioDetect.java 2)", String.valueOf(firstKeyName) + "/" + String.valueOf(100) + " * " + String.valueOf(32767));
        percent=Integer.valueOf(firstKeyName);
        Log.d("AudioDetect.java","PERCENT1"+percent);

        percent=((percent/100) * MAX_AMP);
        Log.d("AudioDetect.java",""+percent);


        if (mictest == true)
        {
            micstatus.setText("IT FOUND A MIC");
            //Creates a new instance of the BackgroundThread class which is used as an ASYNC class.
            BackgroundThread thread = new BackgroundThread();
            thread.execute("TEST");
        } else
        {
            micstatus.setText("NO MIC");
        }
        //This won't be in production code. It is simply used to see IF the ASYNC class is working and that I can do stuff on the UI thread.
        final Button golisten = (Button) findViewById(R.id.BackBtn);
        golisten.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(AudioDetect.this, "HELLO i work", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static boolean mictest(Context context)
    {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void start()
    {
        //Creates a mediarecorder
        if (mediarec == null)
        {
            mediarec = new MediaRecorder();
            //Sets the source to mic, output to and format to mp4 and aac as they are on most devices so chance are it should work on all.
            mediarec.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediarec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediarec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //So it won't actually output he stuff to any file.
            mediarec.setOutputFile("/dev/null");
            try
            {
                mediarec.prepare();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mediarec.start();
        }
    }

    public void stop()
    {
        try
        {
            //Stops the mediarec and releases assets.
            if (mediarec != null)
            {
                mediarec.stop();
                mediarec.release();
                mediarec = null;
            }
        }catch(RuntimeException stopException)
        {
            //I should probably have an exception error screen. Code for that will eventually go here.
        }
    }
    public double getAmplitude()
    {
        //If statement to avoid breaking stuff.
        //Checks if mediarec is intialized if it is, it will get the amplitude.
        if (mediarec != null)
        {
            return mediarec.getMaxAmplitude();
        }
        else
        {
            return 0;
        }
    }

    private class BackgroundThread extends AsyncTask<String,String, String>
    {
        int count;
        double avg;
        long starttime,currenttime;
        int cur, max,half;
        AudioManager audio;

        void varsetup()
        {
            avg=count=0;
            starttime=currenttime=0;
            micstatus.setText("STARTING");


            //setting up audio manager
            audio = (AudioManager) getSystemService(AUDIO_SERVICE);

            //getting the values of the system volume.
            cur = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            half=Math.round (cur/2);
            max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            //printing it out to make sure it worked. Can be removed later.
            Log.d("MainActivity.java","MAX"+ String.valueOf(max));
            Log.d("MainActivity.java","CUR"+ String.valueOf(cur));
            Log.d("MainActivity.java","HALF VOL"+ String.valueOf(half));
        }

        @Override
        protected void onPreExecute() //Done before
        {
            start();
            varsetup();
            super.onPreExecute();
        }

       //The main background thread.
        double previous=0;
        @Override
        protected String doInBackground(String... params)
        {
            Log.d("AudioDetect.java"," "+percent);
            //Runs around making sure the amp is less then a hardcoded number
            //Users will input this later, but for now it's 1500.
            while (amps < percent)
            {

                amps = getAmplitude();
                //gets the amplitude and stores it in variable
                if (amps > 35)
                {
                    //having data may be useful at some point. I don't know when though.
                    count++;
                    sum =(sum +amps);
                    avg=sum/count;
                    //logs it to console so I can actually see what it is.
                    Log.d("AudioDetect.java", String.valueOf(amps) + "    avg:"+ String.valueOf(avg));
                    if(amps>(previous + (amps/3)))
                    {
                        publishProgress();
                    }
                    previous=amps;
                }
            }
            if(starttime == 0)
            {
                volumedown();
                checktime();
            }
            //This gets passed to post execute
            return "DONE";
        }

        @Override
        protected void onProgressUpdate(String...values)
        {
            super.onProgressUpdate(values);
            Log.e("PROGRESS UPDATE","onProgressUpdate");
            micstatus.setText(String.valueOf(amps) + " amps");
        }
        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result)
        {
            //changes the micstatus to done once it breaks the loop in the above section.
            micstatus.setText("DONE");
            stop();
            super.onPostExecute(result);
            // Do things like hide the progress bar or change a TextView
        }

        void volumedown()
        {
            Log.d("AudioDetect.java", "Current:  " + cur);
            cur = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            half=Math.round (cur/2);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, half, 0);
            Log.d("AudioDetect.java", "Volume DOWN TO:  " + half);
        }

        void checktime()
        {
            currenttime=starttime=System.currentTimeMillis();
            Log.d("Audiodetect","loop");
            //Loop to check if 15 sec has passed Gonna make this selectable from a list later
            while(currenttime<starttime+8000)
            {
                //keeps checking until the 15 sec is over
                currenttime=System.currentTimeMillis();
            }
            //raises volume back up after the time is done
            Log.d("AudioDetect.java", "Back to:  " + cur);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,cur, 0);
        }
    }
}