package sig;

import java.io.IOException;
import java.nio.file.Path;
import java.awt.image.BufferedImage;
import java.awt.AWTException;
import java.awt.GraphicsEnvironment;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sig.engine.Panel;

public class ArcadeScreenshotHandler {
	public static Robot r;
	public static final String PROGRAM_NAME="Sig's Java Project Template";
	public static void main(String[] args) {
		JFrame f = new JFrame(PROGRAM_NAME);
		JButton button = new JButton("Capture");

		JLabel text = new JLabel("");
		
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				BufferedImage img;
				try {
					img = CaptureScreen();
					ImageIO.write(img,"png",Path.of("..","screenshot.png").toFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				playSound(Path.of("..","ding.wav"));
			}

		});
		
		f.add(button);
		//f.add(text);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static void playSound(Path file) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.toFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
			// If you want the sound to loop infinitely, then put: clip.loop(Clip.LOOP_CONTINUOUSLY); 
			// If you want to stop the sound, then use clip.stop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static BufferedImage CaptureScreen() throws IOException {
		BufferedImage screenshot = r.createScreenCapture(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
		//ImageIO.write(screenshot,"png",new File("screenshot.png"));
		return screenshot;
	}
}
