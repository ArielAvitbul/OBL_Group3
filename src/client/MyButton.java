package client;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MyButton extends Button {
	private ImageView image;
	public MyButton(String imageURL, double layoutX, double layoutY, EventHandler<MouseEvent> mouseRelease) {
		image = new ImageView(new Image(getClass().getResource(imageURL).toExternalForm()));
		image.setLayoutX(layoutX);
		image.setLayoutY(layoutY);
		setLayoutX(layoutX);
		setLayoutY(layoutY);
		setOpacity(0.0);
		setPrefHeight(image.getImage().getHeight());
		setPrefWidth(image.getImage().getWidth());
		setOnMouseReleased(mouseRelease);
	}
	
	public ImageView getImage() {
		return image;
	}
}
