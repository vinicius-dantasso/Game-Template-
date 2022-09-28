package com.viniciusstd.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.viniciusstd.main.Game;
import com.viniciusstd.main.Sound;
import com.viniciusstd.main.SoundAdvanced;
import com.viniciusstd.world.AStar;
import com.viniciusstd.world.Camera;
import com.viniciusstd.world.Vector2i;
import com.viniciusstd.world.World;

public class Enemy extends Entity {
	
	//private double speed = 0.5;
	
	private int maskX = 4, maskY = 5, maskW = 8, maskH = 8;
	
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
		depth = 0;
		
		/*PRIMEIRO ALGORITMO DE MOVIMENTAÇÃO DOS INIMIGOS
		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 100) {
			
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
					//Sound.hitEffect.play();
					Game.player.life-=Game.rand.nextInt(5);
					Game.player.isDamaged = true;
				}
			}
		}
		*/
		
		//SEGUNDO MÉTODO DE MOVIMENTAÇÃO DO INIMIGO / ALGORITMO A*
		if(this.isColiddingWithPlayer() == false) {
			if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int)(x/16), (int)(y/16));
				Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
			}
		}
		else {
			if(Game.rand.nextInt(100) < 10) {
				//Sound.hitEffect.play(); Método básico
				SoundAdvanced.hitSound.play(); //Método Avançado
				Game.player.life-=Game.rand.nextInt(5);
				Game.player.isDamaged = true;
			}
		}
		
		if(new Random().nextInt(100) < 75)
			followPath(path);
		
			
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
					World.generateParticles(5, this.getX(), this.getY());
					Game.shoot.remove(i);
					return;
				}
			}
		}
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
		
		//DEBUG CAIXA DE COLISÃO INIMIGOS
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskW, maskH);
		
	}
	
}
