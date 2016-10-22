package combilbobx182.github.micmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{

    public static int ytouch=300;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.startbutton);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        ytouch = ((int)e.getY()- 350); // for some reason it likes to be around 350 above the button, this is a temporary fix.
        System.out.println(ytouch);
        btn.setY(ytouch);
        return(true);
    }
}
