package com.mss.myfirebasedemo.model;

/**
 * Created by deepakgupta on 9/2/17.
 */


public class ChatModel {

    private String id;
    private FromModel fromModel;
    private String message;
    private String timeStamp;

    private ToModel toModel;

    public FileModel getFileModel() {
        return fileModel;
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
    }

    private FileModel fileModel;


    public ChatModel() {
    }

    public ChatModel(FromModel frommodel, ToModel toModel, FileModel fileModel, String message, String timeStamp) {
        this.fromModel = frommodel;
        this.message = message;
        this.toModel = toModel;
        this.timeStamp = timeStamp;
        this.fileModel = fileModel;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FromModel getFromModel() {
        return fromModel;
    }

    public void setFromModel(FromModel fromModel) {
        this.fromModel = fromModel;
    }

    public ToModel getToModel() {
        return toModel;
    }

    public void setToModel(ToModel toModel) {
        this.toModel = toModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public String toString() {
        return "ChatModel{" +
                ", timeStamp='" + timeStamp + '\'' +
                ", message='" + message + '\'' +
                ", fromModel=" + fromModel + '\'' +
                ", toModel='" + toModel + '\'' +
                ", fileModel='" + fileModel +
                '}';
    }
}
