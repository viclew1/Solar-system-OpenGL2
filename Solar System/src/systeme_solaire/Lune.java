package systeme_solaire;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 *	Objet Lune h�ritant de la classe Planete contenant les informations n�cessaires pour dessiner les lunes 
 * @author Maximilien Therras
 */
public class Lune extends Planete{

	public Planete p;
	
	/**
	 * @author Maximilien Therras
	 * @param p Plan�te associ�e � cette lune
	 * @param incl_orb inclinaison orbitale de la lune
	 * @param d_Soleil distance de la lune � la plan�te
	 * @param textureName nom du fichier de texture
	 * @param incl_p_s inclinaison de la lune par rapport au soleil
	 * @param r rayon de la lune
	 * @param glu GLU de la classe principale
	 * @param vitesse_rot vitesse de rotation de la plan�te sur elle-m�me
	 * @param vitesse_revolution vitesse de r�volution de la plan�te autour du soleil
	 * @param count permet d'assigner l'ID de la list
	 */
	public Lune(Planete p,float incl_orb,float d_Soleil,
			String textureName, float incl_p_s,float r,GLU glu,float vitesse_rot,float vitesse_revolution, int count, GL2 gl){
		super(incl_orb,d_Soleil,
				textureName,incl_p_s,r,glu,vitesse_rot,vitesse_revolution,null);
		this.p=p;
		ID=gl.glGenLists(count++);
		p.lunes.add(this);
	}

	/**
	 * Calcule les translations et rotations � effectuer pour placer la lune
	 * @author Maximilien Therras
	 * @param gl
	 * @param glu
	 */
	public void calcul(GL2 gl, GLU glu){
		gl.glPushMatrix();

		//On place la lune l� o� se trouve la plan�te
		gl.glRotatef(p.incl_orb,1,0,0);
		gl.glRotatef(p.rot_p_soleil,0,0,1);
		gl.glTranslatef(-p.d_Soleil,0,0);
		
		//On place la lune sur la bonne inclinaison orbitale par rapport au plan de la plan�te
		gl.glRotatef(incl_orb,1,0,0);
		if (Launcher.showOrbits)
			drawOrbit(gl);

		//On fait tourner la lune autour de la plan�te
		gl.glRotatef(rot_p_soleil-p.rot_p_soleil,0,0,1);
		
		//On place la lune � la bonne distance de la plan�te
		gl.glTranslatef(-d_Soleil,0,0);
		//On incline la lune par rapport � la plan�te
		gl.glRotatef(incl_p_s-p.incl_p_s,-1,0,0);
		//On fait tourner la lune sur elle-m�me
		gl.glRotatef(rot_p_p-rot_p_soleil,0,0,1);
	}
}
