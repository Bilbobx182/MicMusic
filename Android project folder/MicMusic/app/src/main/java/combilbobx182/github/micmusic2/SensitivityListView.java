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

        ListView listView;
        listView = (ListView) findViewById(android.R.id.list);

        try
        {
            //opens the database.
            db.open();
            //returns a cursor with all the information about sensitivity
            Cursor result = db.getAll();
            //Goes to the cursor adapter.
            MyCursorAdapter cursorAdapter = new MyCursorAdapter(SensitivityListView.this, result);
            listView.setAdapter(cursorAdapter);
            db.close();
        }
        catch (Exception ex)
        {
            Toast toast = Toast.makeText(this,"OOPS something went wrong",Toast.LENGTH_LONG);
            toast.show();
        }

        /* Commented out because I don't have a button with the db listview */
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(SensitivityListView.this,R.style.AppTheme));
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
                        String YouEditTextValue = edittext.getText().toString();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> av, View view, int position, long arg)
            {
                Cursor mycursor = (Cursor) av.getItemAtPosition(position);
                //Gets the location of the row that was selected and stores in selection.
                String selection = mycursor.getString(0);
                /*
                converts the value selected in the ordered sensitivity list 0-10
                Multiplies it by 10 so we have it on a scale of 10-100
                Then converts it back to a string so it can be passed around easier.
                 */
                String res=String.valueOf((10 * (Integer.parseInt(selection))));

                //Used to return it to the main Activity that acts as a controller.
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",res);
                setResult(Activity.RESULT_OK, returnIntent);
                //finishes the instance.
                finish();
            }
        });


    }
}
