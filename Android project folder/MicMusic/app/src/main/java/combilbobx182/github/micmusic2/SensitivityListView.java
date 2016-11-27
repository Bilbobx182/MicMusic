

/**
Written by: Ciarán O Nualláin - C14474048
Purpose: Act as the list view for the sensitivity screen.
Updated: 23rd November 2016
 */

package combilbobx182.github.micmusic2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
public class SensitivityListView extends ListActivity
{
    DBManager db = new DBManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity_list_view);
        final Button btn = (Button) findViewById(R.id.valueadder);

        ListView sensitivityList;
        sensitivityList = (ListView) findViewById(android.R.id.list);

        try
        {
            //opens the database.
            db.open();
            //returns a cursor with all the information about sensitivity
            Cursor result = db.getAll();
            //Goes to the cursor adapter.
            MyCursorAdapter cursorAdapter = new MyCursorAdapter(SensitivityListView.this, result);
            sensitivityList.setAdapter(cursorAdapter);
            db.close();
        }
        catch (Exception ex)
        {
            Toast toast = Toast.makeText(this,"OOPS something went wrong",Toast.LENGTH_LONG);
            toast.show();
        }

        /*
        Reference: Following code from:
        https://blog.andromo.com/2011/fixing-text-colours-on-an-alertdialog-when-using-theme-light/
         */
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(SensitivityListView.this,R.style.AppTheme));
        //Reference complete.
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                final EditText edittext = new EditText(SensitivityListView.this);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_NUMBER);
                alert.setMessage("Enter your sensitivity");
                alert.setTitle("Choose a value between 10 - 100");

                alert.setView(edittext);

                alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        String selection = edittext.getText().toString();
                        try
                        {
                            db.open();
                            db.insertSensitivity(selection);
                            Toast.makeText(SensitivityListView.this, "Value: " + selection + " was inserted.", Toast.LENGTH_LONG).show();
                            refresh();
                        }
                        catch (Exception ex)
                        {
                            Log.d("SensitvityLV","OPEN FAILED");
                        }
                    }
                });

                alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });
                alert.show();
            }
        });

        sensitivityList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> av, View view, int position, long arg)
            {
                Cursor mycursor = (Cursor) av.getItemAtPosition(position);
                //Gets the location of the row that was selected and stores in selection.
                String selection = mycursor.getString(1);
                /*
                converts the value selected in the ordered sensitivity list 0-10
                Multiplies it by 10 so we have it on a scale of 10-100
                Then converts it back to a string so it can be passed around easier.
                 */
                String res=String.valueOf(((Integer.parseInt(selection))));

                //Used to return it to the main Activity that acts as a controller.
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",res);
                setResult(Activity.RESULT_OK, returnIntent);
                //finishes the instance.
                finish();
            }
        });

        sensitivityList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> av2, View arg1,int pos, long id)
            {
                Cursor mycursor = (Cursor) av2.getItemAtPosition(pos);
                String selection = mycursor.getString(1);
                Log.d("SELECTED",selection);
                deleteItem(selection);
                return true;
            }
        });
    }

    void deleteItem(String item)
    {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this);
        final String valuetodelete=item;
        builder1.setMessage("Do you really want to delete value: " + valuetodelete);
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        DBManager db=new DBManager(getApplicationContext());
                        try
                        {
                            db.open();
                            db.deleteSen(valuetodelete);
                            Toast.makeText(SensitivityListView.this, "Value: " + valuetodelete + " was deleted.", Toast.LENGTH_LONG).show();
                            refresh();
                        }
                        catch (Exception ex)
                        {
                            Log.d("SensitvityLV","OPEN FAILED");
                        }
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

        android.support.v7.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void refresh()
    {
        SensitivityListView.this.finish();
        Intent refresh=new Intent(this,SensitivityListView.class);
        startActivity(refresh);
    }
}
