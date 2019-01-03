package client;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MyButton extends Button {
	private ImageView image;
	public MyButton(Pane pane,String imageURL, double layoutX, double layoutY, EventHandler<MouseEvent> mouseRelease) {
		image = new ImageView(new Image(getClass().getResource(imageURL).toExternalForm()));
		image.setLayoutX(layoutX);
		image.setLayoutY(layoutY);
		setLayoutX(layoutX);
		setLayoutY(layoutY);
		setOpacity(0.0);
		setPrefHeight(image.getImage().getHeight());
		setPrefWidth(image.getImage().getWidth());
		if (mouseRelease!=null)
		setOnMouseReleased(mouseRelease);
		pane.getChildren().add(image);
		pane.getChildren().add(this);
	}
	
	public ImageView getImage() {
		return image;
	}
}
