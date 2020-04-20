package com.anyu.community;

import com.anyu.community.utils.CommunityConstant;

public class EmunTest implements CommunityConstant {


    public static void main(String[] args) {
        for (EntityType E : EntityType.values()) {
            System.out.println(E.name());
            System.out.println(E.value());
        }
//        System.out.printf(EntityType.POST);
    }
}
