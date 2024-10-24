package store.roombook.domain;

public class FileServiceResult {
    private boolean isSaved;
    private int unsavedFileCnt;
    private boolean isDeleted;
    private int undeletedFileCnt;
    private boolean isFileCntExceeded;

    public FileServiceResult() {}

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public int getUnsavedFileCnt() {
        return unsavedFileCnt;
    }

    public void setUnsavedFileCnt(int unsavedFileCnt) {
        this.unsavedFileCnt = unsavedFileCnt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getUndeletedFileCnt() {
        return undeletedFileCnt;
    }

    public void setUndeletedFileCnt(int undeletedFileCnt) {
        this.undeletedFileCnt = undeletedFileCnt;
    }

    public boolean isFileCntExceeded() {
        return isFileCntExceeded;
    }

    public void setFileCntExceeded(int spaceFileCnt) {
        isFileCntExceeded = spaceFileCnt != 1;
    }
}
