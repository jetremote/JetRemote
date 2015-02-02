/** PiWC :: Boat Control System to WaterSportsCenters
Copyright (C) 2014  Javier Hdez. :: movidroid@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package application;
**/
package application;
	
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		File file = new File("/var/log/PiWC.log");
		try {
			if(file.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		launch(args);
		}
	 
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main_WhiteBg.fxml"));
		final Parent root = (Parent) loader.load();
		final Scene scene = new Scene(root);
		//scene.setCursor(Cursor.NONE);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
