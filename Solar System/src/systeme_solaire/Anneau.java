package systeme_solaire;

import java.io.File;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Classe anneau servant pour représenter l'anneau de Saturne
 * @author Victor Lewon et Maximilien Therras
 */
public class Anneau {
	
	public float rayon_in;
	public float rayon_out;
	public Texture texture;
	
	/**
	 * @author Victor Lewon et Maximilien Therras
	 * @param rayon_in rayon intérieur
	 * @param rayon_out rayon extérieur
	 * @param textureFileName nom du fichier texture
	 */
	public Anneau(float rayon_in,float rayon_out,String textureFileName){
		this.rayon_in=rayon_in;
		this.rayon_out=rayon_out;
		try {
			texture=TextureIO.newTexture(new File(textureFileName),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dessine l'anneau
	 * @author Victor Lewon et Maximilien Therras
	 * @param gl
	 * @param glu
	 */
	public void affiche_anneau(GL2 gl, GLU glu){
		gl.glEnable(GL2.GL_TEXTURE_2D);
		texture.enable(gl);
		texture.bind(gl);
		GLUquadric anneau=glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(anneau,GLU.GLU_FILL);
		glu.gluQuadricTexture(anneau,true) ;
		glu.gluQuadricNormals(anneau,GLU.GLU_SMOOTH);
		glu.gluDisk(anneau, rayon_in, rayon_out, 100, 100);
	}
}
