

/*
Written by: Ciarán O Nualláin - C14474048
Purpose: Class to deal with the audio Screen, sub class to deal with audio input processing.
Updated: 23rd November 2016
 */

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
    final int MAX_AMP=32767;
    float percent=0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detect);
        micstatus = (TextView) findViewById(R.id.micstat);
        boolean micTest = mictest(getApplicationContext());


        Intent intentFromMain = getIntent();
        String senisitivityKey = intentFromMain.getStringExtra("sensitivity");

        percent=Integer.valueOf(senisitivityKey);

        //calculating what the value passed in is in percent of overall volume.
        percent=((percent/100) * MAX_AMP);


        if (micTest == true)
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
                Toast.makeText(AudioDetect.this, "I'M HELPING", Toast.LENGTH_LONG).show();
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
        int totalAmpCalls;
        double avg, previous;
        long starttime,currenttime;
        int cur, max,half;
        AudioManager audio;

        void varsetup()
        {
            previous=avg= totalAmpCalls =0;
            starttime=currenttime=0;
            micstatus.setText("STARTING");
            //setting up audio manager
            audio = (AudioManager) getSystemService(AUDIO_SERVICE);
            //getting the values of the system volume.
            cur = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            half=Math.round (cur/2);
            max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        @Override
        protected void onPreExecute() //Done before
        {
            start();
            varsetup();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            //Runs around making sure the amp is less then a hardcoded number
            //Users will input this later, but for now it's 1500.
            while (amps < percent)
            {
                amps = getAmplitude();
                //gets the amplitude and stores it in variable
                if (amps > 35)
                {
                    //having data may be useful at some point. I don't know when though.
                    totalAmpCalls++;
                    sum =(sum +amps);
                    avg=sum/ totalAmpCalls;

                    //logs it to console so I can actually see what it is.
                    Log.d("AudioDetect.java", String.valueOf(amps) + "    avg:"+ String.valueOf(avg));
                    if(amps>(previous + (amps/3)))
                    {
                        publishProgress();
                    }
                    previous=amps;
                }
            }
            //turns the volume down, then loops checking to see if time is up
            volumeDown();
            checkTime();
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

        void volumeDown()
        {
            cur = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            half=Math.round (cur/2);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, half, 0);
            Log.d("AudioDetect.java", "Volume DOWN TO:  " + half);
        }

        void checkTime()
        {
            currenttime=starttime=System.currentTimeMillis();

            //Loop to check if 15 sec has passed Gonna make this selectable from a list later
            while(currenttime<starttime+8000)
            {
                //keeps checking until the 15 sec is over
                currenttime=System.currentTimeMillis();
            }
            //raises volume back up after the time is done
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,cur, 0);
        }
    }
}

/*
Useful comments and logs for testing:
       //Log.d("AUDIO",senisitivityKey + "VALUE PASSED IN");

        Log.d("AudioDetect.java 1)",senisitivityKey);
        Log.d("AudioDetect.java 2)", String.valueOf(senisitivityKey) + "/" + String.valueOf(100) + " * " + String.valueOf(32767));

        Log.d("AudioDetect.java",""+percent);

          Log.d("MainActivity.java","MAX"+ String.valueOf(max));
            Log.d("MainActivity.java","CUR"+ String.valueOf(cur));
            Log.d("MainActivity.java","HALF VOL"+ String.valueOf(half));


*/