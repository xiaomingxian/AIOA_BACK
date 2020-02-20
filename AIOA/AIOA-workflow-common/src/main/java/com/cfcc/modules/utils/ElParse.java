package com.cfcc.modules.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ElParse {

    /**
     * ${a=='b'||c=='d'&&e=='f'}
     * ${a=='b'}
     * ....
     * TODO 不等待补充
     */
    public static Map<String, String> parseCondition(String elString) {

        HashMap<String, String> map = new HashMap<>();

        String ss = elString.replace("$", "").
                replace("{", "").
                replace("}", "").
                replace("'", "");

        if (ss.contains("(") && ss.contains(")")) {
            ss = ss.replace("(", "").replace(")", "");
        }

        if (ss.contains(" || ") && ss.contains(" && ")) {
            ss = ss.replace(" || ", "-");
            ss = ss.replace(" && ", "-");
        }
        if (ss.contains(" || ") && !ss.contains(" && " )) {
            ss = ss.replace("||", "-");
        }
        if (!ss.contains(" || ") && ss.contains(" && ")) {
            ss = ss.replace(" && ", "-");
        }
        /**
         * todo 待验证
         */

        if (ss.contains(" and ") && ss.contains(" or ")) {
            ss = ss.replace(" and ", "-");
            ss = ss.replace(" or ", "-");
        }
        if (!ss.contains(" or ") && ss.contains(" and ")) {
            ss = ss.replace(" and ", "-");
        }

        if (!ss.contains(" and ") && ss.contains(" or ")) {
            ss = ss.replace(" or ", "-");
        }
        if (ss.contains("-")) {
            String[] split = ss.split("-");
            Arrays.stream(split).forEach(i -> {
                map.put(i.split("==")[0], i.split("==")[1]);
            });
        } else {
            //只有一个条件
            map.put(ss.split("==")[0], ss.split("==")[1]);
        }


        return map;
    }

    /**
     * ${assignee}
     *
     * @param elString
     * @return
     */
    public static String parseNormal(String elString) {
        if (elString.contains("$") && elString.contains("{") && elString.contains("}")) {
            elString = elString.replace("$", "").replace("{", "").replace("}", "");
        }
        return elString;
    }


}
