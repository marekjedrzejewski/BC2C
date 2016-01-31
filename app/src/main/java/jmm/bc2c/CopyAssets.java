package jmm.bc2c;


import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.app.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyAssets {

    AssetManager assetManager = null;


    public CopyAssets(AssetManager am){
        this.assetManager = am;
    }

    public Boolean AllTessdataPresent()
    {
        String[] files = null;
        String dir = "tessdata";
        try {
            files = assetManager.list(dir);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null)
            for (String filename : files) {
                File file = new File(MainActivity.DATA_PATH, dir + "/" + filename);
                if(!file.exists())
                {
                    return false;
                }
            }
        return true;
    }

    public void CopyTessdata(){
        copyFileOrDir("tessdata");
    }

    private void copyFileOrDir(String path) {
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath =  MainActivity.DATA_PATH + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = MainActivity.DATA_PATH + filename.substring(0, filename.length()-4);
            else
                newFileName = MainActivity.DATA_PATH + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

    }


}

class CheckAssetsTask extends AsyncTask<Void,Void,Boolean>{

    MainActivity mainActivity = null;

    public CheckAssetsTask(MainActivity ma){
        mainActivity = ma;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainActivity.LaunchProgressDialog("Please wait...", "Checking langauge files");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        CopyAssets ca = new CopyAssets(mainActivity.getAssets());
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Boolean dataPresent = ca.AllTessdataPresent();
        return dataPresent;
    }

    @Override
    protected void onPostExecute(Boolean dataPresent) {
        super.onPostExecute(dataPresent);
        mainActivity.KillProgressDialog();
        if(!dataPresent){
            mainActivity.PerformCopyingAssets();
        }
    }

}

class CopyAssetsTask extends AsyncTask<Void, Void, Void>{

    MainActivity mainActivity = null;
    public CopyAssetsTask(MainActivity ma){
        this.mainActivity = ma;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainActivity.LaunchProgressDialog("Please wait...", "Copying language files");
    }

    @Override
    protected Void doInBackground(Void... params) {
        CopyAssets ca = new CopyAssets(mainActivity.getAssets());
        ca.CopyTessdata();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mainActivity.KillProgressDialog();
    }
}
