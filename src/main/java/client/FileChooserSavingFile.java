package client;

import java.io.File;
import java.io.FileOutputStream;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.FileResponse;
import javafx.stage.FileChooser.ExtensionFilter;

public class FileChooserSavingFile extends Application {
	private FileResponse file;
	
	public FileChooserSavingFile(FileResponse file) {
		this.file = file;
	}
	@Override
	public void start(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save");
		fileChooser.setInitialFileName(file.getFilename());
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"), new ExtensionFilter(file.getFileType(), "*.txt"));
		File selectedFile = fileChooser.showSaveDialog(stage);
		if (selectedFile != null) {
			try {
				// dialog closed by selecting a file to save the data to
				
				FileOutputStream br = new FileOutputStream(selectedFile.toPath().toString());
				byte[] buffer = file.getDataBytes();
				System.out.println(selectedFile.toPath());
				for(int i = 0; i < file.getFileSize(); i++) {
					br.write(buffer[i]);
				}
				br.close();
			} catch (Exception e) {

			}
			// write data here yourself, e.g.

		}
	}
	public FileResponse getFile() {
		return file;
	}
	public void setFile(FileResponse file) {
		this.file = file;
	}
	
	
}
