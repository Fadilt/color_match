package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by wanted on 06/12/17.
 */

public class ScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        TextView textPoints = (TextView)findViewById(R.id.textViewPoint);
        SharedPreferences param = getSharedPreferences("saveFile", 0);
        int meilleur_score = param.getInt("meilleur_score", 0);
        textPoints.setText("Best Score: " + String.valueOf(meilleur_score));

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
