package com.cfcc.common.util;

import lombok.extern.slf4j.Slf4j;


import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
public class ZipUtils {

    /**
     * 附件打包
     *
     * @param list     文件路径list
     * @param response
     * @return
     */
    public static HttpServletResponse downFile(List<File> list, HttpServletResponse response) {
        try {
            String zipFiles = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + System.currentTimeMillis() + ".zip"; //根据毫秒数生成一个ZIP文件
            File file = new File(zipFiles);
            if (!file.exists()) {
                file.createNewFile();
            }
            response.reset();//清空response
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
//            zipOutputStream.setEncoding("gbk");  //防止中文文件名称乱码
            zipFiles(list, zipOutputStream);
            zipOutputStream.close();
            fileOutputStream.close();
            return downloadZip(file, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * 打包文件list
     *
     * @param files
     * @param outputStream
     */
    public static void zipFiles(List<File> files, ZipOutputStream outputStream) {
        int size = files.size();
        for (int i = 0; i < size; i++) {
            File file = files.get(i);
            zipFile(file, outputStream);
        }
    }

    /**
     * @param inputFile   //文档路径
     * @param ouputStream
     */
    public static void zipFile(File inputFile, ZipOutputStream ouputStream) {
        try {
            //File路径
            FileInputStream IN = new FileInputStream(inputFile);
            BufferedInputStream bins = new BufferedInputStream(IN, 1024);
            ZipEntry entry = new ZipEntry(inputFile.getName());
            ouputStream.putNextEntry(entry);
            // 向压缩文件中输出数据
            int nNumber;
            byte[] buffer = new byte[1024];
            while ((nNumber = bins.read(buffer)) != -1) {
                ouputStream.write(buffer, 0, nNumber);
            }
            // 关闭创建的流对象
            bins.close();
            IN.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 附件打包数据返回前台
     *
     * @param file
     * @param response
     * @return
     */
    public static HttpServletResponse downloadZip(File file, HttpServletResponse response) {
        try {
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            // 如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes("GB2312"), "ISO8859-1"));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                File f = new File(file.getPath());
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * 创建zip包
     *
     * @param sourcePath 源文件夹地址
     * @param zipPath    指定打包路径
     */
    public static void createZip(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
//            zos.setEncoding("gbk");//此处修改字节码方式。
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            log.error("创建ZIP文件失败", e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("创建ZIP文件失败", e);
            }

        }
    }

    /**
     * 文件夹zip
     *
     * @param file
     * @param parentPath
     * @param zos
     */
    public static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if (file.exists()) {
            if (file.isDirectory()) {//处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                if (files.length != 0) {
                    for (File f : files) {
                        writeZip(f, parentPath, zos);
                    }
                } else { //空目录则创建当前目录
                    try {
                        zos.putNextEntry(new ZipEntry(parentPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                    log.error("创建ZIP文件失败", e);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        log.error("创建ZIP文件失败", e);
                    }
                }
            }
        }
    }

    /**
     * 获取文件/文件夹大小
     *
     * @param file
     * @return
     */
    public static String getFileSize(File file) {
        String size = "";
        if (file.exists() && file.isFile()) {
            long fileSize = file.length();
            size = countFileLength(fileSize);
        } else if (file.exists() && file.isDirectory()) {
            long fileSize = countFilesLength(file);
            size = countFileLength(fileSize);
        } else {
            size = "0KB";
        }
        return size;
    }

    /**
     * 统计文件夹大小
     *
     * @param file
     * @return
     */
    public static long countFilesLength(final File file) {
        final File[] files = file.listFiles();
        long total = 0;
        if (files != null) {
            for (final File child : files) {
                total += child.length();
            }
        }
        return total;
    }

    /**
     * 计算文件大小
     *
     * @param size
     * @return
     */
    public static String countFileLength(long size) {
        String fileSize = "";
        DecimalFormat df = new DecimalFormat("#.00");
        if (size < 1024) {
            fileSize = df.format((double) size) + "BT";
        } else if (size < 1048576) {
            fileSize = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSize = df.format((double) size / 1048576) + "MB";
        } else {
            fileSize = df.format((double) size / 1073741824) + "GB";
        }
        return fileSize;
    }


}
