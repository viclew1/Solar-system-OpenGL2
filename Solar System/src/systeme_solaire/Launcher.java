package systeme_solaire;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;




public class Launcher extends JFrame implements GLEventListener, KeyListener,MouseListener,MouseWheelListener{

	private static final long serialVersionUID = 1L;
	private GLU glu=new GLU();
	private int count=0;
	private ArrayList<Planete> planetes=new ArrayList<Planete>();
	private float	eyeX	=1200 ;
	private float	eyeY	=1200 ;
	private	float	eyeZ	=600;
	private	float	POV_orientation	=0;
	private float 	POV_orientation_y=0;
	private	float	POV_speed	=10f	;
	private	float	POV_rotation_speed	=1f	;
	private	float	lx	= 	0;
	private float 	ly  =	0;
	private	float	lz	=	0;
	private int planetWatched=-1;
	private float d_to_planet_watched=1;

	private boolean following=false;
	private int[] textFond=new int[6];



	static Robot r;

	public static float vitesse_systeme=0.5f;


	private boolean[] keys=new boolean[20];
	private int echelleRotation=100,echelleRevolution=1000;

	//Screen size
	public static GraphicsEnvironment graphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static Rectangle RRR=graphicsEnvironment.getMaximumWindowBounds();
	public static double RRRw=RRR.getWidth();
	public static double RRRh=RRR.getHeight();
	public static boolean showOrbits=true;


