package com.CAN301_ios.forum_server.controller;



import com.CAN301_ios.forum_server.utils.DatabaseFunction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.CAN301_ios.forum_server.utils.DatabaseFunction.*;

@RestController
@RequestMapping("/login")
public class LoginRegisterFunction {
    @ResponseBody
    @RequestMapping("/register")
    public static String register(String username, String password) {
        //check if the username is unique
        if (accountRepeatCheck(username)) {
            //generate the random salt value for password
            String salt = randomStringGenerator();
            //insert the account information into database
            String SQL = String.format("INSERT INTO account_information (Username,Password,Salt,Token) " +
                    "VALUES ('%s',MD5('%s'),'%s',null)", username, password + salt, salt);
            executeSQL(SQL);
            return "register successfully";
        } else {
            //the account duplicate in database
            return "username has been used";
        }
    }

    //API allows user to use password to login
    @ResponseBody
    @RequestMapping("/loginWithPassword")
    public static String loginWithPassword(String username, String password) {
        //get userid according to username and password
        int userid = loginPasswordCheck(username, password);
        if (userid > 0) {
            //username and password is correct
            //update the token information
            String token = generateToken(userid);
            String SQL = String.format("UPDATE account_information SET Token = '%s' WHERE User_ID = %s", token, userid);
            executeSQL(SQL);
            //return the userid and token
            return "login successfully," + userid + "," + token;
        } else {
            //account or password is wrong
            return "wrong account or password, please check again";
        }
    }

    //API allows user using token to login
    @ResponseBody
    @RequestMapping("/loginWithToken")
    public static String loginWithToken(String token) {
        //get the userid
        int userid = DatabaseFunction.loginWithToken(token);
        if (userid > 0) {
            //no error and return the userid
            return "login successfully";
        } else {
            //no such token exists
            return "token overdue, login failed";
        }
    }

    //API allows user to logout manually and clean the token
    @ResponseBody
    @RequestMapping("/logoutManually")
    public static String logoutManually(int userid, String token) {
        //Check token validity
        if (checkToken(token)) {
            cleanToken(userid);
            return "logout successfully";
        } else {
            return "wrong token";
        }

    }

    //API allows the user to change its password
//    @ResponseBody
//    @RequestMapping("/passwordModify")
//    public static String passwordModify(int userid, String newPassword){
//        //call the method before encapsulation
//        resetPassword(userid, newPassword);
//        return "password reset successfully";
//    }
}
