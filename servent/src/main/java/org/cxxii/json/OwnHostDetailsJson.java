package org.cxxii.json;

public class OwnHostDetailsJson {
    private int NumSharedFiles;
    private int NumkilobytesShared;

    // Default constructor
    public OwnHostDetailsJson() {
        this(0, 0);
    }

    // Parameterized constructor
    public OwnHostDetailsJson(int numSharedFiles, int numkilobytesShared) {
        this.NumSharedFiles = numSharedFiles;
        this.NumkilobytesShared = numkilobytesShared;
    }

    // Getters and setters (if needed)
    public int getNumSharedFiles() {
        return NumSharedFiles;
    }

    public void setNumSharedFiles(int numSharedFiles) {
        NumSharedFiles = numSharedFiles;
    }

    public int getNumkilobytesShared() {
        return NumkilobytesShared;
    }

    public void setNumkilobytesShared(int numkilobytesShared) {
        NumkilobytesShared = numkilobytesShared;
    }
}
