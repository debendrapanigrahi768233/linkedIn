package com.debpro.linkedin.user_service.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    //Hash the password for first time
    public static String hashPassword(String plainPasswordText){
        return BCrypt.hashpw(plainPasswordText, BCrypt.gensalt());
    }

    //Match the password from hash and supplied text password
    public static boolean matchPassword(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword,hashedPassword);
    }

}
