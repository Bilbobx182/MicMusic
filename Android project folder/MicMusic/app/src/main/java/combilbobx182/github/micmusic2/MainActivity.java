

/**
Written by: Ciarán O Nualláin - C14474048
Purpose: This will act as the home screen for the app. All other screens will be activated through this one.
Updated: 23rd November 2016
 */


package combilbobx182.github.micmusic2;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity
{

    private boolean sensitivityWarning = true;
    private int volumeSelection;
    String result="";
    Long starttime;
    DBManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar topMenuBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topMenuBar);

        //getting time the app was started.
        starttime=System.currentTimeMillis();

        /*
        REFERENCE: Following code is from:
        http://stackoverflow.com/questions/24971524/set-app-background-to-be-the-same-as-home-screen-wallpaper
         */
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.activity_main);
        ll.setBackground(wallpaperDrawable);
        //end refrence

        db=new DBManager(getApplicationContext());
        try
        {
            db.open();
        }
        catch (Exception ex)
        {
            Log.d("MAINACTIVITY.JAVA","Failed to open db");
        }
        //will be used to go from the welcome screen to the mic recording screen later.
        final Button golisten = (Button) findViewById(R.id.btn1);
        golisten.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                beginListeningClass();
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
                listDisplay();
                return true;

            case R.id.headset:
                Log.d("MainActivity","HEADSET VOLUME");
                setVolumeLowering();
                return true;

            case R.id.stats:
                stats();
                return true;

            default:
                return true;
        }
    }

    public void beginListeningClass()
    {
        if(sensitivityWarning ==true)
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
        //REFERENCE: Figured out how to return contents activities using this
        // http://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
        if (requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                result=data.getStringExtra("result");
                //because we no longer need to warn them to set the sensitivity if result is set.
                if(result != null)
                {
                    sensitivityWarning =false;
                    Log.d("MAINACTIVITY","RESULT :"+result);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {

            }
        }
    }

    public void listDisplay()
    {
        Log.d("MainActivity","I WAS CLICKED");
        Intent ld = new Intent(this, SensitivityListView.class);
        startActivityForResult(ld,1);
    }

    void warning()
    {
        // REFERENCE: Learned how to do an alertDialog initially from.
        // http://stackoverflow.com/questions/26097513/android-simple-alert-dialog
        AlertDialog.Builder popupWarning = new AlertDialog.Builder(MainActivity.this);
        popupWarning.setMessage("You didn't select a sensitivity, go by default?");
        popupWarning.setCancelable(true);

        popupWarning.setPositiveButton("Yes",
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

        popupWarning.setNegativeButton("No",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = popupWarning.create();
        alert11.show();
    }

    void stats()
    {
        AlertDialog.Builder statsAlert = new AlertDialog.Builder(MainActivity.this);
        statsAlert.setMessage("Current uptime is: " + String.valueOf(( System.currentTimeMillis()-starttime ) /1000 ) + " seconds");
        statsAlert.setCancelable(true);

        statsAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        statsAlert.show();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        timeupdate();
        db.close();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            db.open();
            timeupdate();
            Cursor result = db.getStat();
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(result));
        }
        catch (Exception ex)
        {
            Log.d("MAINACTIVITY.JAVA","Failed to open db");
        }
    }

    public void timeupdate()
    {
        try
        {
            db.insertStat(String.valueOf(( System.currentTimeMillis()-starttime ) /1000 ) );
            Log.d("MainActivity","inserted the time lol");
        }
        catch (Exception ex)
        {
            Log.d("MainActivity","Something bad happened while inserting");
        }
    }



    void setVolumeLowering()
    {
        //REFERENCE: Fixed the colouring with the help from here
        //https://blog.andromo.com/2011/fixing-text-colours-on-an-alertdialog-when-using-theme-light/
        final android.app.AlertDialog.Builder alert=new android.app.AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AppTheme));
        //End Reference

        //Reference: Figured out how to do editext alert boxes from here.
        //http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
        final EditText edittext = new EditText(MainActivity.this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_NUMBER);
        alert.setMessage("% it will lower music by");
        alert.setCancelable(true);
        alert.setTitle("Choose a value between 0 - 100");
        alert.setView(edittext);

        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String selection = edittext.getText().toString();
                volumeSelection = Integer.valueOf(selection);
                //Test if it's 0 or not, if it's above 0 that means it's out of range.
                if (volumeSelection < 100)
                {
                    System.out.println(volumeSelection);
                    Log.d("MainActivity",String.valueOf(volumeSelection));
                }
                else
                {
                    setVolumeLowering();
                }
            }
        });

        alert.setNegativeButton("No Option", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }
}