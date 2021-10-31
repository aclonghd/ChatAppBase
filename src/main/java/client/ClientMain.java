package client;

import java.net.URL;

import netscape.javascript.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;

public class ClientMain extends Application {

	@Override
	public void start(final Stage stage) {
		
		WebView browser = new WebView();
		
		WebEngine webEngine = browser.getEngine();
		webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36 Edg/95.0.1020.38");
		Scene scene = new Scene(browser);
		webEngine.setOnAlert(event -> System.err.println(event.toString()));
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
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent t) {
		    	if(client.isConnect())webEngine.executeScript("exit();");
		        Platform.exit();
		        System.exit(0);
		    }
		});
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}