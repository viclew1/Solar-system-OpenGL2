package systeme_solaire;

import java.io.File;
import java.util.ArrayList;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;



/**
 * Objet Planete contenant toutes les informations nécessaires au dessin des planètes
 * @author Maximilien Therras
 */
public class Planete {
	public float incl_orb;
	public float rot_p_soleil;
	public float d_Soleil;
	public Texture text_planete;
	public float incl_p_s;
	public float rot_p_p;
	public GLUquadric params;
	public float r;
	public ArrayList<Lune> lunes=new ArrayList<Lune>();
	public int ID;
	public float vitesse_rot;
	public float vitesse_revolution;
	public Anneau anneau;
	
	/**
	 * @author Maximilien Therras
	 * @param incl_orb inclinaison orbitale de la planète
	 * @param d_Soleil distance de la planète au soleil
	 * @param textureName nom du fichier de texture
	 * @param incl_p_s inclinaison de la planète par rapport au soleil
	 * @param r rayon de la planète
	 * @param glu GLU de la classe principale
	 * @param vitesse_rot vitesse de rotation de la planète sur elle-même
	 * @param vitesse_revolution vitesse de révolution de la planète autour du soleil
	 * @param anneau Anneau de la planète (null si elle n'en possède pas)
	 */
	public Planete(float incl_orb,float d_Soleil,
			String textureName, float incl_p_s,float r,GLU glu,float vitesse_rot,float vitesse_revolution,Anneau anneau){
		this.incl_orb=incl_orb;
		this.rot_p_soleil=0;
		this.d_Soleil=d_Soleil;
		try {
			text_planete=TextureIO.newTexture(new File(textureName),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.incl_p_s=incl_p_s;
		this.rot_p_p=0;
		
		GLUquadric sphere=glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere,GLU.GLU_FILL);
		glu.gluQuadricTexture(sphere,true) ;
		glu.gluQuadricNormals(sphere,GLU.GLU_SMOOTH);
		params=sphere;
		
		this.r=r;
		this.vitesse_rot=vitesse_rot;
		this.vitesse_revolution=vitesse_revolution;
		this.anneau=anneau;
	}
	
	/**
	 * Dessine la planète ainsi que ses anneaux
	 * @author Maximilien Therras
	 * @param gl GL2 de la classe principale
	 * @param glu GLU de la classe principale
	 */
	public void affiche_planete(GL2 gl, GLU glu){
		
		move();
		//Calcul la rotation de la planete sur elle meme et autour de sa planete centre
		gl.glRotatef(-90,1,0,0);
		calcul(gl,glu);
		//dessin d’un planete
		bindTexture(gl);
		glu.gluSphere(params,r,100,100);
		if(anneau!=null)
			anneau.affiche_anneau(gl, glu);

		gl.glPopMatrix();
		//désactivation des textures
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glCallList(ID);

	}
	
	/**
	 * Dessine l'orbite de la planète
	 * @author Maximilien Therras
	 * @param gl
	 */
	public void drawOrbit(GL2 gl)
	{
		gl.glBegin(GL.GL_LINE_LOOP);
		float x,y;
		for(float k=0;k<(Math.PI*2);k+=Math.PI/180){
			x=(float) (Math.sin(k)*d_Soleil);
			y=(float) (Math.cos(k)*d_Soleil);
			gl.glVertex3f(x,y,0);
		}
		gl.glEnd();
	}
	
	/**
	 * Calcule les translations et rotations à effectuer pour placer la planète
	 * @author Maximilien Therras
	 * @param gl
	 * @param glu
	 */
	public void calcul(GL2 gl, GLU glu){
		gl.glPushMatrix();

		//On place la planète sur la bonne inclinaison orbitale par rapport au plan du soleil
		gl.glRotatef(incl_orb,1,0,0);
		
		if (Launcher.showOrbits)
			drawOrbit(gl);
		
		//On fait tourner la planète autour du soleil
		gl.glRotatef(rot_p_soleil,0,0,1);
		
		//On place la planète à la bonne distance du soleil
		gl.glTranslatef(-d_Soleil,0,0);
		
		//On incline la planète par rapport au soleil
		gl.glRotatef(incl_p_s,-1,0,0);
		
		//On fait tourner la planète sur elle-même
		gl.glRotatef(rot_p_p-rot_p_soleil,0,0,1);

	}
	
	/**
	 * @author Maximilien Therras
	 * Modifie les angles actuels de la planète
	 */
	public void move(){
		this.rot_p_p+=vitesse_rot*Launcher.vitesse_systeme;
		this.rot_p_soleil+=vitesse_revolution*Launcher.vitesse_systeme;
	}
	
	/**
	 * Applique la texture à la planète
	 * @author Maximilien Therras
	 * @param gl
	 */
	public void bindTexture(GL2 gl){
		//activation des textures
		gl.glEnable(GL2.GL_TEXTURE_2D);
		text_planete.enable(gl);
		text_planete.bind(gl);
	}
}
