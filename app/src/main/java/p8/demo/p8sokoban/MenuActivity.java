package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wanted on 04/12/17.
 */



public class MenuActivity extends Activity  {
    int points=0;
    private ImageView imageView;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.imageView3 = (ImageView) findViewById(R.id.imageView3);
        this.imageView4 = (ImageView) findViewById(R.id.imageView4);
        this.imageView6 = (ImageView) findViewById(R.id.imageView6);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent otherActivity = new Intent(getApplicationContext(), p8_Sokoban.class);
                startActivity(otherActivity);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent otherActivity = new Intent(getApplicationContext(), ScoreActivity.class);
                startActivity(otherActivity);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent otherActivity = new Intent(getApplicationContext(), MoreActivity.class);
                startActivity(otherActivity);
            }
        });


        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               finish();
                System.exit(0);
            }
        });



    }



    public void onPause()
    {
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

}
