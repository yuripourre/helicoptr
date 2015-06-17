import helicoptr.HelicoptrApplication;
import br.com.etyllica.EtyllicaFrame;
import br.com.etyllica.context.Application;


public class HelicoptrExample extends EtyllicaFrame {

	private static final long serialVersionUID = 7739713774644387495L;

	public HelicoptrExample() {
		super(800,600);
	}

	// Main program
	public static void main(String[] args) {
		HelicoptrExample app = new HelicoptrExample();
		app.init();
	}

	@Override
	public Application startApplication() {
		initialSetup("../");
		return new HelicoptrApplication(w, h);
	}

}