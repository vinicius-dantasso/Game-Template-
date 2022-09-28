package com.viniciusstd.main;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	
	public static final Sound musicBackground = new Sound("res/music.wav");
	public static final Sound hitEffect = new Sound("res/hit.wav");
	private AudioInputStream audioInput;
	
	private Sound(String name) {
		try {
			File musicPath = new File(name);
			
			if(musicPath.exists()) {
				audioInput = AudioSystem.getAudioInputStream(musicPath);
			}
			else {
				System.out.println("Música não encontrada");
			}
		}catch(Throwable e) {}
	}
	
	public void play() {
		try {
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
		}catch(Throwable e) {}
	}
	
	public void loop() {
		try {
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
		}catch(Throwable e) {}
	}
}
