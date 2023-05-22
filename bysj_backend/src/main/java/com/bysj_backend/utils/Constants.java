package com.bysj_backend.utils;


import java.util.ArrayList;
import java.util.List;

//常量类
public class Constants {

    public static final String SEX_MAN = "1";
    public static final String SEX_WOMAN = "0";
    public static final String IS_DEFAULT_TRUE = "1";
    public static final String IS_DEFAULT_FALSE = "0";



    //1待派送，2已派送，3已完成，4已取消
    public static final String ORDER_STATUS_1 = "1";
    public static final String ORDER_STATUS_2 = "2";
    public static final String ORDER_STATUS_3 = "3";
    public static final String ORDER_STATUS_4 = "4";


    public static final String NOTLOGIN = "NOTLOGIN";







    /**
     * 分页
     * @param dataList
     * @param pageSize
     * @param pageIndex
     * @return
     */
    public static List<?> page(List<?> dataList, Integer pageSize, Integer pageIndex) {

        //没有分页数据则直接返回不用分页
        if (pageSize == null || pageIndex == null){
            return dataList;
        }

        List currentPageList = new ArrayList<>();
        if (!dataList.isEmpty()) {
            int currIdx = (pageIndex > 1 ? (pageIndex - 1) * pageSize : 0);
            for (int i = 0; i < pageSize && i < dataList.size() - currIdx; i++) {
                Object o = dataList.get(currIdx + i);
                currentPageList.add(o);
            }
        }
        return currentPageList;
    }



}
