package org.jasig.ssp.util;

public interface Validators {

    static <T> void requiredArgument(T target, String msg) {
        if(target == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    static <T> void checkCondition(Boolean condition, String msg) {
        if(condition) {
            throw new IllegalArgumentException(msg);
        }
    }

}
