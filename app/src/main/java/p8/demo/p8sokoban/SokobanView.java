package p8.demo.p8sokoban;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.Random;

import static p8.demo.p8sokoban.R.id.textView5;

public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    Random r = new Random();
    // Declaration des images
    private Bitmap block;
    private Bitmap diamant;
    private Bitmap bleuciel;
    private Bitmap vert;
    private Bitmap marron;
    private Bitmap jaune;
    private Bitmap cyan;
    private Bitmap violet;
    private Bitmap rose;
    private Bitmap perso;
    private Bitmap vide;
    private Bitmap vide1;
    private Bitmap[] zone = new Bitmap[4];
    private Bitmap up;
    private Bitmap down;
    private Bitmap left;
    private Bitmap right;
    private Bitmap win;

    //private TextView textView5;
    int points = 0;



    // Declaration des objets Ressources et Context permettant d'accéder aux ressources de notre application et de les charger
    private Resources mRes;
    private Context mContext;

    // tableau modelisant la carte du jeu
    int[][] carte;
    int level = 1;


    // ancres pour pouvoir centrer la carte du jeu
    int carteTopAnchor;                   // coordonnées en Y du point d'ancrage de notre carte
    int carteLeftAnchor;                  // coordonnées en X du point d'ancrage de notre carte

    // taille de la carte
    static final int carteWidth = 10;
    static final int carteHeight = 14;
    static int carteTileSize;
    static int unblocked=2;

    // constante modelisant les differentes types de cases

    static final int CST_diamant = 0;

    static final int CST_vert = 1;
    static final int CST_marron = 2;
    static final int CST_bleuciel = 3;
    static final int CST_cyan = 4;
    static final int CST_jaune = 5;
    static final int CST_violet = 6;
    static final int CST_rose = 7;

    static final int CST_blanc = 8;
    static final int CST_gris = 9;
    static final int CST_vid = 10;




    class Color {

        int color_one;
        int row;
        int column;


        Color() {

            color_one = 15;
            row = 15;
            column = 15;


        }

        Color(int a, int y, int x) {

            color_one = a;
            row = y;
            column = x;

        }

    }

    // tableau de reference du terrain
    int[][] ref = {
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},
            {CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris},
            {CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc, CST_gris, CST_blanc},






    };






    /* compteur et max pour animer les zones d'arriv�e des diamants */
    int currentStepZone = 0;
    int maxStepZone = 4;

    // thread utiliser pour animer les zones de depot des diamants
    private boolean in = true;
    private Thread cv_thread;
    SurfaceHolder holder;

    Paint paint;

    /**
     * The constructor called from the main JetBoy activity
     *
     * @param context
     * @param attrs
     */
    public SokobanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        carteTileSize = context.getResources().getDisplayMetrics().widthPixels / 10;

        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext = context;
        mRes = mContext.getResources();
        block = BitmapFactory.decodeResource(mRes, R.drawable.block);
        diamant = BitmapFactory.decodeResource(mRes, R.drawable.blue);
        bleuciel = BitmapFactory.decodeResource(mRes, R.drawable.bleueciel);
        vert = BitmapFactory.decodeResource(mRes, R.drawable.green);
        jaune = BitmapFactory.decodeResource(mRes, R.drawable.yellow);
        cyan = BitmapFactory.decodeResource(mRes, R.drawable.cyan);
        marron = BitmapFactory.decodeResource(mRes, R.drawable.darkgreen);
        rose = BitmapFactory.decodeResource(mRes, R.drawable.rose);
        violet = BitmapFactory.decodeResource(mRes, R.drawable.violet);
        perso = BitmapFactory.decodeResource(mRes, R.drawable.perso);
        zone[0] = BitmapFactory.decodeResource(mRes, R.drawable.zone_01);
        zone[1] = BitmapFactory.decodeResource(mRes, R.drawable.zone_02);
        zone[2] = BitmapFactory.decodeResource(mRes, R.drawable.zone_03);
        zone[3] = BitmapFactory.decodeResource(mRes, R.drawable.zone_04);
        vide = BitmapFactory.decodeResource(mRes, R.drawable.blanc);
        vide1 = BitmapFactory.decodeResource(mRes, R.drawable.gris);
        up = BitmapFactory.decodeResource(mRes, R.drawable.time_progress_bar);
        down = BitmapFactory.decodeResource(mRes, R.drawable.down);
        left = BitmapFactory.decodeResource(mRes, R.drawable.left);
        right = BitmapFactory.decodeResource(mRes, R.drawable.right);
        win = BitmapFactory.decodeResource(mRes, R.drawable.win);

        // initialisation des parmametres du jeu
        initparameters();

        // creation du thread
        cv_thread = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true); // mise au point en mode tactile


    }
    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {

        if (level == 1) {
            for (int i = 0; i < carteHeight; i++) {
                for (int j = 0; j < carteWidth; j++) {
                    carte[i][j] = ref[i][j];
                }
            }
        }
    }

    // initialisation du jeu
    public void initparameters() {
        paint = new Paint();
        paint.setColor(0xff0000);//définr la couleur de la peinture

        paint.setDither(true);//Le dithering affecte la façon dont les couleurs ayant une précision supérieure à celle du périphérique sont sous-échantillonnées.
        paint.setColor(0xFFFFFF00);
        paint.setStyle(Paint.Style.STROKE);//style bordure autour du carré
        paint.setStrokeJoin(Paint.Join.ROUND);//déf. la jointure arrondie
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);//la taille d'un point pixel
        paint.setTextAlign(Paint.Align.LEFT);//Alignement GAUCHE signifie que tout le texte sera dessiné à droite de son origine (c'est-à-dire que l'origine spécifie le bord GAUCHE du texte) et ainsi de suite.
        carte = new int[carteHeight][carteWidth];

        loadlevel();
        initColor();

        carteTopAnchor = (getHeight() - carteHeight * carteTileSize) / 2;

        carteLeftAnchor = (getWidth() - carteWidth * carteTileSize+3) / 2;

        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }

    // dessin des fleches , transfert des bitmaps vers un canvas
    private void paintarrow(Canvas canvas) {
        canvas.drawBitmap(up, (getWidth() - up.getWidth()) / 2, 0, null);

        //canvas.drawBitmap(down, (getWidth() - down.getWidth()) / 2, getHeight() - down.getHeight(), null);
        //canvas.drawBitmap(left, 0, (getHeight() - up.getHeight()) / 2, null);
        //canvas.drawBitmap(right, getWidth() - right.getWidth(), (getHeight() - up.getHeight()) / 2, null);
    }

    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor + 3 * carteTileSize, carteTopAnchor + 4 * carteTileSize, null);

    }

    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        //int value2=r.nextInt(10)
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {


                if (ref[i][j] == CST_blanc) {
                    vide = Bitmap.createScaledBitmap(vide, carteTileSize , carteTileSize, true);
                    canvas.drawBitmap(vide, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (ref[i][j] == CST_gris) {
                    vide1 = Bitmap.createScaledBitmap(vide1, carteTileSize , carteTileSize, true);
                    canvas.drawBitmap(vide1, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
            }
        }
    }

    public void initColor()
    {

        //Initialisation de 15 cases vides
        for(int i = 0 ; i<15 ; i++)
        {
            int k = r.nextInt(carteHeight);
            int l = r.nextInt(carteWidth);
            carte[k][l] = 10;
        }

        //Initialisation du reste avec des couleurs
        for(int i=0 ; i<carteHeight ; i++)
        {
            for(int j=0 ; j<carteWidth ; j++)
            {
                if(carte[i][j] != 10)
                {
                    int color = r.nextInt(8);
                    carte[i][j] = color;
                }
            }
        }
    }
    private void paintcolor(Canvas canvas) {

        if (level == 1) {

            for (int i = 0; i < carteHeight ; i++)
            {
                for (int j = 0; j < carteWidth ; j++) {

                    switch (carte[i][j]) {

                        case CST_diamant:
                            diamant = Bitmap.createScaledBitmap(diamant, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(diamant, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_marron:
                            marron = Bitmap.createScaledBitmap(marron, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(marron, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_bleuciel:
                            bleuciel = Bitmap.createScaledBitmap(bleuciel, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(bleuciel, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_cyan:
                            cyan = Bitmap.createScaledBitmap(cyan, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(cyan, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_vert:
                            vert = Bitmap.createScaledBitmap(vert, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(vert, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_jaune:
                            jaune = Bitmap.createScaledBitmap(jaune, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(jaune, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_rose:
                            rose = Bitmap.createScaledBitmap(rose, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(rose, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_violet:
                            violet = Bitmap.createScaledBitmap(violet, carteTileSize , carteTileSize, true);
                            canvas.drawBitmap(violet, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;
                    }
                }
            }
        }
    }


    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(200,200,200);

            paintcarte(canvas);
            paintcolor(canvas);
        }









    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged "+ width +" - "+ height);//informations sur la taille de la surface

        initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }//log pour prévenir lorsque la surface se créée


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }// log lorsque la surface est détruite

    /**
     * run (run du thread cr��)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on lib�re le canvas
     */
    public void run() {
        Canvas c = null;
        while (in) {
            try {

                cv_thread.sleep(40);
                currentStepZone = (currentStepZone + 1) % maxStepZone;
                try {
                    c = holder.lockCanvas(null);//obtient un canvas pour pouvoir dessiner dans cette surface
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
                in= false;
            }
        }
    }



    public Color [] position_color(int j, int i) {


        int y = j;
        int x = i;

        int a = 0;
        Color color_tab[] = new Color[4];
        for (int z = 0; z < 4; z++) {

            color_tab[z] = new Color();
        }



        /*   Le dernier cas est un cas quelconque dans lequel on ne risque pas de dépasser les limites du tableau si on cherche à droite ou dans une autre direction  */


        if (carte[y][x] == 10) {


            while (x < carteWidth) {


                if (carte[y][x] == 0 || carte[y][x] == 1 || carte[y][x] == 2 || carte[y][x] == 3 || carte[y][x] == 4 || carte[y][x] == 5 || carte[y][x] == 6 || carte[y][x] == 7) {


                    color_tab[a] = new Color(carte[y][x], y, x);

                    a++;
                    break;
                }

                x++;
            }
            x = i;

            while (y < carteHeight) {


                if (carte[y][x] == 0 || carte[y][x] == 1 || carte[y][x] == 2 || carte[y][x] == 3 || carte[y][x] == 4 || carte[y][x] == 5 || carte[y][x] == 6 || carte[y][x] == 7) {


                    color_tab[a] = new Color(carte[y][x], y, x);


                    a++;
                    break;
                }
                y++;
            }
            y = j;


            while (x >= 0) {

                if (carte[y][x] == 0 || carte[y][x] == 1 || carte[y][x] == 2 || carte[y][x] == 3 || carte[y][x] == 4 || carte[y][x] == 5 || carte[y][x] == 6 || carte[y][x] == 7) {


                    color_tab[a] = new Color(carte[y][x], y, x);
                    //carte[y][x]=8;

                    a++;
                    break;
                }
                x--;

            }
            x = i;

            while (y >= 0) {


                if (carte[y][x] == 0 || carte[y][x] == 1 || carte[y][x] == 2 || carte[y][x] == 3 || carte[y][x] == 4 || carte[y][x] == 5 || carte[y][x] == 6 || carte[y][x] == 7) {


                    color_tab[a] = new Color(carte[y][x], y, x);
                    a++;
                    break;
                }

                y--;
            }


        }
        return color_tab;
    }





    public int[] countSameColor(Color c[]) {


        int count[] = {0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 8; j++) {

                if (c[i].color_one == j) {

                    count[j] = count[j] + 1;

                }
            }
        }
        return count;
    }


    public void clearColor(int count[],Color[] c){

        int cleared_color = 0;

        for (int k=0;k<8;k++) {

            if (count[k]>1){
                for (int l=0;l<4;l++){

                    if (c[l].color_one==k){

                        int y=c[l].row;
                        int x=c[l].column;

                        carte[y][x]=10;
                        cleared_color++;
                    }
                }
            }
        }

        if( cleared_color== 2)
            points += 40;
        else if(cleared_color == 3)
            points += 60;
        else if(cleared_color == 4)
            points += 120;


    }




    public void display_points()
    {
        TextView textPoints = (TextView)this.getRootView().findViewById(R.id.textView5);
        textPoints.setText("Points : " + String.valueOf(points));
    }



    public int setunblocked( int []count) {


        for (int k = 0; k < 8; k++) {

            if (count[k] > 1) {
                unblocked = 1;

                return unblocked;
            }
        }

        //Log.i("", "UNBLOCKED= " + unblocked);
        unblocked=0;

        return unblocked;
    }

    public int unblockedValue(){// permet d'arrêter le jeu si il n y'a plus de combinaisons possibles ou s'il n y'a que des cases vides


        for (int j=0;j<carteHeight;j++){
            for (int i=0;i<carteWidth;i++){
                if (carte[j][i]==10){

                    countSameColor(position_color(j,i));
                    unblocked=setunblocked(countSameColor(position_color(j,i)));

                    if(unblocked==1) {

                        //Log.i("", "UNBLOCKED= " + unblocked);

                        return unblocked;

                    }
                }
            }
        }
        if (unblocked!=1) {
            unblocked = 0;
        }



        return unblocked;

    }

    public void blocked_game(){

        unblocked=unblockedValue();

        if(unblocked!=1){

            unblocked=0;
            //Log.i("", "UNBLOCKED= " + unblocked);


        }

    }
    public boolean onTouchEvent (MotionEvent event) {
        Log.i("-> FCT <-", "jj " + event.getX());
        Log.i("-> FCT <-", "jj " + event.getY());

        int x=((int)event.getX()-carteLeftAnchor)/carteTileSize;
        int y=((int)event.getY()-carteTopAnchor)/carteTileSize;

        Log.i("", "matrice x= " + x);
        Log.i("", "matrice y= " + y);

        Log.i("", "matrice x= " + carteTopAnchor);



        if ((y>=0&&y<carteHeight)&&(x>=0&&x<carteWidth)) {
            int []count_color=countSameColor(position_color(y, x));
            clearColor(count_color,position_color(y, x));
        }
        display_points();



        /*if (event.getY() < 50) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_UP, null);
        } else if (event.getY() > getHeight() - 50) {
            if (event.getX() > getWidth() - 50) {
                onKeyDown(KeyEvent.KEYCODE_0, null);
            } else {
                onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, null);
            }
        } else if (event.getX() < 50) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
        } else if (event.getX() > getWidth() - 50) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        } else if (isWon() == true && (event.getX() >= 155 && event.getX() <= 175 && event.getY() >= 255 && event.getY() <= 275)) {

            if (level == 1) {
                level = 2;
                initparameters();
            } else if (level == 2) {
                level = 1;
                initparameters();
            }
        }*/

        return super.onTouchEvent(event);
    }

}
