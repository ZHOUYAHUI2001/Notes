package com.edu.mynote.Note;

import java.io.Serializable;

public class Note implements Serializable {
    private int nId;
    private String nContent;
    private String nTime;
    private String nAuthor;
    public  Note(){

    }
    public Note(int nId,String nAuthor, String nContent, String nText) {
        this.nId = nId;
        this.nAuthor=nAuthor;
        this.nContent = nContent;
        this.nTime = nText;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }

    public String getnContent() {
        return nContent;
    }

    public void setnContent(String nContent) {
        this.nContent = nContent;
    }

    public String getnTime() {
        return nTime;
    }

    public void setnTime(String nTime) {
        this.nTime = nTime;
    }

    public String getnAuthor() {
        return nAuthor;
    }

    public void setnAuthor(String nAuthor) {
        this.nAuthor = nAuthor;
    }

    @Override
    public String toString() {
        return "Note{" +
                "nId=" + nId +
                ",nAuthor="+ nAuthor +'\''+
                ", nContent='" + nContent + '\'' +
                ", nText='" + nTime + '\'' +
                '}';
    }


}
