package com.CAN301_ios.forum_server.entity;

public class Comment {
    int commentID;
    int postID;
    String username;
    String comment;
    String date;
    String time;

    public Comment() {
    }

    public Comment(int postID, String username, String comment) {
        this.postID = postID;
        this.username = username;
        this.comment = comment;
    }

    public Comment(int commentID, int postID, String username, String comment) {
        this.commentID = commentID;
        this.postID = postID;
        this.username = username;
        this.comment = comment;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
