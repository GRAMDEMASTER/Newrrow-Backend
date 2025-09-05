package com.work.newrrow.domain.group;

import java.security.SecureRandom;

final class GroupCodeGenerator {
    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RND = new SecureRandom();
    private GroupCodeGenerator(){}


    static String code6(){ return rand(6); }
    static String gid(){ return "group_" + rand(8).toLowerCase(); }


    private static String rand(int n){
        var sb = new StringBuilder(n);
        for (int i=0; i<n; i++) sb.append(ALPHANUM.charAt(RND.nextInt(ALPHANUM.length())));
        return sb.toString();
    }
}