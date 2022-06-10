package com.maps.yolearn.util.ftp;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * @author PREMNATH
 */
public class MyFTPClient {

    private FTPClient ftpClient = null;

    public MyFTPClient(String hostIP, String userName, String password, int portNumber) {
        ftpClient = new FTPClient();
        try {
            ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
            int reply;
            ftpClient.connect(hostIP, portNumber);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new Exception("Exception in connecting to FTP Server");
            }
            ftpClient.login(userName, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

        } catch (Exception exception) {
            exception.getMessage();
            ftpClient = null;
        }
    }

    public void disconnect() {
        if (this.ftpClient != null && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            } catch (IOException iOException) {
                iOException.getMessage();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.disconnect();
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public String getFileContents(String remoteFileName) throws IOException {
        InputStream iStream = ftpClient.retrieveFileStream(remoteFileName);
        BufferedInputStream bInf = new BufferedInputStream(iStream);

        int bytesRead;
        byte[] buffer = new byte[1024];
        String fileContent = null;
        while ((bytesRead = bInf.read(buffer)) != -1) {
            fileContent = new String(buffer, 0, bytesRead);
        }

        return fileContent;
    }

    public FTPFile[] getDirecotries(String parentDirecotry) {
        FTPFile[] fTPFiles;
        try {
            fTPFiles = ftpClient.listFiles(parentDirecotry);
        } catch (IOException exception) {
            fTPFiles = null;
            exception.getMessage();
        }
        return fTPFiles;
    }

    public boolean checkDirectoryExists(String dirPath) throws IOException {
        int returnCode;
        ftpClient.changeWorkingDirectory(dirPath);
        returnCode = ftpClient.getReplyCode();

        if (returnCode == 550) {
            return false;
        } else {
            return true;
        }

    }

    public boolean mkdir(String newDir) throws IOException {
        boolean created = ftpClient.makeDirectory(newDir);
        return created;
    }

    public boolean moveFiles(String from, String to) throws IOException {
        boolean b;
        try {
            ftpClient.rename(from, to);
            b = true;
        } catch (IOException exception) {
            exception.getMessage();
            b = false;
        }
        return b;
    }

    public boolean deleteFile(String remoteFileName) {
        boolean b;
        try {
            this.ftpClient.dele(remoteFileName);
            b = true;
        } catch (IOException exception) {
            exception.getMessage();
            b = false;
        }
        return b;
    }

    public InputStream getInputStream(String fileName) throws IOException {

        InputStream inputStream = ftpClient.retrieveFileStream(fileName);

        return inputStream;

    }

}
