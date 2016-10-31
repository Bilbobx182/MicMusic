package combilbobx182.github.micmusic2;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


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
        startActivityForResult(ld,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                String result=data.getStringExtra("result");
                Log.d("MainActivity",result);
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                Log.d("MainActivity","NO RESULT");
            }
        }
    }
}

