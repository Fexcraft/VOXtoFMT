package alemax;

import alemax.model.Model;
import alemax.opengl.Window;
import alemax.util.FileHandler;


public class Main {

	public static void main(String[] args) {
		Model model = new Model(FileHandler.readVoxFile("./Rail.vox"));

		Window window = new Window();
		window.init();
		
		ModelView modelView = new ModelView(window);

		modelView.setModel(model);

		while(!window.shouldClose()) {
			window.startRender();
			
			modelView.update();
			
			window.finishRender();

		}
		
		modelView.destroy();
		window.close();
		
	}


}
