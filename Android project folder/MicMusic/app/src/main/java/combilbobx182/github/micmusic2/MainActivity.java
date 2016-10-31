package combilbobx182.github.micmusic2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //will be used to go from the welcome screen to the mic recording screen later.
        //will rename btn1 later.
        final Button golisten = (Button) findViewById(R.id.btn1);
        golisten.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                audcall();
            }
        });
        final Button lv = (Button) findViewById(R.id.ListID);
        lv.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                listdisplay();
            }
        });
    }

    public void audcall()
    {
        Intent ad = new Intent(this, AudioDetect.class);
        startActivity(ad);
    }
    public void listdisplay()
    {
        Log.d("MainActivity","I WAS CLICKED");
        Intent ld = new Intent(this, SensitivityListView.class);
        startActivity(ld);

    }
}
