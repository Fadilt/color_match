package p8.demo.p8sokoban;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // Declaration des images
    private Bitmap diamant;
    private Bitmap bleuciel;
    private Bitmap vert;
    private Bitmap marron;
    private Bitmap jaune;
    private Bitmap cyan;
    private Bitmap violet;
    private Bitmap rose;
    private Bitmap      vide;
    private Bitmap      vide1;
    private Bitmap      up;
    private Bitmap      down;
    private Bitmap      left;
    private Bitmap      right;
    private Bitmap      win;

    // Declaration des objets Ressources et Context permettant d'accéder aux ressources de notre application et de les charger
    private Resources   mRes;
    private Context     mContext;

    // tableau modelisant la carte du jeu
    int[][] carte;
    int level = 1;



    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonnées en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonnées en X du point d'ancrage de notre carte

    // taille de la carte
    static final int    carteWidth    = 10;
    static final int    carteHeight   = 10;
    static final int    carteTileSize = 20;

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



    class Color {//tab

        int color_one;
        int row;
        int column;


        Color() {

            color_one = 10;
            row = 10;
            column = 10;


        }


        Color(int a ,int y,int x){

            color_one=a;
            row=y;
            column=x;

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
    };










    /* compteur et max pour animer les zones d'arriv�e des diamants */
    int currentStepZone = 0;
    int maxStepZone     = 4;

    // thread utiliser pour animer les zones de depot des diamants
    private     boolean in      = true;
    private     Thread  cv_thread;
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


        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext = context;
        mRes = mContext.getResources();
        diamant = BitmapFactory.decodeResource(mRes, R.drawable.blue);
        bleuciel = BitmapFactory.decodeResource(mRes, R.drawable.bleueciel);
        vert = BitmapFactory.decodeResource(mRes, R.drawable.green);
        jaune = BitmapFactory.decodeResource(mRes, R.drawable.yellow);
        cyan = BitmapFactory.decodeResource(mRes, R.drawable.cyan);
        marron = BitmapFactory.decodeResource(mRes, R.drawable.darkgreen);
        rose = BitmapFactory.decodeResource(mRes, R.drawable.rose);
        violet = BitmapFactory.decodeResource(mRes, R.drawable.violet);
        vide = BitmapFactory.decodeResource(mRes, R.drawable.blanc);
        vide1 = BitmapFactory.decodeResource(mRes, R.drawable.gris);
        up = BitmapFactory.decodeResource(mRes, R.drawable.up);
        down = BitmapFactory.decodeResource(mRes, R.drawable.down);
        left = BitmapFactory.decodeResource(mRes, R.drawable.left);
        right = BitmapFactory.decodeResource(mRes, R.drawable.right);
        win = BitmapFactory.decodeResource(mRes, R.drawable.win);

        // initialisation des parmametres du jeu
        initparameters();

        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true); // mise au point en mode tactile


    }

    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {

        if (level == 1) {
            for (int i = 0; i < carteHeight; i++) {
                for (int j = 0; j < carteWidth; j++) {
                    carte[j][i] = ref[j][i];
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
        carte           = new int[carteHeight][carteWidth];

        loadlevel();
        initColor();
        
        carteTopAnchor  = (getHeight()- carteHeight*carteTileSize)/2;

        carteLeftAnchor = (getWidth()- carteWidth*carteTileSize)/2;


        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }

    // dessin des fleches , transfert des bitmaps vers un canvas
    private void paintarrow(Canvas canvas) {
        canvas.drawBitmap(up, (getWidth()-up.getWidth())/2, 0, null);
        //canvas.drawBitmap(down, (getWidth()-down.getWidth())/2, getHeight()-down.getHeight(), null);
        //canvas.drawBitmap(left, 0, (getHeight()-up.getHeight())/2, null);
        //canvas.drawBitmap(right, getWidth()-right.getWidth(), (getHeight()-up.getHeight())/2, null);
    }

    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor+ 3*carteTileSize, carteTopAnchor+ 4*carteTileSize, null);

    }

    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        //int value2=r.nextInt(10)
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {


                if (ref[i][j] == CST_blanc) {
                    canvas.drawBitmap(vide, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (ref[i][j] == CST_gris) {
                    canvas.drawBitmap(vide1, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
            }
        }

       /* for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                if (carte[i][j] == 6 || carte[i][j] == 7) {
                    int value = r.nextInt(5);
                    carte[i][j] = value;
                    Log.i("LE RANDOOOOOM", "ESSST:" + value);
                    //canvas.drawBitmap(vert,carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                }
                if (carte[i][j] == CST_diamant) {
                    canvas.drawBitmap(diamant, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (carte[i][j] == CST_magenta) {
                    canvas.drawBitmap(magenta, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (carte[i][j] == CST_cyan) {
                    canvas.drawBitmap(cyan, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (carte[i][j] == CST_vert) {
                    canvas.drawBitmap(vert, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (carte[i][j] == CST_jaune) {
                    canvas.drawBitmap(jaune, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                if (carte[i][j] == CST_rouge) {
                    canvas.drawBitmap(rouge, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                }
                /*switch (carte[i][j]) {
                    case CST_block:
                        canvas.drawBitmap(block, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;                    
                    case CST_zone:
                        canvas.drawBitmap(zone[currentStepZone],carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_blanc:
                        canvas.drawBitmap(vide,carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_gris:
                        canvas.drawBitmap(vide1,carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                }
            }
        }*/
    }


    public void initColor()
    {

        Random r= new Random();
        //Initialisation de 10 cases vides
        for(int i = 0 ; i<10 ; i++)
        {
            int k = r.nextInt(carteHeight);
            int l = r.nextInt(carteWidth);
            carte[k][l] = 8;
        }

        //Initialisation du reste avec des couleurs
        for(int i=0 ; i<carteHeight ; i++)
        {
            for(int j=0 ; j<carteWidth ; j++)
            {
                if(carte[i][j] != 8)
                {
                    int color = r.nextInt(6);
                    carte[i][j] = color;
                }
            }
        }
    }



    // dessin des couleurs
    private void paintcolor(Canvas canvas) {
        if (level == 1) {

            for (int i = 0; i < carteHeight ; i++)
            {
                for (int j = 0; j < carteWidth ; j++) {

                    switch (carte[i][j]) {


                        case CST_diamant:
                            canvas.drawBitmap(diamant, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_marron:
                            canvas.drawBitmap(marron, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_bleuciel:
                            canvas.drawBitmap(bleuciel, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_cyan:
                            canvas.drawBitmap(cyan, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_vert:
                            canvas.drawBitmap(vert, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_jaune:
                            canvas.drawBitmap(jaune, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_rose:
                            canvas.drawBitmap(rose, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;

                        case CST_violet:
                            canvas.drawBitmap(violet, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                            break;
                    }
                }

            }
        }
    }
   



    // permet d'identifier si la partie est gagnee (tous les diamants à leur place)
    /*private boolean isWon() {
        if (level == 1) {
            for (int i = 0; i < 4; i++) {
                if (!IsCell(diamants[i][1], diamants[i][0], CST_zone)) {
                    return false;
                }
            }
        }
        return true;
    }*/

    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(44,44,44);
       /* if (isWon()) {
            paintcarte(canvas);
            paintwin(canvas);
            //initparameters();
        } else*/
            paintcarte(canvas);
            paintcolor(canvas);            
            //paintPlayer(canvas);
            //paintdiamants(canvas);
            //paintarrow(canvas);


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
                Thread.sleep(40);
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

    // verification que nous sommes dans le tableau
    private boolean IsOut(int x, int y) {
        if ((x < 0) || (x > carteWidth- 1)) {
            return true;
        }
        return (y < 0) || (y > carteHeight - 1);
    }

    //controle de la valeur d'une cellule
    private boolean IsCell(int x, int y, int mask) {
        return carte[y][x] == mask;
    }



   /* public void FoundColour(int y,int x){
        int i=x;
        if (i==0){
            while (i<carteWidth){
                if()
            }
        }
    }*/
    
    
    
    public void position_color(int j,int i){
        
        
        int y=j;
        int x=i;
        
        int a=0;
        Color color_tab[]=new Color[4];
        for (int z=0;z<4;z++) {
            
            color_tab[x] = new Color();
        }
        
        
        if (y==0 && x==0){  // si la case sur laquelle on appuie est celle du coin gauche haut du tableau
            
            while(x<carteWidth){
                
                if(carte[y][x]==1 || carte[y][x]==2 || carte[y][x] == 3 || carte[y][x] == 4 ){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                
                x++;
                
            }
            
            x=i;
            
            while(y<carteHeight){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                
                y++;
                
            }
            
            y=j;
            
        }
        
        
        
        else if (y==0 && x==carteWidth){ // si la case sur laquelle on appuie est celle du coin droit haut du tableau
            
            while(x>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                
                x--;
                
            }
            
            x=i;
            
            while(y<carteHeight){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                
                y++;
                
            }
            y=j;
        }
        /*  YxyxyxyxyxyxyxyxyxyxyxyxyxyxyxxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxxyxyxyxyxyxxyxyX  */
        
        else if (y==carteHeight && x==0){ // si la case sur laquelle on appuie est celle du coin gauche bas du tableau
            
            while(x<carteWidth){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                x++;
            }
            x=i;
            
            while(y>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                
                y--;
                
            }
            
            y=j;
        }
        
        
        else if (y==carteHeight && x==carteWidth){ //si la case sur laquelle on appuie est celle du coin droite bas du tableau
            
            while(x>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    
                    break;
                }
                
                x--;
                
            }
            x=i;
            
            while(y>0){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    a++;
                    
                    break;
                }
                
                y--;
                
            }
            y=j;
            
        }
        
        
        /* yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy*/
        
        else if (y==0){ // verifier si la hauteur est au niveau de la case la plus en haut
            
            
            while(x<carteWidth){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                x++;
                
            }
            
            x=i;
            
            while(y<carteHeight){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y++;
                
            }
            y=j;
            
            while(x>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    
                    break;
                }
                
                x--;
                
            }
            x=i;
            
        }
        
        else if (y==carteHeight){ // verifier si le max de HEIGHT est au niveau de la case la plus en bas
            
            
            while(x<carteWidth){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                x++;
                
            }
            
            x=i;
            
            while(y>0){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y--;
                
            }
            y=j;
            
            while(x>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                x--;
                
            }
            
            x=i;
            
            
        }
        
        /* xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx*/
        else if (x==0){
            
            
            
            while(y>0){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y--;
            }
            y=j;
            
            while(x<carteWidth){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                x++;
                
            }
            
            x=i;
            
            
            while(y<carteHeight){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y++;
                
            }
            y=j;
        }
        
        
        else if (x==carteWidth){
            
            
            while(y>0){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y--;
            }
            y=j;
            
            while(x>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                x--;
                
            }
            x=i;
            
            
            
            while(y<carteHeight){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y++;
                
            }
            
            y=j;
        }
        
        
        /*   Le dernier cas est un cas quelconque dans lequel on ne risque pas de dépasser les limites du tableau si on cherche à droite ou dans une autre direction  */
        
        
        
        else {
            
            
            
            while(x<carteWidth){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                x++;
            }
            x=i;
            
            while(y<carteHeight){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                y++;
            }
            y=j;
            
            
            
            while(x>0){
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                x--;
                
            }
            x=i;
            
            while(y>0){
                
                
                if(carte[y][x]==1||carte[y][x]==2||carte[y][x]==3||carte[y][x]==4){
                    
                    
                    
                    
                    color_tab[a]=new Color(carte[y][x],y,x);
                    
                    a++;
                    break;
                }
                
                y--;
            }
            y=j;
            
        }
        
        
        
    }
    



    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {
        Log.i("-> FCT <-", "onTouchEvent: " + event.getX());
        Log.i("-> FCT <-", "onTouchEvent: " + event.getY());

        /*int x=(int)event.getX()/20;
        int y=((int)event.getY()-carteTopAnchor)/20;*/

        if (event.getY() < 50) {
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
        } /*else if (isWon() == true && (event.getX() >= 155 && event.getX() <= 175 && event.getY() >= 255 && event.getY() <= 275)) {
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

