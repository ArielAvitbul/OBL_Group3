package client;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MyImage extends ImageView {
	public MyImage(String imageURL, double layoutX, double layoutY, EventHandler<MouseEvent> mouseRelease) {
		super(imageURL);
		setLayoutX(layoutX);
		setLayoutY(layoutY);
		if (mouseRelease!=null)
		setOnMouseReleased(mouseRelease);
	}
}
