package client;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MyButton extends ImageView {
	public MyButton(String imageURL, double layoutX, double layoutY, EventHandler<MouseEvent> mouseRelease) {
		super(imageURL);
//		image = new ImageView();
		setLayoutX(layoutX);
		setLayoutY(layoutY);
		if (mouseRelease!=null)
		setOnMouseReleased(mouseRelease);
	}
}
