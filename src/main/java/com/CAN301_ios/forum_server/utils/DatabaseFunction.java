package com.CAN301_ios.forum_server.utils;

import com.CAN301_ios.forum_server.entity.Comment;
import com.CAN301_ios.forum_server.entity.Post;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseFunction {
    static String database_url = "jdbc:mysql://sh-cynosdbmysql-grp-0kbomg8k.sql.tencentcdb.com:26316/forum?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
    static String database_username = "root";
    static String database_password = "Iosgroupwork1";

    //execute the SQL sentence without return information
    public static String executeSQL(String SQL) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            myStatement.executeUpdate(SQL);
            //close the connection
            myConnection.close();
            return "execute successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            return "database error";
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //check if the account name already exist in database
    public static boolean accountRepeatCheck(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM account_information WHERE Username = '%s'", username);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            if (rs.next()) {
                rs.close();
                myConnection.close();
                return false;
            }
            rs.close();
            myConnection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //login with username and password
    public static int loginPasswordCheck(String username, String password) {
        try {
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select the row has the corresponding password and username
            //there is a sub-query for the password in order to get the MD5 value of the string concatenation of salt and original password
            String SQL = String.format("SELECT * FROM account_information WHERE Username = '%s' AND Password = MD5(CONCAT('%s',(SELECT Salt FROM account_information WHERE Username = '%s')))", username, password, username);
            ResultSet rs = myStatement.executeQuery(SQL);
            if (rs.next()) {
                //if there exists such row, return the userid
                int userid = rs.getInt("User_ID");
                //close connection
                rs.close();
                myConnection.close();
                return userid;
            }
            //if not exist, close connection
            rs.close();
            myConnection.close();
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //according to the userid and server time, to generate a unique token for authentication
    public static String generateToken(int userid) {

        java.util.Date date = new Date();
        //token validity period
        long date_overdue = date.getTime() + 3*24*60*60*1000;
        return String.valueOf(userid) + '_' + date + '_' + date_overdue;

    }

    // generate salt for password encryption
    public static String randomStringGenerator() {

        // generate a random string
        return RandomStringUtils.randomAlphanumeric(20);

    }

    //use token to log in and return the userid
    public static int loginWithToken(String token) {
        try {
            //connect to database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //check if any user has the token in database
            String SQL = String.format("select User_ID from account_information where Token = '%s'", token);
            ResultSet rs = myStatement.executeQuery(SQL);
            if (rs.next()) {
                //if the token exist, return the ID code
                int userid = rs.getInt("User_ID");
                rs.close();
                myConnection.close();
                if (checkToken(token)){
                    return userid;
                }else {
                    return -1;
                }
            }
            rs.close();
            myConnection.close();
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //check token
    public static boolean checkToken(String token) {
        try {
            //connect to database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            String SQL = String.format("select * from account_information where Token = '%s'", token);
            ResultSet rs = myStatement.executeQuery(SQL);
            //check if token exist in database
            if (rs.next()) {
                rs.close();
                myConnection.close();
                String[] token_data = token.split("_");
                //check if token overdue
                Date date = new Date();
                long date_now = date.getTime();
                return date_now < Long.parseLong(token_data[2]);
            }
            rs.close();
            myConnection.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //clean the existing token of specific userid
    public static void cleanToken(int userid) {
        String SQL = String.format("UPDATE account_information SET Token = null where User_ID = %s", userid);
        executeSQL(SQL);
    }

    //change the password of specific userid
    public static void resetPassword(int userid, String newPassword) {
        //update password in database
        String SQL = String.format("UPDATE account_information SET Password = '%s' WHERE User_ID = '%s'", newPassword, userid);
        executeSQL(SQL);
    }


    public static List<Post> getAllPost() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = "SELECT * FROM post_information";
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            List<Post> posts= new ArrayList<>();
            while (rs.next()){
                Post post = new Post();
                post.setPostID(rs.getInt(1));
                post.setUsername(rs.getString(2));
                post.setTopic(rs.getString(3));
                post.setContent(rs.getString(4));
                post.setDate(rs.getString(5));
                post.setTime(rs.getString(6));
                post.setLikes(rs.getInt(7));
                posts.add(post);
            }
            rs.close();
            myConnection.close();
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Post> getHotPost() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = "SELECT * FROM post_information ORDER BY Likes desc";
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            List<Post> posts= new ArrayList<>();
            while (rs.next()){
                Post post = new Post();
                post.setPostID(rs.getInt(1));
                post.setUsername(rs.getString(2));
                post.setTopic(rs.getString(3));
                post.setContent(rs.getString(4));
                post.setDate(rs.getString(5));
                post.setTime(rs.getString(6));
                post.setLikes(rs.getInt(7));
                posts.add(post);
            }
            rs.close();
            myConnection.close();
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //check if the account name already exist in database
    public static boolean userExistCheck(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM account_information WHERE Username = '%s'", username);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            if (rs.next()) {
                rs.close();
                myConnection.close();
                return true;
            }
            rs.close();
            myConnection.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Post> getPostByName(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM post_information WHERE Username = '%s'", username);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            List<Post> posts= new ArrayList<>();
            while (rs.next()){
                Post post = new Post();
                post.setPostID(rs.getInt(1));
                post.setUsername(rs.getString(2));
                post.setTopic(rs.getString(3));
                post.setContent(rs.getString(4));
                post.setDate(rs.getString(5));
                post.setTime(rs.getString(6));
                post.setLikes(rs.getInt(7));
                posts.add(post);
            }
            rs.close();
            myConnection.close();
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Post> getPostByTopic(String topic) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM post_information WHERE Topic = '%s'", topic);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            List<Post> posts= new ArrayList<>();
            while (rs.next()){
                Post post = new Post();
                post.setPostID(rs.getInt(1));
                post.setUsername(rs.getString(2));
                post.setTopic(rs.getString(3));
                post.setContent(rs.getString(4));
                post.setDate(rs.getString(5));
                post.setTime(rs.getString(6));
                post.setLikes(rs.getInt(7));
                posts.add(post);
            }
            rs.close();
            myConnection.close();
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Post> getPostByContent(String content) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            String SQL = "SELECT * FROM post_information WHERE Content LIKE '%" + content + "%'";
            ResultSet rs = myStatement.executeQuery(SQL);
            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                Post post = new Post();
                post.setPostID(rs.getInt(1));
                post.setUsername(rs.getString(2));
                post.setTopic(rs.getString(3));
                post.setContent(rs.getString(4));
                post.setDate(rs.getString(5));
                post.setTime(rs.getString(6));
                post.setLikes(rs.getInt(7));
                posts.add(post);
            }
            rs.close();
            myConnection.close();
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Comment> getCommentByPost(int postID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM comment_information WHERE Post_ID = '%s'", postID);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            List<Comment> comments= new ArrayList<>();
            while (rs.next()){
                Comment comment = new Comment();
                comment.setCommentID(rs.getInt(1));
                comment.setPostID(rs.getInt(2));
                comment.setUsername(rs.getString(3));
                comment.setComment(rs.getString(4));
                comment.setDate(rs.getString(5));
                comment.setTime(rs.getString(6));
                comments.add(comment);
            }
            rs.close();
            myConnection.close();
            return comments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Post getPostByID(int postID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM post_information WHERE Post_ID = '%s'", postID);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            List<Post> posts= new ArrayList<>();
            if (rs.next()){
                Post post = new Post();
                post.setPostID(rs.getInt(1));
                post.setUsername(rs.getString(2));
                post.setTopic(rs.getString(3));
                post.setContent(rs.getString(4));
                post.setDate(rs.getString(5));
                post.setTime(rs.getString(6));
                post.setLikes(rs.getInt(7));
                rs.close();
                myConnection.close();
                return post;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    //check if the account name already exist in database
    public static boolean alreadyLikeCheck(String username, int postID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //connect the database
            Connection myConnection = DriverManager.getConnection(database_url, database_username, database_password);
            Statement myStatement = myConnection.createStatement();
            //select rows with such username
            String SQL = String.format("SELECT * FROM likes_information WHERE Username = '%s' AND Post_ID ='%s'", username, postID);
            ResultSet rs = myStatement.executeQuery(SQL);
            //if there exist this username in database
            if (rs.next()) {
                rs.close();
                myConnection.close();
                return true;
            }
            rs.close();
            myConnection.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