	public Launcher(int width, int height)
	{
		super( "MainFrame" ) ;
		GLProfile profil = GLProfile.get ( GLProfile.GL2) ;
		GLCapabilities capabilities = new GLCapabilities ( profil ) ;
		GLCanvas canvas = new GLCanvas ( capabilities ) ;
		canvas.addGLEventListener( this ) ;
		canvas.setSize (width , height ) ;

		this.getContentPane().add( canvas ) ;
		this.setSize ( this.getContentPane() . getPreferredSize ( ) ) ;
		this.setLocationRelativeTo ( null ) ;
		this.setDefaultCloseOperation (JFrame .EXIT_ON_CLOSE) ;
		this.setVisible ( true ) ;
		this.setResizable ( false ) ;
		this.setFocusable(true);
		canvas .requestFocusInWindow ( ) ;
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);
		final FPSAnimator animator = new FPSAnimator( canvas , 100, true ) ;
		animator.start();

	}

	/**
	 * Définit la vue affichée, et permet de suivre une planète
	 * @author Victor Lewon
	 */
	public void setView()
	{
		if (following)
		{
			//Si on suit une planète, on place eyeX et eyeZ à ses coordonées et eyeY plus haut.
			eyeY=planetes.get(planetWatched).r*15*d_to_planet_watched;
			eyeX=-(float) ((planetes.get(planetWatched).d_Soleil)*Math.cos(Math.toRadians(planetes.get(planetWatched).rot_p_soleil)));
			eyeZ=(float) ((planetes.get(planetWatched).d_Soleil)*Math.sin(Math.toRadians(planetes.get(planetWatched).rot_p_soleil)));

			ly=-10;
		}
		glu . gluLookAt(eyeX , eyeY , eyeZ , eyeX+lx ,	eyeY+ly , eyeZ+lz ,	0, 1, 0) ;
	}

	/**
	 * @author Victor Lewon et Maximilien Therras
	 */
	@Override
	public void display(GLAutoDrawable drawable) {

		moveCamera();

		GL2 gl = drawable.getGL().getGL2() ;
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT) ;

		gl . glLoadIdentity () ;


		dessinerFond(gl);
		//dessinerCeinture(gl,10,200,16,100);
		setLight(gl);

		for(Iterator<Planete> it=planetes.iterator();it.hasNext();){
			Planete p= it.next();
			gl.glLoadIdentity();
			p.affiche_planete(gl,glu);
			for(Iterator<Lune> it2=p.lunes.iterator();it2.hasNext();){
				Lune l=it2.next();
				gl.glLoadIdentity();
				l.affiche_planete(gl,glu);
			}
		}

		gl . glMatrixMode( GL2.GL_PROJECTION ) ;
		gl . glLoadIdentity () ;
		glu . gluPerspective ( 45.0f ,	1, 1.0 , 100000	) ;

		setView();
		gl . glMatrixMode( GL2.GL_MODELVIEW ) ; 


		mouseMvt();

		gl.glFlush ( ) ;

	}

	/**
	 * @author Victor Lewon et Maximilien Therras
	 */
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @author Victor Lewon et Maximilien Therras
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl = drawable . getGL ( ) . getGL2 ( ) ;
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable ( GL2.GL_DEPTH_TEST ) ;
		gl.glEnable (GL2.GL_RESCALE_NORMAL) ;

		Planete soleil = new Planete(0,0,"data/sun.jpg",0,100,glu,echelleRotation/24,0,null);
		planetes.add(soleil);
		soleil.ID=gl.glGenLists(count++);

		Planete mercure = new Planete(7,350,"data/mercure.jpg",0,15,glu,echelleRotation/(58.65f),echelleRevolution/87.969f,null);
		planetes.add(mercure);
		mercure.ID=gl.glGenLists(count++);

		Planete venus = new Planete(3.39f,450,"data/venus.jpg",177.36f,25,glu,echelleRotation/(243.01f),echelleRevolution/224.701f,null);
		planetes.add(venus);
		venus.ID=gl.glGenLists(count++);

		Planete terre = new Planete(0,550,"data/EarthMap.jpg",23.43f,20,glu,echelleRotation/(23.934f/24),echelleRevolution/365.256f,null);
		terre.ID=gl.glGenLists(count++);
		new Lune(terre,-23.43f,35,"data/moon.jpg",0,10,glu,echelleRotation/(27.32f),echelleRevolution/27.32f,count,gl);
		planetes.add(terre);

		Planete mars = new Planete(1.85f,700,"data/mars.jpg",25.19f,20,glu,echelleRotation/(24.630f/24),echelleRevolution/686.980f,null);
		planetes.add(mars);
		mars.ID=gl.glGenLists(count++);

		Planete jupiter = new Planete(1.3f,1050,"data/jupiter.jpg",3.12f,50,glu,echelleRotation/(9.841f/24),echelleRevolution/4332.6f,null);
		jupiter.ID=gl.glGenLists(count++);
		new Lune(jupiter,0.036f-3.12f,80,"data/io.jpg",0,11,glu,echelleRotation/(1.769f),echelleRevolution/1.769f,count,gl);
		new Lune(jupiter,0.469f-3.12f,130,"data/europe.jpg",0,10,glu,echelleRotation/(3.551181f),echelleRevolution/3.551181f,count,gl);
		new Lune(jupiter,0.21f -3.12f,200,"data/ganymede.jpg",0,14,glu,echelleRotation/(7.1545529f),echelleRevolution/7.1545529f,count,gl);
		planetes.add(jupiter);

		Anneau anneau_saturne=new Anneau(45,70,"data/anneau_saturne.png");
		Planete saturne = new Planete(2.48f,1350,"data/saturne.jpg",26.73f,35,glu,echelleRotation/(10.233f/24),echelleRevolution/10759.2f,anneau_saturne);
		planetes.add(saturne);
		saturne.ID=gl.glGenLists(count++);

		Planete uranus = new Planete(0.77f,1450,"data/uranus.jpg",97.8f,30,glu,echelleRotation/(17.9f/24),echelleRevolution/30688.4f,null);
		planetes.add(uranus);
		uranus.ID=gl.glGenLists(count++);

		Planete neptune = new Planete(1.76f,1550,"data/neptune.jpg",29.58f,30,glu,echelleRotation/(19.2f/24),echelleRevolution/60181.3f,null);
		planetes.add(neptune);
		neptune.ID=gl.glGenLists(count++);


		try {
			for (int i=0;i<6;i++)
			{
				File im = new File ( "data/space"+(i+1)+".png" ) ;
				Texture t= TextureIO . newTexture (im, true ) ;
				textFond[i]= t . getTextureObject ( gl ) ;
			}
		} catch ( Exception e ) {
			e . printStackTrace ( ) ;
		}

		//Cacher le curseur
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
		Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, new Point(0,0), "InvisibleCursor");        
		setCursor(invisibleCursor);

		centerMouse();
		
		//regarde le soleil
		float d=(float) Math.sqrt(eyeX*eyeX+eyeY*eyeY);
		POV_orientation=-65;
		POV_orientation_y=-(float) Math.toDegrees(Math.asin(eyeY/d));
	}

	/**
	 * @author Maximilien Therras
	 */
	@Override
	public void reshape(GLAutoDrawable drawable , int x , int y , int width , int height) {
		GL2 gl = drawable.getGL().getGL2();
		gl = drawable . getGL ( ) . getGL2 ( ) ;
		if ( height <= 0 ) height = 1 ;
		final float h = ( float ) width / ( float ) height ;
		gl . glViewport ( 0 , 0 , width , height ) ;
		gl . glMatrixMode ( GL2.GL_PROJECTION ) ;
		gl . glLoadIdentity ( ) ;
		glu.gluPerspective ( 45.0f , h, 1.0 , 20.0 ) ;
		gl . glMatrixMode ( GL2.GL_MODELVIEW ) ;
		gl . glLoadIdentity ( );
		gl.glTranslatef(0, 0, -5f);
		gl.glRotatef(-90, 1, 0, 0);
	}

	public static void main(String[] args)
	{
		new Launcher(1024,1024);
	}

	/**
	 * Place la souris au centre de l'écran en continu
	 * (ATTENTION : est également actif hors de la fenêtre)
	 * @author Victor Lewon
	 */
	public void centerMouse() 
	{
		//Place la souris au milieu
		try {
			r = new Robot();
			r.mouseMove((int)RRRw/2, (int)RRRh/2);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gère les déplacements :
	 * Z : Avance vers la caméra
	 * S : Recule
	 * Q : Déplacement latéral sur la gauche
	 * D : Déplacement latéral sur la droite
	 * @author Victor Lewon
	 */
	public void moveCamera()
	{

		//déplacement latéral calculé avec le produit vectoriel de (lx,ly,lz) et (0,1,0)
		if (keys[0])
		{
			//On quitte le mode suivi de planète
			following=false;
			eyeX -= -lz * POV_speed ; 
			eyeZ -= lx * POV_speed ; 
		}
		if (keys[1])
		{
			following=false;
			eyeX += -lz * POV_speed ; 
			eyeZ += lx * POV_speed ; 
		}
		if (keys[2])
		{
			following=false;
			//déplacements de la caméra en se dirigant vers le point regardé
			eyeX += lx * POV_speed ; 
			eyeZ += lz * POV_speed ; 
			eyeY += ly * POV_speed ;
		}
		if (keys[3])
		{
			following=false;
			eyeX -= lx * POV_speed ;
			eyeZ -= lz * POV_speed ; 
			eyeY -= ly * POV_speed ;
		}
		if (keys[4])
		{
			following=false;
			//On augmente eyeY : déplacement vertical
			eyeY+=POV_speed;
		}
		if (keys[5])
		{
			following=false;
			eyeY-=POV_speed;
		}
	}

	/**
	 * Gère les touches appuyées :
	 * BACK SPACE : Suit la planète suivante
	 * Maj + BACK SPACE : Suit la planète précédente
	 * P : Augmente la vitesse de rotation des planètes / lunes
	 * M : Diminue la vitesse de rotation des planètes / lunes
	 * O : Affiche / Cache les orbites
	 * @author Victor Lewon
	 */
	@Override
	public void keyPressed(KeyEvent e)	{
		if (e.getKeyCode()==KeyEvent.VK_Q)
			keys[0]=true;
		if (e.getKeyCode()==KeyEvent.VK_D)
			keys[1]=true;
		if (e.getKeyCode()==KeyEvent.VK_Z)
			keys[2]=true;
		if (e.getKeyCode()==KeyEvent.VK_S)
			keys[3]=true;
		if (e.getKeyCode()==KeyEvent.VK_SPACE)
			keys[4]=true;
		if (e.getKeyCode()==KeyEvent.VK_C)
			keys[5]=true;

		//Passe la caméra en mode suivi, suit une planète et la regarde depuis en haut.
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
		{
			d_to_planet_watched=1f;
			following=true;
			if (!e.isShiftDown())
			{
				planetWatched++;
				if (planetWatched>=planetes.size())
					planetWatched=0;
			}
			else
			{
				planetWatched--;
				if (planetWatched<0)
					planetWatched=planetes.size()-1;
			}
		}
		//Accélération vitesse planètes
		if (e.getKeyCode()==KeyEvent.VK_P)
			vitesse_systeme+=0.1f;
		//Ralentissement vitesse planètes
		if (e.getKeyCode()==KeyEvent.VK_M)
			vitesse_systeme-=0.1f;
		//Affiche / Cache orbites
		if (e.getKeyCode()==KeyEvent.VK_O)
			showOrbits=!showOrbits;
	}


	/**
	 * @author Victor Lewon
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_Q)
			keys[0]=false;
		if (e.getKeyCode()==KeyEvent.VK_D)
			keys[1]=false;
		if (e.getKeyCode()==KeyEvent.VK_Z)
			keys[2]=false;
		if (e.getKeyCode()==KeyEvent.VK_S)
			keys[3]=false;
		if (e.getKeyCode()==KeyEvent.VK_SPACE)
			keys[4]=false;
		if (e.getKeyCode()==KeyEvent.VK_C)
			keys[5]=false;
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Gère les mouvements de caméra à la souris
	 * @author Victor Lewon
	 */
	public void mouseMvt() {
		Point cursor=new Point();
		cursor.x=(int) MouseInfo.getPointerInfo().getLocation().getX();
		cursor.y=(int) MouseInfo.getPointerInfo().getLocation().getY();

		//On calcule l'intensité du mouvement de souris effectué
		double intensityX=cursor.x-RRRw/2;
		double intensityY=cursor.y-RRRh/2;

		//On modifie l'angle de vue vertical
		POV_orientation_y	-= POV_rotation_speed*(intensityY)*0.07;
		//on majore et minore l'angle de vue vertical
		if (POV_orientation_y>=180f)
			POV_orientation_y=180f;
		if (POV_orientation_y<=-180f)
			POV_orientation_y=-180f;
		
		//On modifie l'angle de vue horizontal
		POV_orientation		+= POV_rotation_speed*(intensityX)*0.07; 
		
		//On modifie le point regardé
		lx = (float) Math.sin(Math.toRadians(POV_orientation )) ;
		lz = (float)-Math.cos(Math.toRadians(POV_orientation )) ;
		ly = (float) Math.toRadians(POV_orientation_y);
		
		//On replace la souris au centre de l'écran
		centerMouse();
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Affiche la textFond représentant les étoiles et la voie lactée en fond
	 * @author Victor Lewon et Maximilien Therras
	 */
	public void dessinerFond(GL2 gl)
	{
		//On dessine un cube avec une texture d'étoiles et de la voie lactée dessus pour faire le fond
		gl.glPushMatrix();

		gl.glLoadIdentity();
		//On aligne la voie lactée avec le plan du système solaire
		gl.glRotatef(90, 1, 0, 0);
		gl.glPushAttrib(GL2.GL_ENABLE_BIT);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_LIGHTING);

		float taille_box=30000f;
		
		//On applique chaque texture à une face
		gl.glBindTexture(GL.GL_TEXTURE_2D, textFond[0]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0); gl.glVertex3f(  taille_box, -taille_box, -taille_box );
		gl.glTexCoord2f(1, 0); gl.glVertex3f( -taille_box, -taille_box, -taille_box );
		gl.glTexCoord2f(1, 1); gl.glVertex3f( -taille_box,  taille_box, -taille_box );
		gl.glTexCoord2f(0, 1); gl.glVertex3f(  taille_box,  taille_box, -taille_box );
		gl.glEnd();

		gl.glBindTexture(GL.GL_TEXTURE_2D, textFond[1]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0); gl.glVertex3f(  taille_box, -taille_box,  taille_box );
		gl.glTexCoord2f(1, 0); gl.glVertex3f(  taille_box, -taille_box, -taille_box );
		gl.glTexCoord2f(1, 1); gl.glVertex3f(  taille_box,  taille_box, -taille_box );
		gl.glTexCoord2f(0, 1); gl.glVertex3f(  taille_box,  taille_box,  taille_box );
		gl.glEnd();

		gl.glBindTexture(GL.GL_TEXTURE_2D, textFond[2]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0); gl.glVertex3f( -taille_box, -taille_box,  taille_box );
		gl.glTexCoord2f(1, 0); gl.glVertex3f(  taille_box, -taille_box,  taille_box );
		gl.glTexCoord2f(1, 1); gl.glVertex3f(  taille_box,  taille_box,  taille_box );
		gl.glTexCoord2f(0, 1); gl.glVertex3f( -taille_box,  taille_box,  taille_box );
		gl.glEnd();

		gl.glBindTexture(GL.GL_TEXTURE_2D, textFond[3]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0); gl.glVertex3f( -taille_box, -taille_box, -taille_box );
		gl.glTexCoord2f(1, 0); gl.glVertex3f( -taille_box, -taille_box,  taille_box );
		gl.glTexCoord2f(1, 1); gl.glVertex3f( -taille_box,  taille_box,  taille_box );
		gl.glTexCoord2f(0, 1); gl.glVertex3f( -taille_box,  taille_box, -taille_box );
		gl.glEnd();

		gl.glBindTexture(GL.GL_TEXTURE_2D, textFond[4]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 1); gl.glVertex3f( -taille_box,  taille_box, -taille_box );
		gl.glTexCoord2f(0, 0); gl.glVertex3f( -taille_box,  taille_box,  taille_box );
		gl.glTexCoord2f(1, 0); gl.glVertex3f(  taille_box,  taille_box,  taille_box );
		gl.glTexCoord2f(1, 1); gl.glVertex3f(  taille_box,  taille_box, -taille_box );
		gl.glEnd();

		gl.glBindTexture(GL.GL_TEXTURE_2D, textFond[5]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0); gl.glVertex3f( -taille_box, -taille_box, -taille_box );
		gl.glTexCoord2f(0, 1); gl.glVertex3f( -taille_box, -taille_box,  taille_box );
		gl.glTexCoord2f(1, 1); gl.glVertex3f(  taille_box, -taille_box,  taille_box );
		gl.glTexCoord2f(1, 0); gl.glVertex3f(  taille_box, -taille_box, -taille_box );
		gl.glEnd();

		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	/**
	 * Lorsqu'une planète est suivie, zoom vers elle avec molette avant et dézoom avec molette arrière
	 * @author Victor Lewon
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation()>0)
			d_to_planet_watched+=0.01f*e.getWheelRotation()*15;
		else if (e.getWheelRotation()<0)
			d_to_planet_watched+=0.01f*e.getWheelRotation()*15;
	}

	/**
	 * Place des lumières autour du soleil.
	 * @author Victor Lewon
	 */
	public void setLight(GL2 gl){
		float diffuse[] = { 3.5f, 3.5f, 3.5f, 3.5f };

		float d=200f;
		
		
		//On place les lumières autour du Soleil
		float l1[] = { -d, 0f, 0f, 1f };
		float l2[] = { d, 0, 0, 1f };
		float l3[] = { 0f, d, 0f, 1f };
		float l4[] = { 0, -d, 0, 1f };
		float l5[] = { 0f, 0f, d, 1f };
		float l6[] = { 0, 0, -d, 1f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, l1, 0);
		gl.glEnable(GL2.GL_LIGHT0);

		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, l2, 0);
		gl.glEnable(GL2.GL_LIGHT1);

		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, l3, 0);
		gl.glEnable(GL2.GL_LIGHT2);

		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, l4, 0);
		gl.glEnable(GL2.GL_LIGHT3);

		gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_POSITION, l5, 0);
		gl.glEnable(GL2.GL_LIGHT4);

		gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT5, GL2.GL_POSITION, l6, 0);
		gl.glEnable(GL2.GL_LIGHT5);
	}
}
