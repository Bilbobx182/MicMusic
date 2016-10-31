package combilbobx182.github.micmusic2;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SensitivityListView extends ListActivity
{
    String[] loudlevel =
            {
                    "10","20","30","40","50",
                    "60","70","80","90","100"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity_list_view);
        setListAdapter(new MyCustomAdapter(SensitivityListView.this,R.layout.row,loudlevel));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",loudlevel[position]);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        //  intent.putExtra("Sensitivity",loudlevel[position]);
        // startActivity(intent);
    }
}