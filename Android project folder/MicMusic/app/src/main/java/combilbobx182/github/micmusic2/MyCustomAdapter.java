package combilbobx182.github.micmusic2;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomAdapter extends ArrayAdapter<String>
{

    String[] loudlevel =
            {
                    "10","20","30","40","50",
                    "60","70","80","90","100"
            };

    public MyCustomAdapter(Context context, int textViewResourceId, String[] objects)
    {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if(row==null)
        {
            LayoutInflater inflater=LayoutInflater.from(getContext());
            row=inflater.inflate(R.layout.row, parent, false);
        }

        TextView label=(TextView)row.findViewById(R.id.loudlevel);
        label.setText(loudlevel[position]+ "\t"+ "\t");


        return row;
    }
}