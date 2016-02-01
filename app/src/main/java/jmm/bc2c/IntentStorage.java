package jmm.bc2c;

import android.content.Intent;

public class IntentStorage {

    public static Intent PhotoIntentData;

    public static String CurrentPhotoPath;
    public static String CroppedNamePath;
    public static String CroppedPhonePath;

    public static int CroppedNameX;
    public static int CroppedNameY;
    public static int CroppedNameW;
    public static int CroppedNameH;

    public static int CroppedPhoneX;
    public static int CroppedPhoneY;
    public static int CroppedPhoneW;
    public static int CroppedPhoneH;

    public final static String CameraTempFile = "temp.jpg";
    public final static String CroppedNameFile = "name.jpg";
    public final static String CroppedPhoneFile = "phone.jpg";
}
