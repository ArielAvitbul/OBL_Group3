package client;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
/**
 * This class handles a dynamic addition of a ImageView node to a pane and add functionality to it.
 * @author Ariel
 *
 */
public class MyImage extends ImageView {
	public MyImage(String name, String imageURL, double layoutX, double layoutY, EventHandler<MouseEvent> mouseRelease) {
		super(imageURL);
		setId(name);
		setLayoutX(layoutX);
		setLayoutY(layoutY);
		if (mouseRelease!=null)
		setOnMouseReleased(mouseRelease);
	}
	
	public MyImage(String name, String imageURL, EventHandler<MouseEvent> mouseRelease) {
		super(imageURL);
		setId(name);
		if (mouseRelease!=null)
		setOnMouseReleased(mouseRelease);
	}
	
	/*public MyImage(String name, String imageURL, EventHandler<MouseEvent> mouseRelease, EventHandler<MouseEvent> mouseEntered, EventHandler<MouseEvent> mouseExit) {
	super(imageURL);
	setId(name);
	if (mouseRelease!=null)
	setOnMouseReleased(mouseRelease);
	setOnMouseExited(mouseExit);
	setOnMouseEntered(mouseEntered);
}*/
}
