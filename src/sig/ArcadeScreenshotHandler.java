package sig;

import java.io.IOException;
import java.nio.file.Path;
import java.awt.image.BufferedImage;
import java.awt.AWTException;
import java.awt.GraphicsEnvironment;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.Robot;
import sig.engine.Panel;

public class ArcadeScreenshotHandler {
	public static Robot r;
	public static final String PROGRAM_NAME="Sig's Java Project Template";
	public static void main(String[] args) {
		JFrame f = new JFrame(PROGRAM_NAME);
		Panel p = new Panel(f);
		
		p.init();
		
		f.add(p);
		f.setSize(1280,720);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		p.render();

		try {
			r = new Robot();
			BufferedImage img = CaptureScreen();
			ImageIO.write(img,"png",Path.of("screenshot.png").toFile());
		} catch (AWTException | IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage CaptureScreen() throws IOException {
		BufferedImage screenshot = r.createScreenCapture(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
		//ImageIO.write(screenshot,"png",new File("screenshot.png"));
		return screenshot;
	}
}
