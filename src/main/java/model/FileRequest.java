package model;

import enums.Event;

@SuppressWarnings("serial")
public class FileRequest extends RequestObject {
	private String filename;
    private int fileSize;
    private byte[] dataBytes;
    private String fileType;
	public FileRequest(String filename, int fileSize, byte[] dataBytes, String fileType, Event event) {
		super(event);
		this.filename = filename;
		this.fileSize = fileSize;
		this.dataBytes = dataBytes;
		this.fileType = fileType;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public byte[] getDataBytes() {
		return dataBytes;
	}
	public void setDataBytes(byte[] dataBytes) {
		this.dataBytes = dataBytes;
	}
    
    
}
