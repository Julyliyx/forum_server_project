package com.CAN301_ios.forum_server.entity;

public class Post {
    int postID;
    String username;
    String topic;
    String content;
    String date;
    String time;
    int likes;
    public Post(){

    }
    public Post(int postID, String username, String topic, String content){
        this.postID = postID;
        this.topic = topic;
        this.content = content;
        this.username = username;
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

    public String getContent() {
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
