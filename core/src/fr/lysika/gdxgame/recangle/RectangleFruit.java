package fr.lysika.gdxgame.recangle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Ajoute la notion de fruit dans le rectangle
 * @author remy
 *
 */
public class RectangleFruit extends Rectangle{
	
	private static final long serialVersionUID = 1L;
	Texture texture;
	Integer point;
	
	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public RectangleFruit(){
		super();
	}
	
	public RectangleFruit(Texture texture){
		super();
		this.texture = texture;
		
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
