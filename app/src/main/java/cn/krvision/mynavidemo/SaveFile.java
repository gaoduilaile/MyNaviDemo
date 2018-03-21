package cn.krvision.mynavidemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by Administrator on 2017/7/17.
 */

public class SaveFile {

    /**
     * 从文件中读取数据
     *
     * @return 从文件中读取的数据
     */
    public static String readAssetsTxt(Context context, String filename) {
        String result = "";
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
//            result = new String(buffer, "utf-8");
            result = new String(buffer, "Utf-8");
            // Finally stick the string into the text view.
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return result;

    }


    /*获取excel表格中的数据不能在主线程中调用
xlsName 为表格的名称
index 表示第几张表格
*/
    public static ArrayList<ExcelBean> getExcelData(Context context, String xlsName, int index) {
        ArrayList<ExcelBean> list = new ArrayList<>();
        //获取文件管理器
        AssetManager assetManager = context.getAssets();
        try {
            Workbook workbook = Workbook.getWorkbook(assetManager.open(xlsName));
            Sheet sheet = workbook.getSheet(index);
            //表格一共有多少行
            int sheetRows = sheet.getRows();
            //将数据添加到集合中
            for (int i = 0; i < sheetRows; i++) {
                ExcelBean bean = new ExcelBean();
                //获取列的数据
//                bean.setName(sheet.getCell(0, i).getContents());
//                bean.setAction(sheet.getCell(1, i).getContents());
//                NumberCell cell1 = (NumberCell) sheet.getCell(2, i);
//                NumberCell cell2 = (NumberCell) sheet.getCell(3, i);
//                bean.setLatitude( cell1.getValue());
//                bean.setLongitude(cell2.getValue());
//                bean.setId(Integer.parseInt(sheet.getCell(4, i).getContents()));
//
//                LogUtils.e("getExcelData ", cell1.getValue() + "  " + cell2.getValue());
//
//                list.add(bean);


                NumberCell cell1 = (NumberCell) sheet.getCell(0, i);
                NumberCell cell2 = (NumberCell) sheet.getCell(1, i);
                bean.setLatitude( cell1.getValue());
                bean.setLongitude(cell2.getValue());
                LogUtils.e("getExcelData ", cell1.getValue() + "  " + cell2.getValue());

                list.add(bean);
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}