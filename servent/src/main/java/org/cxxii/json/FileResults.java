package org.cxxii.json;

public class FileResults {

    private int index;
    private String filename;
    private int filesize;
    private String fileType;

    public FileResults(int index, String filename, int filesize, String fileType) {
        this.filename = filename;
        this.filesize = filesize;
        this.fileType = fileType;
    }

    public FileResults() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
