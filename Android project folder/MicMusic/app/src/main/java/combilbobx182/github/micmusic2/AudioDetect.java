package combilbobx182.github.micmusic2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class AudioDetect extends AppCompatActivity
{

    static TextView micstatus;
    protected double amps=0;
    protected MediaRecorder mRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detect);
        boolean mictest=hasMicrophone(getApplicationContext());

        micstatus=(TextView)findViewById(R.id.micstat);
        if(mictest==true)
        {
            micstatus.setText("IT FOUND A MIC");
            start();
            while (amps < 1500)
            {
                amps=getAmplitude();
                micstatus.setText(String.valueOf(amps));
                if(amps != 0)
                {
                    Log.d("AudioDetect.java", String.valueOf(amps));
                }
            }
            stop();
        }
        else
        {
            micstatus.setText("NO MIC");
        }

    }
    public static boolean hasMicrophone(Context context)
    {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void start()
    {
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mRecorder.start();
        }
    }

    public void stop()
    {
        if (mRecorder != null)
        {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }
    public double getAmplitude()
    {
        if (mRecorder != null)
        {
            return mRecorder.getMaxAmplitude();
        }
        else
        {
            return 0;
        }
    }
}
