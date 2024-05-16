package com.CAN301_ios.forum_server.controller;

import com.CAN301_ios.forum_server.entity.Comment;
import com.CAN301_ios.forum_server.entity.Post;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.CAN301_ios.forum_server.utils.DatabaseFunction;

import java.util.Date;
import java.util.List;

import static com.CAN301_ios.forum_server.utils.DatabaseFunction.*;

@RestController
@RequestMapping("/post")
public class PostFunction {
    @ResponseBody
    @RequestMapping("/addPost")
    public static String addPost(String username, String topic, String content){
        if(!userExistCheck(username)){
            return "user does not exist";
        }
        Date date = new Date();
        String sql = String.format("INSERT INTO post_information (Username,Topic,Content,Date,Time) " +
                "VALUES ('%s','%s','%s','%tD%n','%tT%n')", username, topic, content, date, date);
        if(executeSQL(sql).equals("execute successfully")){
            return "submit successfully";
        }else {
            return "failed to post";
        }
    }

    @ResponseBody
    @RequestMapping("/deletePost")
    public static String deletePost(int postID){
        String sql = String.format("DELETE FROM post_information WHERE Post_ID='%s'",postID);
        if(executeSQL(sql).equals("execute successfully")){
            String sql2 = String.format("DELETE FROM comment_information WHERE Post_ID='%s'",postID);
            if(executeSQL(sql2).equals("execute successfully"))return "delete successfully with comment";
            return "delete successfully without comment";
        }else {
            return "failed to delete";
        }
    }

    @ResponseBody
    @RequestMapping("/getAllPost")
    public static List<Post> getAllPost(){
        return DatabaseFunction.getAllPost();
    }

    @ResponseBody
    @RequestMapping("/getPostByUser")
    public static List<Post> getPostByUser(String username){
        return getPostByName(username);
    }

    @ResponseBody
    @RequestMapping("/getPostByContent")
    public static List<Post> getPostByContent(String content){
        return DatabaseFunction.getPostByContent(content);
    }

    @ResponseBody
    @RequestMapping("/getHotPost")
    public static List<Post> getHotPost(){
        return DatabaseFunction.getHotPost();
    }

    @ResponseBody
    @RequestMapping("/getPostByID")
    public static Post getPostByID(int postID){
        return DatabaseFunction.getPostByID(postID);
    }

    @ResponseBody
    @RequestMapping("/getPostByTopic")
    public static List<Post> getPostByTopic(String topic){
        return DatabaseFunction.getPostByTopic(topic);
    }

    @ResponseBody
    @RequestMapping("/addComment")
    public static String addComment(int postID, String username, String comment){
        if(!userExistCheck(username)){
            return "user does not exist";
        }
        Date date = new Date();
        String sql = String.format("INSERT INTO comment_information (Post_ID,Username,Comment,Date,Time) " +
                "VALUES ('%s','%s','%s','%tD%n','%tT%n')", postID, username, comment, date, date);
        if(executeSQL(sql).equals("execute successfully")){
            return "submit successfully";
        }else {
            return "failed to post";
        }
    }
    @ResponseBody
    @RequestMapping("/getCommentByPost")
    public static List<Comment> getCommentByPost(int postID){
        return DatabaseFunction.getCommentByPost(postID);
    }

    @ResponseBody
    @RequestMapping("/deleteComment")
    public static String deleteComment(int commentID){
        String sql = String.format("DELETE FROM comment_information WHERE Comment_ID='%s'",commentID);
        if(executeSQL(sql).equals("execute successfully")){
            return "delete successfully";
        }else {
            return "failed to delete";
        }
    }
    @ResponseBody
    @RequestMapping("/addLike")
    public static String addLike(String username, int postID){
        String sql = String.format("UPDATE post_information SET Likes=Likes+1 WHERE Post_ID = '%s'",postID);
        if(executeSQL(sql).equals("execute successfully")){
            String sql2 = String.format("INSERT INTO likes_information (Username,Post_ID) VALUES ('%s','%s')",username,postID);
            if(executeSQL(sql2).equals("execute successfully")){
                return "add both";
            }
            return "add only post information";
        }else {
            return "failed to delete";
        }
    }
    @ResponseBody
    @RequestMapping("/cancelLike")
    public static String cancelLike(String username, int postID){
        String sql = String.format("UPDATE post_information SET Likes=Likes-1 WHERE Post_ID = '%s'",postID);
        if(executeSQL(sql).equals("execute successfully")){
            String sql2 = String.format("DELETE FROM likes_information WHERE Username='%s' AND Post_ID='%s'",username,postID);
            if(executeSQL(sql2).equals("execute successfully")){
                return "cancel both";
            }
            return "cancel only post information";
        }else {
            return "failed to delete";
        }
    }
    @ResponseBody
    @RequestMapping("/checkLike")
    public static String checkLike(String username, int postID){
        if(alreadyLikeCheck(username, postID)){
            return "true";
        }else {
            return "false";
        }
    }
    @ResponseBody
    @RequestMapping("/addFavorite")
    public static String addFavorite(int postID, String username, String comment){
        if(!userExistCheck(username)){
            return "user does not exist";
        }
        Date date = new Date();
        String sql = String.format("INSERT INTO Favorite_Posts (Post_ID,Username) " +
                "VALUES ('%s','%s')", postID, username);
        if(executeSQL(sql).equals("execute successfully")){
            return "submit successfully";
        }else {
            return "failed to post";
        }
    }
}
