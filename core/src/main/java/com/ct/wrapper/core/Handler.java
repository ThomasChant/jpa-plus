package com.ct.wrapper.core;

public enum Handler {

        /**
         * 等于
         */
        EQUAL,
        /**
         * 不等于
         */
        NOT_EQUAL,
        /**
         * 小于
         */
        LT,
        /**
         * 小于等于
         */
        LE,
        /**
         * 大于
         */
        GT,
        /**
         * 大于等于
         */
        GE,
        /**
         * 全匹配
         */
        ALL_LIKE,
        /**
         * 左匹配
         */
        LEFT_LIKE,
        /**
         * 右匹配
         */
        RIGHT_LIKE,
        /**
         * 在...之内
         */
        IN,
        /**
         * 不在...之内
         */
        NOT_IN,

        /**
         * 为空
         */
        IS_NULL,
        /**
         * 不为空
         */
        IS_NOT_NULL,

        /**
         * 在...之间
         */
        BETWEEN,

        /**
         * 不在...之间
         */
        NOT_BETWEEN
    }