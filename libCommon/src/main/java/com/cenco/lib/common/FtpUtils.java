package com.cenco.lib.common;

import com.cenco.lib.common.log.LogUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/9/11.
 */

public class FtpUtils {

    private String TAG = FtpUtils.class.getSimpleName();

    private String url;
    private String username;
    private String password;
    private int port;
    private String remotePath;
    private FtpListener listener;



    public FtpUtils(String remotePath) {
        this("119.188.98.7", "ftpssp", "kuaifa4006299139", 41431, remotePath);
    }
    public FtpUtils(String url, String username, String password, int port, String remotePath) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.port = port;
        this.remotePath = remotePath;
    }

    public void setListener(FtpListener listener) {
        this.listener = listener;
    }

    /**
     * 上传
     * @param localFilePath

     */
    public void uploadFile(final String localFilePath){
        File file = new File(localFilePath);
        uploadFile(localFilePath,file.getName());
    }
    public boolean uploadFiles(final String localFilePath) throws IOException {
        File file = new File(localFilePath);
        boolean b = uploadFiles(localFilePath, file.getName());
        return b;
    }
    public boolean uploadFiles(String localPath,String name) throws IOException {
        File file = new File(localPath);
        if (!file.exists()){
            return false;
        }
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(url ,port);
        boolean login = ftpClient.login(username, password);
        if (!login){
            return false;
        }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        FileInputStream fis = new FileInputStream(localPath);
        String dstFilePath = remotePath +"/"+ name;
        LogUtils.d(TAG,"远程路径:"+dstFilePath);
        createMultiDirectory(ftpClient,remotePath);
        ftpClient.changeWorkingDirectory(remotePath);
        ftpClient.enterLocalPassiveMode();
        ftpClient.deleteFile(dstFilePath);
        ftpClient.storeFile(dstFilePath,fis);

        fis.close();
        ftpClient.disconnect();
        return true;
    }

    public void uploadFile(final String localFilePath,final String filePath){
        ThreadManager.getPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    File file = new File(localFilePath);
                    if (!file.exists()){
                        if (listener!=null){
                            listener.onFtpError("文件不存在");
                        }
                        return;
                    }

                    FTPClient ftpClient = new FTPClient();
                    ftpClient.connect(url ,port);
                    boolean login = ftpClient.login(username, password);
                    if (login){
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        FileInputStream fis = new FileInputStream(localFilePath);
                        String dstFilePath = remotePath +"/"+ filePath;
                        LogUtils.d(TAG,"远程路径:"+dstFilePath);
                        createMultiDirectory(ftpClient,remotePath);
                        ftpClient.changeWorkingDirectory(remotePath);
                        ftpClient.enterLocalPassiveMode();
                        ftpClient.deleteFile(dstFilePath);
                        ftpClient.storeFile(dstFilePath,fis);

                        fis.close();
                        //退出登陆FTP，关闭ftpCLient的连接
//                        ftpClient.logout();
                        ftpClient.disconnect();

                        if (listener!=null){
                            listener.onFtpSuccess();
                        }

                    }else {
                        if (listener!=null){
                            listener.onFtpError("登录失败");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener!=null){
                        listener.onFtpError("发生错误");
                    }
                }
            }
        });

    }

    private static boolean createMultiDirectory(FTPClient ftpClient, String multiDirectory) {
        boolean bool = false;

        try {
            String[] dirs = multiDirectory.split("/");
            ftpClient.changeWorkingDirectory("/");

            for(int i = 1; dirs != null && i < dirs.length; ++i) {
                if(!ftpClient.changeWorkingDirectory(dirs[i])) {
                    if(!ftpClient.makeDirectory(dirs[i])) {
                        return false;
                    }

                    if(!ftpClient.changeWorkingDirectory(dirs[i])) {
                        return false;
                    }
                }
            }

            bool = true;
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return bool;
    }

    public interface FtpListener{
        void onFtpError(String error);
        void onFtpSuccess();
    }
}
