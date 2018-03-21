package cn.krvision.mynavidemo;

/**
 * Created by gaoqiong on 2018/3/20
 */

public class JniKit {
    static{
        System.loadLibrary("jhello");
    }
//    public static native int helloFromC(double lat, double lon);
    //jni接口函数
    private static native long createNativeObject();
    private static native boolean CheckSB(long personAddr);

    //java层封装函数
    long nativePerson;
    public JniKit(){
        nativePerson = createNativeObject();
    }

    public boolean JniCheckSB()
    {
        return CheckSB(nativePerson);
    }
}
