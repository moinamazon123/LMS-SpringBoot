
package com.maps.yolearn.util.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RAJAKOTA
 */
public class FTPFileUtility {

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
     * @param ftpAddr
     * @param portNumber
     * @param ftpUserName
     * @param password
     * @param ftpSourceFolder
     * @param fTPFileName
     * @return
     */
    public boolean isFTPFileAvailable(String ftpAddr, int portNumber, String ftpUserName, String password, String ftpSourceFolder, String fTPFileName) {

        List<String> listOfFiles = listFTPFile(ftpAddr, portNumber, ftpUserName, password, ftpSourceFolder);

        return listOfFiles.contains(fTPFileName);
    }

    /**
     * @param ftpAddr
     * @param portNumber
     * @param ftpUserName
     * @param ftpSourceFolder
     * @param password
     * @return
     */
    public List<String> listFTPFile(String ftpAddr, int portNumber, String ftpUserName, String password, String ftpSourceFolder) {

        List<String> fileList = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();

        try {

            ftpClient.connect(ftpAddr, portNumber);
            ftpClient.login(ftpUserName, password);
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

    public InputStream retrieveFile(String remoteFilePath, String ftpAddr, int portNumber, String ftpUserName, String password)
            throws IOException {
        FTPClient ftpClient = new FTPClient();
        InputStream inputStream = null;
        try {
            ftpClient.connect(ftpAddr, 21);
            ftpClient.login(ftpUserName, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            inputStream = ftpClient.retrieveFileStream(remoteFilePath);

//            System.out.println("In: " + inputStream);
        } catch (IOException e) {
            e.getCause().getMessage();
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

    public boolean Store(String remoteFilePath, InputStream stream, String ftpAddr, int portNumber, String ftpUserName, String password)
            throws IOException {
        FTPClient ftpClient = new FTPClient();
        boolean b = false;
        try {

            ftpClient.connect(ftpAddr, 21);
            ftpClient.login(ftpUserName, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            b = ftpClient.storeFile(remoteFilePath, stream);

        } catch (IOException e) {
            e.getCause().getMessage();
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

    public boolean uploadFile(String ftpAddr, int portNumber, String ftpUserName, String password, InputStream inputStream, String destPath, String fileName) {

        boolean isUploaded = false;

        FTPClient ftpClient = new FTPClient();

        try {

            ftpClient.connect(ftpAddr, portNumber);
            ftpClient.login(ftpUserName, password);
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

    public String moveFTP_files(String hostIP, int portNumber, String userName, String password,
                                String rootDir, String folder1, String folder2, String fileName) {

        String msg = "";
        boolean b = false;

        try {

            MyFTPClient client = new MyFTPClient(hostIP, userName, password, portNumber);

            /* Check wether the source directory is available or not */
            b = client.checkDirectoryExists(rootDir + folder1);
            if (b == false) {
                msg = rootDir + folder1 + " does not exist.";

            }

            /* Check wether the destination directory is available or not */
            b = client.checkDirectoryExists(rootDir + folder2);
            if (b == false) {
                client.mkdir(rootDir + folder2);
                msg = rootDir + folder2 + " created...";

            } else {
                msg = rootDir + folder2 + " not created...";
            }

            /* List of all files in "rootDir + folder1" */
            FTPFile[] sourceDir = client.getDirecotries(rootDir + folder1 + "/");

            for (FTPFile d : sourceDir) {
                if (d.getName().endsWith(".xml")) {

                    /* move FTP file */
                    boolean move = false;
                    if ((rootDir + folder1 + "/" + d.getName()).equals((rootDir + folder1 + "/" + fileName))) {
                        move = client.moveFiles(rootDir + folder1 + "/" + d.getName(), rootDir + folder2 + "/" + d.getName());
                        msg = "uploaded!";
//                        System.out.println("msg:::" + msg);
                    }
                }
            }
        } catch (IOException e) {
            msg = e.getCause().getMessage();

        }
        return msg;
    }

    /* method for deleting FTP file from a particular directory */
    public String deleteFTP_files(String hostIP, int portNumber, String userName, String password,
                                  String rootDir, String folder1, String fileName) {

        String msg = "";

        try {

            MyFTPClient client = new MyFTPClient(hostIP, userName, password, portNumber);

            /* List of all files in "rootDir + folder1" */
            FTPFile[] sourceDir = client.getDirecotries(rootDir + folder1 + "/");

            for (FTPFile d : sourceDir) {
                String i = d.getName();
//                System.out.println("name is :" + i);
                if (d.getName().endsWith(".xml")) {

                    if ((rootDir + folder1 + "/" + d.getName()).equals((rootDir + folder1 + "/" + fileName))) {

                        boolean b = client.deleteFile(rootDir + folder1 + "/" + fileName);
//                        System.out.println("Why B  :" + b);
                        msg = fileName + " deleted successfully.";

                    }
                } else {

//                    System.out.println("two :" + rootDir + folder1 + "/" + d.getName());
                    msg = "it is not xml file";
                }
            }

        } catch (Exception e) {
            msg = "Some problem occurred while deleting the file.";

        }
        return msg;
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
