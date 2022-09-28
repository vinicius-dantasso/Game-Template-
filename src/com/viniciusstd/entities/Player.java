package com.viniciusstd.entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.viniciusstd.graficos.SpriteSheet;
import com.viniciusstd.main.Game;
import com.viniciusstd.main.SoundAdvanced;
import com.viniciusstd.world.Camera;
import com.viniciusstd.world.World;

public class Player extends Entity {
	public boolean right,up,left,down;
	public double speed = 1.5;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	
	public double life = 100, maxLife = 100;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean hasGun = false;
	public boolean shoots = false, mouseShoot = false;
	public int mx, my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i=0; i<4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16),0,16,16);
		}
		for(int i=0; i<4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16),16,16,16);
		}
	}
	
	public void tick() {
		depth = 1;
		
		moved = false;
		if(right && World.isFree((int)(x+speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if(left && World.isFree((int)(x-speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		
		if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			y-=speed;
		}
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		this.checkItems();
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 5) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(life <= 0) {
			//GAME OVER
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		if(shoots) {
			//Criar bala e atirar
			shoots = false;
			if(hasGun && ammo > 0) {
				ammo--;
				int dx = 0;
				int px = 0, py = 8;
				if(dir == right_dir) {
					px = 16;
					dx = 1;
				}
				else {
					px = -8;
					dx = -1;
				}
				
				Shoot bullet = new Shoot(this.getX() + px,this.getY() + py, 3, 3, null, dx, 0);
				Game.shoot.add(bullet);
			}
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			if(hasGun && ammo > 0) {
				ammo--;
				double angle = 0;
				
				int px = 8, py = 8;
				if(dir == right_dir) {
					px = 16;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				else {
					px = -8;
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				}
				
				double dx = Math.cos(angle), dy = Math.sin(angle);
				
				Shoot bullet = new Shoot(this.getX() + px,this.getY() + py, 3, 3, null, dx, dy);
				Game.shoot.add(bullet);
			}
		}
		
		updateCamera();
	}
	
	public void checkItems() {
		for(int i=0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			
			if(atual instanceof LifePack) {
				if(Entity.isColidding(this, atual)) {
					life += 10;
					if(life > 100) {
						life = 100;
					}
					Game.entities.remove(atual);
				}
			}
			else if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					ammo+=10;
					Game.entities.remove(atual);
				}
			}
			else if(atual instanceof Weapon) {
				if(Entity.isColidding(this, atual)) {
					hasGun = true;
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					//Desenhar arma para direita
					g.drawImage(Entity.GUN_ON_RIGHT_HAND, this.getX() + 9 - Camera.x, this.getY() + 5 - Camera.y, null);
				}
			}
			else if(dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					//Desenhar arma para esquerda
					g.drawImage(Entity.GUN_ON_LEFT_HAND, this.getX() - 9 - Camera.x, this.getY() + 5 - Camera.y, null);
				}
			}
		}
		else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		
	}
	
}
