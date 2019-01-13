package d.xposedshare.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHookLoad implements IXposedHookLoadPackage {
    public static String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        //系统的演示用
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getDeviceId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("lsw beforeHookedMethod");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        methodHookParam.setResult("这是被修改的数据imei");
                        XposedBridge.log("lsw Hook device id is successful!!! ");
                    }
                });

        //第三方App，支付宝演示
        if (ALIPAY_PACKAGE.equals(ALIPAY_PACKAGE)) {

            // hook设置金额和备注的onCreate方法，自动填写数据并点击
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Field jinErField = XposedHelpers.findField(param.thisObject.getClass(), "b");
                    final Object jinErView = jinErField.get(param.thisObject);
                    Field beiZhuField = XposedHelpers.findField(param.thisObject.getClass(), "c");
                    final Object beiZhuView = beiZhuField.get(param.thisObject);

                    Intent intent = ((Activity) param.thisObject).getIntent();
                    String mark = intent.getStringExtra("mark");
                    String money = intent.getStringExtra("money");
                    //设置支付宝金额和备注
                    XposedHelpers.callMethod(jinErView, "setText", money);
                    XposedHelpers.callMethod(beiZhuView, "setText", mark);
                    //点击确认
                    Field quRenField = XposedHelpers.findField(param.thisObject.getClass(), "e");
                    final Button quRenButton = (Button) quRenField.get(param.thisObject);
                    quRenButton.performClick();
                }
            });

            // hook获得二维码url的回调方法
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", lpparam.classLoader, "a",
                    XposedHelpers.findClass("com.alipay.transferprod.rpc.result.ConsultSetAmountRes", lpparam.classLoader), new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Field moneyField = XposedHelpers.findField(param.thisObject.getClass(), "g");
                            String money = (String) moneyField.get(param.thisObject);

                            Field markField = XposedHelpers.findField(param.thisObject.getClass(), "c");
                            Object markObject = markField.get(param.thisObject);
                            String mark = (String) XposedHelpers.callMethod(markObject, "getUbbStr");

                            Object consultSetAmountRes = param.args[0];
                            Field consultField = XposedHelpers.findField(consultSetAmountRes.getClass(), "qrCodeUrl");
                            String payUrl = (String) consultField.get(consultSetAmountRes);
                            XposedBridge.log(money + "  " + mark + "  " + payUrl);
                        }
                    });


            //这是重点强调：打印日志的逆向风险
            Class<?> clazzLog = lpparam.classLoader.loadClass("com.alipay.mobile.verifyidentity.log.VerifyLogCat");
            XposedHelpers.findAndHookMethod(clazzLog, "i", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("lsw args====VerifyLogCat==i==" + param.args[0] + "  " + param.args[1]);
                }
            });

            XposedHelpers.findAndHookMethod(clazzLog, "d", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("lsw args====VerifyLogCat==d==" + param.args[0] + "  " + param.args[1]);
                }
            });

            XposedHelpers.findAndHookMethod(clazzLog, "e", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("lsw args====VerifyLogCat==e==" + param.args[0] + "  " + param.args[1]);
                }
            });
        }
    }
}
