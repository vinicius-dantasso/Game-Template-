package com.viniciusstd.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.viniciusstd.main.Game;
import com.viniciusstd.world.Camera;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7*16, 0, 16, 16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7*16, 16, 16, 16);
	public static BufferedImage ENEMY_HIT = Game.spritesheet.getSprite(9*16, 16, 16, 16);
	public static BufferedImage GUN_ON_RIGHT_HAND = Game.spritesheet.getSprite(112, 0, 16, 16);
	public static BufferedImage GUN_ON_LEFT_HAND = Game.spritesheet.getSprite(128, 0, 16, 16);
	
	protected double x, y;
	protected int width, height;
	private BufferedImage sprite;
	
	private int maskX, maskY, maskW, maskH;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskX = 0;
		this.maskY = 0;
		this.maskW = width;
		this.maskH = height;
	}
	
	public void setMask(int x, int y, int w, int h) {
		this.maskX = x;
		this.maskY = y;
		this.maskW = w;
		this.maskH = h;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	public void setY(int newY) {
		this.y = newY;
	}
	public void setWidth(int newWidth) {
		this.width = newWidth;
	}
	public void setHeight(int newHeight) {
		this.height = newHeight;
	}
	
	public int getX() {
		return (int)x;
	}
	public int getY() {
		return (int)y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public void tick() {
		
	}
	
	public static boolean isColidding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX()+e1.maskX, e1.getY()+e1.maskY, e1.maskW, e1.maskH);
		Rectangle e2Mask = new Rectangle(e2.getX()+e2.maskX, e2.getY()+e2.maskY, e2.maskW, e2.maskH);
		
		return e1Mask.intersects(e2Mask);
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//g.setColor(Color.red);
		//g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskW, maskH);
	}
}
