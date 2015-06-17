package helicoptr;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.malt.serial.SerialReader;

import br.com.etyllica.context.Application;
import br.com.etyllica.context.UpdateIntervalListener;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.ImageLayer;
import br.com.etyllica.layer.Layer;

public class HelicoptrSerialApplication extends Application implements UpdateIntervalListener, SerialPortEventListener {

	private ImageLayer layer;
	
	private int gravity = 3;
	private int engine = -2;
	private int speed = 8;
	
	private boolean pressed = false; 
	private boolean dead = false;
	
	private List<Layer> obstacles = new ArrayList<Layer>();
	
	private SerialReader reader;
	private static final String PREFIX = "LED";
	
	public HelicoptrSerialApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		reader = new SerialReader();
		reader.init(this);
		
		reset();		
		
		updateAtFixedRate(10, this);
	}
	
	private void reset() {
		layer = new ImageLayer(20,100,"helicopter.png");
		layer.setAngle(10);
		layer.centralizeY(this);
		
		int width = 30;
		
		Random random = new Random();
		
		int baseRandom = 300;
		
		obstacles.clear();
		
		for(int i=0;i<800;i++) {
			obstacles.add(new Layer(i*width+random.nextInt(200), h/2-20+random.nextInt(baseRandom)+100, width, h/2));	
			obstacles.add(new Layer(i*width+random.nextInt(200), -h/2-100+random.nextInt(baseRandom), width, h/2));
		}
		
		dead = false;
	}
	
	@Override
	public void draw(Graphic g) {
		layer.draw(g);
		g.setColor(Color.RED);
		g.drawRect(layer);
		
		for(Layer layer:obstacles) {
			g.setColor(Color.RED);
			g.fillRect(layer);
		}
		
		if(dead) {
			g.drawStringShadowX(200, "YOU ARE DEAD!");
		}
	}
		
	public GUIEvent updateKeyboard(KeyEvent event) {
		if(event.isKeyDown(KeyEvent.TSK_SPACE)) {
			pressButton();
		} else if(event.isKeyUp(KeyEvent.TSK_SPACE)) {
			releaseButton();
		}
		
		if(event.isKeyDown(KeyEvent.TSK_ENTER)) {
			reset();
		}
		
		return null;
	}
	
	private void pressButton() {
		pressed = true;
		layer.setAngle(-10);
	}
	
	private void releaseButton() {
		pressed = false;
		layer.setAngle(10);
	}

	@Override
	public void timeUpdate(long now) {
		if(dead) {
			return;
		}
		
		if(layer.getY() < 0) {
			dead = true;
		}
		
		if(!pressed) {
			layer.setOffsetY(gravity);	
		} else {
			layer.setOffsetY(engine);
		}
				
		for(Layer obstacle:obstacles) {
			
			if(layer.colideRect(obstacle)) {
				dead = true;
			}
			
			obstacle.setOffsetX(-speed);
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = reader.getInput().readLine();
				
				if(inputLine.startsWith(PREFIX)) {
					
					if(inputLine.equals(PREFIX+" ON")) {
						pressButton();
					} else if(inputLine.equals(PREFIX+" OFF")) {
						releaseButton();
					} else {
						System.out.println("Invalid command");
					}
				}				
				
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

}
