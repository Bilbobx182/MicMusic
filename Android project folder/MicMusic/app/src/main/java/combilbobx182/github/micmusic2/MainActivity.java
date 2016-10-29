package combilbobx182.github.micmusic2;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //will be used to go from the welcome screen to the mic recording screen later.
        //will rename btn1 later.
        final Button golisten= (Button) findViewById(R.id.btn1);
        golisten.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                audcall();
            }
        });

        //Test Code for Android System Volume. Basically printing stuff out. nothing important yet.
        final Button vol_btn= (Button) findViewById(R.id.vol_btn);
        vol_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                float cur, max,half;
                AudioManager audioManager;
                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                cur = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                half=Math.round (cur/2);
                max = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                Log.d("MainActivity.java","MAX"+ String.valueOf(max));
                Log.d("MainActivity.java","CUR"+ String.valueOf(cur));
                Log.d("MainActivity.java","HALF VOL"+ String.valueOf(half));
                TextView vol = (TextView) findViewById(R.id.volumestats);
                vol.setText("HALF SYSTEM VOLUME IS:    " + String.valueOf(half));
            }
        });
    }

    //calls the audioDetection class.
    public void audcall()
    {
        Intent ad = new Intent(this,AudioDetect.class);
        startActivity(ad);
    }
}
