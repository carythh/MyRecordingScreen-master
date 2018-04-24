package com.gson8.myscreenshot.Tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import android.widget.EditText;


import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * 项目上用到的工具类
 */
public class LMTool {

    public static Context context;
    public LMTool(Context context) {
        this.context = context;
    }


    /**
     * 加载进度条
     **/
    private static CustomProgressDialog progressDialog = null;
    /**
     * 现在的activity
     **/
    public static Activity NowActivity;

    public static void ShowDialog() {

        if (LMTool.NowActivity != null) {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    NowActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                startProgressDialog();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }).start();

        }

    }

    public static void DismissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                NowActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopProgressDialog();
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    private static void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(LMTool.NowActivity);
//            progressDialog.setMessage("正在加载中...");
        }

        progressDialog.show();
    }

    private static void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public String IMEI() {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static String versionName() {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return null;
    }







    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return [0-9]{5,9}
     */
    public boolean getuserphoenistrue(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("(1)[34578][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return [0-9]{5,9}
     */
    public boolean getuserphoenistrue(EditText mobiles) {

        return getuserphoenistrue(mobiles.getText().toString());
    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */

    public String checkNetworkState() {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            //2.获取当前网络连接的类型信息
            int networkType = networkInfo.getType();
            if (ConnectivityManager.TYPE_WIFI == networkType) {
                return "WIFI";
            } else if (ConnectivityManager.TYPE_MOBILE == networkType) {
                return "MOBILE";
            }
        }
        return "不详";
    }

    /***
     * 清空空格以及回车等
     ****/
    public static String getTrueString(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }

        if (dest.startsWith("\ufeff")) {
            return str.substring(1);
        } else {
            return dest;
        }

    }

    /**
     * 延时一秒弹出版本更新，以防止数据加载的慢出不来服务器数据
     *
     * @param s 延时的秒数
     */
//    public void times(int s) {
//        if (LMApplication.sysConfig_mode != null) {
//            new Handler().postDelayed(new Runnable() {
//                public void run() {
//                    pudate();
//                }
//            }, s);
//
//        }
//    }


}
