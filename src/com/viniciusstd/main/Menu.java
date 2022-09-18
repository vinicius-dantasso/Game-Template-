package com.viniciusstd.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Menu {
	
	public String[] options = {"Novo Jogo", "Carregar Jogo", "Sair"};
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public boolean up,down;
	
	public void tick() {
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;
			}
		}
		
		if(down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;
			}
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		
		g.setColor(Color.white);
		//TÍTULO
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString(">Place Holder<", (Game.WIDTH*Game.SCALE)/2 - 135, (Game.HEIGHT*Game.SCALE)/2 - 160);
		//OPÇÕES
		g.setFont(new Font("arial", Font.BOLD, 24));
		g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE)/2 - 80, 160);
		g.drawString("Carregar Jogo", (Game.WIDTH*Game.SCALE)/2 - 80, 200);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE)/2 - 80, 240);
		
		if(options[currentOption] == "Novo Jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 - 110, 160);
		}
		else if(options[currentOption] == "Carregar Jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 - 110, 200);
		}
		else if(options[currentOption] == "Sair") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 - 110, 240);
		}
	}
}
