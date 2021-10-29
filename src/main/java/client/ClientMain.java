package client;

import java.net.URL;

import netscape.javascript.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;

public class ClientMain extends Application {

	@Override
	public void start(final Stage stage) {
		
		WebView browser = new WebView();
		
		WebEngine webEngine = browser.getEngine();
		Scene scene = new Scene(browser);
		
		URL url = this.getClass().getResource("../index.html");
		webEngine.setJavaScriptEnabled(true);
		
		webEngine.load(url.toString());
		
		
		stage.setTitle("Chat Với Người Lạ");
		stage.setScene(scene);
		stage.setWidth(1080);
		stage.setHeight(720);
		stage.show();
		
		Client client = new Client(webEngine);
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                final JSObject jsobj = (JSObject) webEngine.executeScript("window");
        		jsobj.setMember("myJavaMember", client);
            }
        });
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}