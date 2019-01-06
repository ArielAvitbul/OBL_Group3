package client;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MyButton extends ImageView {
	private ImageView image;
	public MyButton(Pane pane,String imageURL, double layoutX, double layoutY, EventHandler<MouseEvent> mouseRelease) {
		image = new ImageView(new Image(getClass().getResource(imageURL).toExternalForm()));
		image.setLayoutX(layoutX);
		image.setLayoutY(layoutY);
		if (mouseRelease!=null)
		setOnMouseReleased(mouseRelease);
		pane.getChildren().add(image);
	}
}
