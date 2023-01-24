package com.td.util;


import java.util.HashMap;
import java.util.Map;

public class SortUtils {

//    static Integer allrow = 3; // 总从机 54口机
//    static Integer allcol = 3; // 总从机
//    static Integer orderNum = 24;
//    static Integer row = 6;///////
//    static Integer col = 3;///////
//
//    static Integer prow = 3; //充电宝定制 行数
//    static Integer pcol = 2; //充电宝定制 列数
//
//
//
//    public static void main(String[] args) {
//        sortPos(allrow,allcol,row,col,prow,pcol);
//    }

    /**
     *
     * @param allrow  传入的从机总行数(口机)
     * @param allcol  传入的从机总列数(口机)
     * @param row  传入的仓位总行数
     * @param col  传入的仓位总列数
     * @param prow 充电宝固定行数 3
     * @param pcol 充电宝固定列数 2
     * @return   返回map "machineId"  "posId"
     */
    public static Map<String, Integer> sortPos(Integer allrow,Integer allcol,Integer row,Integer col,Integer prow,Integer pcol) {

        Map<String, Integer> map = new HashMap<>();

        Integer y = 0;
        Integer x = 0;
        Integer rowindex = 0;
        Integer colindex = 0;

        // 行数循环区间 3 6 9
        for (int i = 0; i < allrow; i++) {
            int rowSectionMax = prow + prow * i; //3 6 9
            int rowSectionMin = rowSectionMax - prow + 1;//1-3 4-6 7-9

            // 判断传进来的 数值 是否 在改 区间
            if(rowSectionMax>=row && row>=rowSectionMin){
                y=i+1; //所在y轴从机号

                rowindex = Math.abs(rowSectionMin-row) + 1; // 具体行数角标 + 1 等于 从机行数位置
                System.out.println("仓位行数位置: "+rowindex);
                break;
            }


        }

        // 列数循环区间 2 4 6
        for (int j = 0; j < allcol; j++) {
            int colSectionMax = pcol + pcol * j;
            int colSectionMin = colSectionMax - pcol +1;

            // 判断传进来的 数值 是否 在改 区间
            if(colSectionMax>=col && col>=colSectionMin){
                x=j+1; //所在x轴从机号

                colindex = Math.abs(colSectionMin-col) + 1; // 具体行数角标 + 1 等于 从机行数位置
                System.out.println("仓位列数位置: "+colindex);
                break;
            }
        }

        System.out.println("从机位置row："+x);
        System.out.println("从机位置col："+y);
        Integer machineId = count(allrow, x, y);
        Integer posId = count(prow, rowindex, colindex);
        System.out.println("从机号"+machineId);
        System.out.println("仓位号"+posId);

        map.put("machineId",machineId);
        map.put("posId",posId);
        return map;
    }


    /**
     *  二维排序转一维排序
     * @param total_row_len 总行数
     * @param total_row 计算的行数
     * @param total_col 计算的列数
     * @return
     */
    private static Integer count(int total_row_len,int total_row,int total_col){
        return (total_col-1)*total_row_len+total_row;
    }

}
