package com.fxly.creatsms.adapter;

/**
 * Created by Lambert Liu on 2016-09-23.
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public  class Get {

    public static final String SHAREDFILENAME="setting_data";
    public  static  long time_day,time_year,days,time_hor,time_min;


    public static long gettime( Context context) {
        SharedPreferences mysharedpreferences = context.getSharedPreferences(SHAREDFILENAME, Activity.MODE_PRIVATE);
        int    year=mysharedpreferences.getInt("year",1);
        int     month=mysharedpreferences.getInt("month",1);
        int    day=mysharedpreferences.getInt("day",1);
        int    hour=mysharedpreferences.getInt("hour",1);
        int    minute=mysharedpreferences.getInt("minute",1);
        //计算公式=年*天数*24小时*每小时毫秒数（60*60*1000）
         time_year=(long) (year-1970)*365*24*3600000+get_Run_Days(context)*24*3600000;  //31536000000;
        days=(long)(day-1)*24*3600000;
        time_hor=(long)hour*3600000;
        time_min=(long)minute*60000;
        long time=(long)(time_year+getTime_day(context)+days+time_hor+time_min+10*24*3600000+4*3600000);// 年 月 日 小时  分
        return  time;
    }
//    1473646860000
//    1474612224576


    public static long getTime_day(Context context) {
        SharedPreferences mysharedpreferences = context.getSharedPreferences(SHAREDFILENAME, Activity.MODE_PRIVATE);
        int    year=mysharedpreferences.getInt("year",1);
        int     month=mysharedpreferences.getInt("month",1);

        if((year % 400 == 0) || (year % 4 == 0) && (year % 100 != 0)){
            //闰年 366天
            if(month==1){time_day=0;}
            else if(month==2){time_day=(long)31*24*3600000;}
            else if(month==3){time_day=(long)60*24*3600000;}
            else if(month==4){time_day=(long)91*24*3600000;}
            else if(month==5){time_day=(long)121*24*3600000;}
            else if(month==6){time_day=(long)152*24*3600000;}
            else if(month==7){time_day=(long)182*24*3600000;}
            else if(month==8){time_day=(long)213*24*3600000;}
            else if(month==9){time_day=(long)244*24*3600000;}
            else if(month==10){time_day=(long)274*24*3600000;}
            else if(month==11){time_day=(long)305*24*3600000;}
            else if(month==12){time_day=(long)335*24*3600000;}
        }else {
            //不是闰年 365天
            if(month==1){time_day=0;}
            else if(month==2){time_day=(long)31*24*3600000;}
            else if(month==3){time_day=(long)59*24*3600000;}
            else if(month==4){time_day=(long)90*24*3600000;}
            else if(month==5){time_day=(long)120*24*3600000;}
            else if(month==6){time_day=(long)151*24*3600000;}
            else if(month==7){time_day=(long)181*24*3600000;}
            else if(month==8){time_day=(long)212*24*3600000;}
            else if(month==9){time_day=(long)243*24*3600000;}
            else if(month==10){time_day=(long)273*24*3600000;}
            else if(month==11){time_day=(long)304*24*3600000;}
            else if(month==12){time_day=(long)334*24*3600000;}
        }

        long time=time_day;
        return time;
    }


    public static long get_Run_Days(Context context) {
        SharedPreferences mysharedpreferences = context.getSharedPreferences(SHAREDFILENAME, Activity.MODE_PRIVATE);
        int    endYear=mysharedpreferences.getInt("year",1);
        int sum = 0;
        int startYear= 1970;
        while(startYear<=endYear ){
            if( (startYear%4==0 && startYear%100==0) ){

                sum++; //满足条件的就是闰年
            }
            startYear++;//继续循环
        }

        return sum;
    }
}
