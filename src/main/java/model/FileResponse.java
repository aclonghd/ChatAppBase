package model;

import enums.StatusCode;

@SuppressWarnings("serial")
public class FileResponse extends ResponseObject {
	private String filename;
    private int fileSize;
    private byte[] dataBytes;
    private String fileType;
	public FileResponse(String filename, int fileSize, byte[] dataBytes, String fileType, StatusCode statuscode) {
		super(statuscode);
		this.filename = filename;
		this.fileSize = fileSize;
		this.dataBytes = dataBytes;
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
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
    
    
}
