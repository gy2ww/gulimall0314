package com.gy.util;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by gaoyong on 2020/3/28.
 */
@Configuration
public class FastdfsUtil {

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    public String fileUpload(MultipartFile multipartFile){

        String imgUrl = "http://192.168.0.8";
        //获取配置文件
        String path = FastdfsUtil.class.getResource("/tracker.conf").getPath();

        try {
            //创建与fastdfs的连接
            ClientGlobal.init(path);
            //创建trackerClient
            TrackerClient trackerClient = new TrackerClient();
            //创建trackerServer
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            //创建storageClient
            StorageClient storageClient = new StorageClient(trackerServer,null);
            //获取文件的二进制字节
            byte[] bytes = multipartFile.getBytes();
            //获取上传的图片的文件名
            String originalFilename = multipartFile.getOriginalFilename();
            //后缀名
            int i = originalFilename.lastIndexOf(".");
            String substring = originalFilename.substring(i + 1);
            //上传
            String[] url = storageClient.upload_file(bytes, substring, null);
            //url拼接
            for (String s : url) {
                imgUrl+="/"+s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
