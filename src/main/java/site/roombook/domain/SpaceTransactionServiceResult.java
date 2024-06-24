package site.roombook.domain;

public class SpaceTransactionServiceResult {
    private boolean isSpaceSaved;

    private FileServiceResult fileSaveResult;
    private FileServiceResult fileDeleteResult;

    public boolean isSpaceSaved() {
        return isSpaceSaved;
    }

    public void setSpaceSaved(boolean isSpaceSaved) {
        this.isSpaceSaved = isSpaceSaved;
    }

    public FileServiceResult getFileSaveResult() {
        return fileSaveResult;
    }

    public void setFileSaveResult(FileServiceResult fileSaveResult) {
        this.fileSaveResult = fileSaveResult;
    }

    public FileServiceResult getFileDeleteResult() {
        return fileDeleteResult;
    }

    public void setFileDeleteResult(FileServiceResult fileDeleteResult) {
        this.fileDeleteResult = fileDeleteResult;
    }
}
