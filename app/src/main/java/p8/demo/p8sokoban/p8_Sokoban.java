package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import static p8.demo.p8sokoban.SokobanView.unblocked;


// declaration de notre activity héritée de Activity
public class p8_Sokoban extends Activity
{
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private SokobanView mSokobanView;
    private Handler handler = new Handler();
    TextView textView5;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        //initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);

        // charge le fichier main.xml comme vue de l'activité
        setContentView(R.layout.main);

        //Création de la sauvegarde



        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(60000);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 60000) {

                    progressStatus += 100;
                    handler.post(new Runnable() {
                        public void run() {progressBar.setProgress(progressStatus);

                        }
                    });

                    try {
                        // Sleep for 100 milliseconds.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if(unblocked==0||progressStatus==60000 ){
                        Intent otherActivity = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(otherActivity);
                        unblocked=2;
                        finish();

                    }

                }







            }
        }).start();

        // recuperation de la vue une voie cree à partir de son id
        mSokobanView = (SokobanView)findViewById(R.id.SokobanView);
        // rend visible la vue
        mSokobanView.setVisibility(View.VISIBLE);



    }
}