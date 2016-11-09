package combilbobx182.github.micmusic2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SensitivityListView extends ListActivity {
    String[] loudlevel =
            {
                    "10", "20", "30", "40", "50",
                    "60", "70", "80", "90", "100"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity_list_view);


        ListView listView;
        LayoutInflater layoutinflater;
        listView = (ListView) findViewById(android.R.id.list);

        layoutinflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup) layoutinflater.inflate(R.layout.listview_footer, listView, false);
        listView.addFooterView(footer);

        setListAdapter(new MyCustomAdapter(SensitivityListView.this, R.layout.row, loudlevel));



        final Button btn = (Button) findViewById(R.id.button);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                final EditText edittext = new EditText(getApplicationContext());
                alert.setMessage("Enter Your Message");
                alert.setTitle("Enter Your Title");

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
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", loudlevel[position]);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        //  intent.putExtra("Sensitivity",loudlevel[position]);
        // startActivity(intent);
    }

}
