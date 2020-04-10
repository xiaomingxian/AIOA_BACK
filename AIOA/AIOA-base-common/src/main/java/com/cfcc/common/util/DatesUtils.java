package com.cfcc.common.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ClassName:DatesUtils
 * Package:com.cfcc.common.util
 * Description:<br/>
 *
 * @date:2020/4/3
 * @author:
 */
public class DatesUtils {
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static long getDayCoincidence(Date begindate1,Date enddate1,Date begindate2,Date enddate2){
        long b1=begindate1.getTime();
        long e1=enddate1.getTime();
        long b2=begindate2.getTime();
        long e2=enddate2.getTime();
        assert(b1<e1&&b2<e2);
        long coincidenceday;
        if(b1<=b2&&e1>=e2){//（b1---【b2-----e2】--e1）
            coincidenceday=getDayDifference(enddate2,begindate2);
        }else if(b1>=b2&&e1<=e2){//【b2---（b1-----e1）--e2】
            coincidenceday=getDayDifference(enddate1,begindate1);
        }else if(b1>=b2&&b1<=e2&&e2<=e1){//【b2---(b1---e2】----e1)
            coincidenceday=getDayDifference(enddate2,begindate1);
        }else if(b1<=b2&&e1<=e2&&e1>=b2){//（b1---【b2---e1）----e2】
            coincidenceday=getDayDifference(enddate1,begindate2);
        }else if(e1<=b2||b1>=e2){
            coincidenceday=0;
        }else{
            coincidenceday=0;
            // System.out.println("意料外的日期组合，无法计算重合天数！");
        }
        //  System.out.println("重合天数为["+coincidenceday+"]天。");
        return coincidenceday;
    }

    /**
     * 计算两个日期的相差天数(d1-d2)
     * @param d1
     * @param d2
     * @return
     */
    public static long getDayDifference(Date d1,Date d2){
        long num = (d1.getTime()-d2.getTime())/1000;
        long days  = num/(3600*24);
        if(days<0)days=days*-1;
        return days;
    }

    public static Date stringToDate(String strDate) {
        if (strDate==null){return null;}
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String getThisMonth()
    {
        // 本月的第一天
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DATE, 1);
        SimpleDateFormat simpleFormate = new SimpleDateFormat("yyyy-MM-dd");
        String fd = simpleFormate.format(calendar.getTime());
        // 本月的最后一天
        calendar.set( Calendar.DATE,  1 );
        calendar.roll(Calendar.DATE,  - 1 );
        String ld = simpleFormate.format(calendar.getTime());
        return fd+","+ld;
    }
}
