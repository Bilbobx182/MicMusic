package combilbobx182.github.micmusic2;

import android.content.Context;
import android.content.pm.PackageManager;
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
    protected MediaRecorder mediarec = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detect);
        micstatus = (TextView) findViewById(R.id.micstat);
        boolean mictest = mictest(getApplicationContext());
        //testing to see if the android device has a microphone.
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

    private class BackgroundThread extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute() //Done before
        {
            start();
            micstatus.setText("STARTING");
            super.onPreExecute();
        }

       //The main background thread.
        @Override
        protected String doInBackground(String... params)
        {
            //Runs around making sure the amp is less then a hardcoded number
            //Users will input this later, but for now it's 1500.
            while (amps < 1500)
            {
                //gets the amplitude and stores it in variable
                amps = getAmplitude();
                if (amps != 0)
                {
                    //logs it to console so I can actually see what it is.
                    Log.d("AudioDetect.java", String.valueOf(amps));
                }
            }
            amps = getAmplitude();
            //This gets passed to post execute
            return "DONE";
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            Log.e("PROGRESS UPDATE","Trying to enter onProgressUpdate");

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
    }
}