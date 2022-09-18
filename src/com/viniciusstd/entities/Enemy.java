package com.viniciusstd.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.viniciusstd.main.Game;
import com.viniciusstd.world.Camera;
import com.viniciusstd.world.World;

public class Enemy extends Entity {
	
	private double speed = 0.5;
	
	private int maskX = 8, maskY = 8, maskW = 10, maskH = 10;
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	
	private int life = 10;
	private boolean isHit = false;
	private int hitFrames = 5, hitCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112+16, 16, 16, 16);
	}
	
	public void tick() {
		maskX = 8; 
		maskY = 8; 
		maskW = 8; 
		maskH = 8;
		
		if(this.isColiddingWithPlayer() == false) {
			
			if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
					&& !isColidding((int)(x+speed), this.getY())) {
				x+=speed;
			}
			else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColidding((int)(x-speed), this.getY())) {
				x-=speed;
			}
			
			if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isColidding(this.getX(), (int)(y+speed))) {
				y+=speed;
			}
			else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isColidding(this.getX(), (int)(y-speed))) {
				y-=speed;
			}
		}
		else {
			if(Game.rand.nextInt(100) < 10) {
				Game.player.life-=Game.rand.nextInt(5);
				Game.player.isDamaged = true;
				if(Game.player.life <= 0) {
					//Game Over
					//System.exit(1);
				}
			}
		}
			
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		if(isHit) {
			hitCurrent++;
			if(hitCurrent == hitFrames) {
				hitCurrent = 0;
				isHit = false;
			}
		}
		
		isColiddingWithShoot();
		
		if(life <= 0) {
			destroySelf();
		}
			
	}
	
	public boolean isColiddingWithPlayer() {
		Rectangle currentEnemy = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return currentEnemy.intersects(player);
	}
	
	public void isColiddingWithShoot(){
		for(int i=0; i<Game.shoot.size(); i++) {
			Entity e = Game.shoot.get(i);
			
			if(e instanceof Shoot) {
				if(Entity.isColidding(this, e)) {
					isHit = true;
					life-=5;
					Game.shoot.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isColidding(int xNext, int yNext) {
		Rectangle currentEnemy = new Rectangle(xNext + maskX, yNext + maskY, maskW, maskH);
		
		for(int i=0; i<Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) {
				continue;
			}
			
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY, maskW, maskH);
			if(currentEnemy.intersects(targetEnemy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void render(Graphics g) {
		if(!isHit) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		else {
			g.drawImage(Entity.ENEMY_HIT, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		
	}
	
}
