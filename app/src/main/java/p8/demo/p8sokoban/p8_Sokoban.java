package p8.demo.p8sokoban;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

// declaration de notre activity héritée de Activity
public class p8_Sokoban extends Activity
{
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private SokobanView mSokobanView;
    private Handler handler = new Handler();
    private TextView textView5;
    private int points = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent        
        super.onCreate(savedInstanceState);
        // charge le fichier main.xml comme vue de l'activité
        setContentView(R.layout.main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;

                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            //if (progressStatus == 100)
                            //for (int i=0; i<100;i++)
                                Log.i("","finiiiiiiii !!!!!!!!" + progressStatus);


                            //textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });

                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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