package com.maps.yolearn.util.ftp;

import com.maps.yolearn.constants.Constants;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PREMNATH
 */
public class FTPServer {

    /**
     * @param ftpClient
     * @param dirPath
     * @return
     * @throws IOException
     */
    public static boolean makeDirectories(FTPClient ftpClient, String dirPath)
            throws IOException {

        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {

            for (String singleDir : pathElements) {

                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {

                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        return false;
                    }

                }

            }

        }

        return true;
    }

    /**
     * @param ftpSourceFolder
     * @param fTPFileName
     * @return
     */
    public boolean isFTPFileAvailable(String ftpSourceFolder, String fTPFileName) {

        List<String> listOfFiles = listFTPFile(ftpSourceFolder);

        return listOfFiles.contains(fTPFileName);
    }

    /**
     * @param ftpSourceFolder
     * @return
     */
    public List<String> listFTPFile(String ftpSourceFolder) {

        List<String> fileList = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();

        try {

            ftpClient.connect(Constants.FTP_ADDRESS, Constants.FTP_PORT);
            ftpClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();

            FTPFile[] files = ftpClient.listFiles(ftpSourceFolder);

            for (FTPFile file : files) {
                String fname = file.getName();

                if (!fname.equals(".") && !fname.equals("..")) {

                    fileList.add(fname);

                }
            }

        } catch (IOException e) {
        } finally {

            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }

        }

        return fileList;
    }

    /**
     * @param fileName
     * @return
     */
    public int deleteFTPFile(String fileName) {

        int x = 0;

        FTPClient ftpClient = new FTPClient();

        try {

            ftpClient.connect(Constants.FTP_ADDRESS, Constants.FTP_PORT);
            ftpClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();

            //*If the ftpfile is deleted from the server then it will return 250
            //*If the required ftpfile is not present then obviously it won't delete the file and will return 550
            x = ftpClient.dele(fileName);

        } catch (IOException e) {

            x = 0;

        } finally {

            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {

                }
            }

        }

        return x;
    }

    /**
     * @param inputStream
     * @param destPath
     * @param fileName
     * @return
     */
    public boolean uploadFile(InputStream inputStream, String destPath, String fileName) {

        boolean isUploaded = false;

        FTPClient ftpClient = new FTPClient();

        try {

            ftpClient.connect(Constants.FTP_ADDRESS, Constants.FTP_PORT);
            ftpClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();

            makeDirectories(ftpClient, destPath);

            uploadSingleFile(ftpClient, inputStream, destPath, fileName);

            isUploaded = true;

        } catch (IOException e) {

            isUploaded = false;

        } finally {

            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    isUploaded = false;
                }
            }

        }

        return isUploaded;
    }

    /**
     * @param ftpClient
     * @param inputStream
     * @param remoteFilePath
     * @param fileName
     * @return
     * @throws IOException
     */
    public boolean uploadSingleFile(FTPClient ftpClient, InputStream inputStream, String remoteFilePath, String fileName) throws IOException {

        boolean b = false;
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.makeDirectory(remoteFilePath);
            b = ftpClient.storeFile(remoteFilePath + "/" + fileName, inputStream);
            return b;
        } catch (IOException e) {
            return b;
        } finally {
            inputStream.close();
        }
    }

    public boolean createDir(String dirPath) throws IOException {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(Constants.FTP_ADDRESS, Constants.FTP_PORT);
            ftpClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();

            return makeDirectories(ftpClient, dirPath);

        } catch (IOException e) {
            throw e;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * @param destinationDir
     * @param from
     * @param to
     * @return
     */
    public boolean moveFile(String destinationDir, String from, String to) {

        boolean b = false;

        FTPClient ftpClient = new FTPClient();

        try {

            ftpClient.connect(Constants.FTP_ADDRESS, Constants.FTP_PORT);
            ftpClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();

            makeDirectories(ftpClient, destinationDir);

            b = ftpClient.rename(from, to);

        } catch (IOException e) {
            b = false;
        } finally {

            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }

        }

        return b;
    }

    /**
     * @param remoteFilePath
     * @return
     * @throws IOException
     */
    public InputStream retrieveFile(String remoteFilePath)
            throws IOException {
        FTPClient ftpClient = new FTPClient();
        InputStream inputStream = null;
        try {
            ftpClient.connect(Constants.FTP_ADDRESS, Constants.FTP_PORT);
            ftpClient.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            inputStream = ftpClient.retrieveFileStream(remoteFilePath);

//            System.out.println("In: " + inputStream);
        } catch (IOException e) {
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }
        }
        return inputStream;
    }

    public Long getfilesize(String ftpAddr, String ftpUserName, String password, String destinationPath) {

        FTPClient ftpClient = new FTPClient();
        long size = 0;
        try {
            ftpClient.connect(ftpAddr, 21);
            ftpClient.login(ftpUserName, password);

            String filePath = destinationPath;

            FTPFile file = ftpClient.mlistFile(filePath);
            size = file.getSize();

//            System.out.println("File size = " + size);
            ftpClient.sendCommand("SIZE", filePath);
            String reply = ftpClient.getReplyString();
//            System.out.println("Reply for size command: " + reply);

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException ex) {
            ex.getCause().getMessage();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ex) {

                    ex.getCause().getMessage();
                }
            }
        }
        return size;

    }

}
