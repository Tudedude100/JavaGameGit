package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject{
	
	// player stuff
	private int health;
	private int maxHealth;
	private int ammo;
	private boolean dead;
	
	//raygun attack
	private boolean firing;
	private int rayGunDamage;
	//private ArrayList<GunRays> gunRays;
	
	private boolean slashing;
	private boolean jabbing;
	private int slashDamage;
	private int jabDamage;
	private int slashRange;
	private int jabRange;
	
	private boolean tumbling;
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 1, 1, 1, 1, 1, 1, 1 };
	
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int FIRING = 5;
	private static final int SLASHING = 6;
	private static final int JABBING = 7;

	
	public Player(TileMap tm) {
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.4;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		rayGunDamage = 3;
		slashDamage = 2;
		jabDamage = 4;
		slashRange = 40;
		jabRange = 50;
		
		try {
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
								"/Sprites/Player/playersprites.gif"
					)
			);
			
			sprites = new ArrayList<BufferedImage[]>();
			
			for(int i = 0; i < 7; i++) {
				BufferedImage[] bi =
						new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++) {
					if(i != 6){
						bi[j] = spritesheet.getSubimage(
								j * width,
								i * height,
								width,
								height
						);
					}else{
						bi[j] = spritesheet.getSubimage(
								j * width * 2,
								i * height,
								width,
								height
						);
					}
				}
				
				sprites.add(bi);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
	}
	
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public void setFiring() {
		firing = true;
	}
	public void setSlashing() {
		slashing = true;
	}
	public void setJabbing() {
		jabbing = true;
	}
	public void setTumbling() {
		tumbling = true;
	}
	
	private void getNextPosition() {
		
		//movement
		if(left){
			dx -= moveSpeed;
			if(dx < -maxSpeed){
				dx = -maxSpeed;
			}
		}else if(right){
			dx += moveSpeed;
			if(dx > moveSpeed){
				dx = maxSpeed;
			}
		}else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}else if(dx < 0) {
				dx += stopSpeed;
				if( dx > 0) {
					dx = 0;
				}
			}
		}
		
		if(
		(currentAction == SLASHING || currentAction == JABBING || currentAction == FIRING)
		&& !(jumping || falling)){
			dx = 0;
		}
		
		if(jumping && !falling) {
			dy = jumpStart;
			falling = true;
		}
		
		if(falling) {
			
			dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			if(dy > maxFallSpeed) dy = maxFallSpeed;
			
		}
		
	}
	
	public void update() {
		
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(slashing) {
			if(currentAction != SLASHING) {
				currentAction = SLASHING;
				animation.setFrames(sprites.get(SLASHING));
				animation.setDelay(100);
				width = 60;
			}
		}
		else if(jabbing) {
			if(currentAction != JABBING) {
				currentAction = JABBING;
				animation.setFrames(sprites.get(JABBING));
			}
		}else if(firing){
			if(currentAction != FIRING) {
				currentAction = FIRING;
				animation.setFrames(sprites.get(FIRING));
			}
		}else if(dy > 0) {
			if(falling) {
				if(currentAction != FALLING) {
					currentAction = FALLING;
					animation.setFrames(sprites.get(FALLING));
					animation.setDelay(100);
					width = 30;
				}
			}
		}else if(dy < 0) {
			if(currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		}else if(left || right) {
			if(currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		}else{
			if(currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}
		
		animation.update();
		
		// set direction
		if(currentAction != SLASHING && currentAction != JABBING && currentAction != FIRING){
			if(right) facingRight = true;
			if(left) facingRight = false;
			
		}
	}
	public void draw(Graphics2D g) {
		
		setMapPosition();
		
		if(facingRight) {
			g.drawImage(
				animation.getImage(),
				(int)(x + xmap - width / 2),
				(int)(y + ymap - height / 2),
				null
			);
		}
	}
}
