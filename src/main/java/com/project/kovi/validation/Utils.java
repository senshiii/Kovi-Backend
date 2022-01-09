package com.project.kovi.validation;

import java.util.UUID;

public class Utils {

    public static String newId(){
        return String.join("", UUID.randomUUID().toString().split("-"));
    }

}
