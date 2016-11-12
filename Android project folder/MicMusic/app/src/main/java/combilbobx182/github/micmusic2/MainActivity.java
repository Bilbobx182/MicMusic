package combilbobx182.github.micmusic2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    boolean sensitivitywarning = true;
    String result="";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DBManager db=new DBManager(getApplicationContext());
        try
        {
            db.open();
            boolean res=db.dbtest(getApplicationContext(),"SensitivityListValues.db");
            Log.d("Main",String.valueOf(res));
            if(res == false)
            {
                for(int count=10; count<=100;count+=10)
                {
                    db.insertSensitivity(String.valueOf(count));
                }
            }
        }
        catch (Exception ex)
        {
            Log.d("MAINACTIVITY.JAVA","FAILED INSERT");
        }


        //will be used to go from the welcome screen to the mic recording screen later.
        final Button golisten = (Button) findViewById(R.id.btn1);
        golisten.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                audcall();
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.mic:
                listdisplay();
                return true;
            case R.id.stats:
                return true;
            case R.id.headset:

                return true;
            default:
                return true;
        }
    }

    public void audcall()
    {
        if(sensitivitywarning==true)
        {
            warning();
        }
        else
        {
            Intent ad = new Intent(this, AudioDetect.class);
            ad.putExtra("sensitivity",result);
            startActivity(ad);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                result=data.getStringExtra("result");
                //because we no longer need to warn them to set the sensitivity.
                sensitivitywarning=false;
                Log.d("MAINACTIVITY","RESULT :"+result);
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {

            }
        }
    }

    public void listdisplay()
    {
        Log.d("MainActivity","I WAS CLICKED");
        Intent ld = new Intent(this, SensitivityListView.class);
        startActivityForResult(ld,1);
    }

    void warning()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("You didn't select a sensitivity, go by default?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent ad = new Intent(MainActivity.this, AudioDetect.class);
                        ad.putExtra("sensitivity","50");
                        startActivity(ad);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}