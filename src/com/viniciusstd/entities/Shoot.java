package com.viniciusstd.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.viniciusstd.main.Game;
import com.viniciusstd.world.Camera;

public class Shoot extends Entity {
	
	private double dx, dy;
	private double speed = 4;
	private int life = 0, curLife = 30;
	
	public Shoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x+=dx*speed;
		y+=dy*speed;
		life++;
		if(life == curLife) {
			Game.shoot.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
}
