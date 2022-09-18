package com.viniciusstd.main;

import com.viniciusstd.entities.Enemy;
import com.viniciusstd.entities.Entity;
import com.viniciusstd.entities.Player;
import com.viniciusstd.entities.Shoot;
import com.viniciusstd.graficos.SpriteSheet;
import com.viniciusstd.graficos.UI;
import com.viniciusstd.world.World;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener {
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Shoot> shoot;
	public static SpriteSheet spritesheet;
	public static Player player;
	
	public static World world;
	public static Random rand;
	public UI ui;
	
	private int curLevel = 1, maxLevel = 2;
	
	public static String gameState = "MENU";
	private boolean showGameOverMessage = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	public Menu menu;
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		shoot = new ArrayList<Shoot>();
		spritesheet = new SpriteSheet("/spritesheet.png");
		player = new Player(0,0,16,16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		
		world = new World("/level1.png");
		
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game jogo = new Game();
		jogo.start();
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
			this.restartGame = false;
			for(int i=0; i<entities.size(); i++) {
				Entity e = entities.get(i);
				if(e instanceof Player) {
					//Dando tick no player
				}
				e.tick();
			}
			
			for(int i=0; i<shoot.size(); i++) {
				shoot.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				//AVANÇAR NÍVEL
				curLevel++;
				if(curLevel > maxLevel) {
					curLevel = 1;
				}
				String newWorld = "level"+curLevel+".png";
				World.restartGame(newWorld);
			}
		}
		else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 24) {
				this.framesGameOver = 0;
				if(this.showGameOverMessage) {
					this.showGameOverMessage = false;
				}
				else {
					this.showGameOverMessage = true;
				}
			}
		}
		else if(gameState == "MENU") {
			menu.tick();
		}
		
		if(restartGame) {
			this.restartGame = false;
			gameState = "NORMAL";
			curLevel = 1;
			String newWorld = "level"+curLevel+".png";
			World.restartGame(newWorld);
		}
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		
		//RENDERIZAÇÃO DO JOGO
		//Graphics2D  g2 = (Graphics2D) g;
		world.render(g); //Renderizando mundo
		for(int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g); //Renderizando entidades
		}
		for(int i=0; i<shoot.size(); i++) {
			shoot.get(i).render(g); //Renderizando tiros
		}
		ui.render(g); //Renderizando barra de vida
		
		//g.dispose();
		//g.setColor(Color.GREEN);
		//g.fillOval(0, 40, 80, 80);
		g = bs.getDrawGraphics();
		g.drawImage(image, 0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
		
		//Renderizando contador de munição
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 600, 30);
		
		//Renderizando Game Over
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0, 100));
			g2.fillRect(0,0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("Game Over", (WIDTH*SCALE)/2 - 80, (HEIGHT*SCALE)/2 - 10);
			g.setFont(new Font("arial", Font.BOLD, 26));
			if(this.showGameOverMessage)
				g.drawString(">Pressione Enter para Reiniciar<", (WIDTH*SCALE)/2 - 175, (HEIGHT*SCALE)/2 + 35);
		}
		else if(gameState == "MENU") {
			menu.render(g);
		}
			
		bs.show();
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+frames);
				frames = 0;
				timer+=1000;
			}
			
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			
			if(gameState == "MENU") {
				menu.up = true;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoots = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
