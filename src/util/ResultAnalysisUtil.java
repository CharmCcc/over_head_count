package util;

import java.util.ArrayList;

import static main.OverHeadCount.SCALE;

public class ResultAnalysisUtil {

    /*
     *解析result字符串中的person_num
     */
    public static int getPersonNum(String result) {
        int person_num = Integer.parseInt(result.split("\"person_num\": ")[1].split(",")[0]);
        return person_num;
    }


    public static ArrayList<int[]> getPersonInfos(String result) {
        ArrayList<int[]> person_infos = new ArrayList<>();

        if (result.contains("ID")) {
            String[] ss = result.split("\"ID\": ");

            //数组中第一个String元素无有用数据，这里直接从1开始
            for (int i = 1; i < ss.length; i++) {
                int ID = Integer.parseInt(ss[i].split(",")[0]);
                int width = Integer.parseInt(ss[i].split("\"width\": ")[1].split(",")[0]);
                int top = Integer.parseInt(ss[i].split("\"top\": ")[1].split(",")[0]);
                int height = Integer.parseInt(ss[i].split("\"height\": ")[1].split(",")[0]);
                int left = Integer.parseInt(ss[i].split("\"left\": ")[1].split("}")[0]);
                int[] person_info = {ID,left*(int)(1/SCALE),top*(int)(1/SCALE),width*(int)(1/SCALE),height*(int)(1/SCALE)};
                person_infos.add(person_info);
            }

            return person_infos;
        }
        return null;
    }

    /*
     *解析result字符串中的in与out
     */
    public static int[] getPersonCount(String result) {

        //person_count[0]表示in，personCount[1]表示out
        int[] person_count = new int[2];

        String in = result.split("\"in\": ")[1].split(",")[0];
        String out = result.split("\"out\": ")[1].split("}")[0];
        person_count[0] = Integer.parseInt(in);
        person_count[1] = Integer.parseInt(out);
        return person_count;
    }
}
