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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener,MouseMotionListener {
	
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
	
	private int curLevel = 1, maxLevel = 3;
	
	public static String gameState = "MENU";
	private boolean showGameOverMessage = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	public Menu menu;
	
	public boolean saveGame = false;
	
	public int[] pixels;
	public BufferedImage lightMap;
	public int[] lightMapPixels;
	
	public static BufferedImage miniMapa;
	public static int[] miniMapaPixels;
	
	//Fonte Importada
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	//public Font newFont;
	
	//Coordenadas do Mouse
	//public int mx, my;
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			lightMap = ImageIO.read(getClass().getResource("/light.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightMapPixels = new int[lightMap.getWidth() * lightMap.getHeight()];
		lightMap.getRGB(0, 0, lightMap.getWidth(), lightMap.getHeight(), lightMapPixels, 0, lightMap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		shoot = new ArrayList<Shoot>();
		spritesheet = new SpriteSheet("/spritesheet.png");
		player = new Player(0,0,16,16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		
		world = new World("/level1.png");
		
		menu = new Menu();
		
		/*
		try {
			newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f);
		} 
		catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public void initFrame() {
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		
		Image windowIcon = null;
		try {
			windowIcon = ImageIO.read(getClass().getResource("/icon.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorIcon = toolkit.getImage(getClass().getResource("/cursor.png"));
		Cursor c = toolkit.createCustomCursor(cursorIcon, new Point(0,0), "img");
		frame.setCursor(c);
		frame.setIconImage(windowIcon);
		
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
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level"};
				int[] opt2 = {this.curLevel};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo Salvo");
			}
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
				//AVAN??AR N??VEL
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
			
			if(restartGame) {
				this.restartGame = false;
				gameState = "NORMAL";
				curLevel = 1;
				String newWorld = "level"+curLevel+".png";
				World.restartGame(newWorld);
			}
		}	
		else if(gameState == "MENU") {
			player.updateCamera();
			menu.tick();
		}
		
		//MINIMAPA
		miniMapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		miniMapaPixels = ((DataBufferInt)miniMapa.getRaster().getDataBuffer()).getData();
	}
	
	/* Manipulando Pixels
	public void drawRectangleExample(int xoff, int yoff) {
		for(int xx = 0; xx<32; xx++) {
			for(int yy=0; yy<32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				if(xOff < 0 || yOff < 0 || xOff>=WIDTH || yOff>=HEIGHT) {
					continue;
				}
				pixels[xOff + (yOff*WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	public void applyLight() {
		for(int xx=0; xx<Game.WIDTH; xx++) {
			for(int yy=0; yy<Game.HEIGHT; yy++) {
				if(lightMapPixels[xx + (yy*Game.WIDTH)] == 0xffffffff) {
					pixels[xx+ (yy*Game.WIDTH)] = 0;
				}
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		//g.setColor(new Color(0, 0, 0));
		//g.fillRect(0,0,WIDTH,HEIGHT);
		//Graphics2D  g2 = (Graphics2D) g;
		//g.dispose();
		//g.setColor(Color.GREEN);
		//g.fillOval(0, 40, 80, 80);
		
		//RENDERIZA????O DO JOGO
		world.render(g); //Renderizando mundo
		Collections.sort(entities, Entity.depthSorter);//Determinar Profundidade das entidades
		
		for(int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g); //Renderizando entidades
		}
		for(int i=0; i<shoot.size(); i++) {
			shoot.get(i).render(g); //Renderizando tiros
		}
		
		//applyLight(); //Ilumina????o Din??mica
		
		ui.render(g); //Renderizando barra de vida
		
		g = bs.getDrawGraphics();
		g.drawImage(image, 0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
		
		//Renderizando contador de muni????o
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Muni????o: " + player.ammo, 600, 30);
		
		//Renderizando Fonte importada
		//g.setFont(newFont);
		//g.drawString("Teste com a nova fonte", 20, 20);
		
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
		
		//RENDERIZA????O DO MINIMAPA
		World.renderMiniMapa();
		if(curLevel < 3) {
			g.drawImage(miniMapa, 618, 378, World.WIDTH*5, World.HEIGHT*5, null);
		}
		else {
			g.drawImage(miniMapa, 615, 375, World.WIDTH*2, World.HEIGHT*2, null);
		}
		
		//ROTA????O DE OBJETO PELO MOUSE
		/*
		Graphics2D g2 = (Graphics2D) g;
		double mouseAngle = Math.atan2(200+25 - my, 200+25 - mx);
		g2.rotate(mouseAngle, 200+25, 200+25);
		g.setColor(Color.red);
		g.fillRect(200, 200, 50, 50);
		*/
			
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
			
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			Menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL") {
				this.saveGame = true;
			}
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

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/*
		mx = e.getX();
		my = e.getY();
		*/
	}

}
