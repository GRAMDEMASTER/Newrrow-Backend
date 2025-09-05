package com.work.newrrow.domain.quest;

import java.security.SecureRandom;

final class QuestIdGenerator {
    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RND = new SecureRandom();
    private QuestIdGenerator(){}

    static String qid(){
        return "quest_" + rand(8).toLowerCase();
    }
    private static String rand(int n){
        var sb = new StringBuilder(n);
        for (int i=0;i<n;i++) sb.append(ALPHANUM.charAt(RND.nextInt(ALPHANUM.length())));
        return sb.toString();
    }
}