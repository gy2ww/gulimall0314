package com.gy;

import org.csource.common.MyException;
import org.csource.fastdfs.*;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

	@Test
    public void contextLoads() throws IOException, MyException {
		//读取tracker.conf配置文件
		String path = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
		//创建与fastdfs的连接
		ClientGlobal.init(path);
         //创建trackerClient
		TrackerClient trackerClient = new TrackerClient();
		//获取trackerServer
		TrackerServer trackerServer = trackerClient.getTrackerServer();
        //创建storageClient
		StorageClient storageClient = new StorageClient(trackerServer,null);
		//上传图片,fastdfs会把url路径里的东西以数组的形式返回[group1, M00/00/00/wKgCbl5-Bl-EF9fcAAAAAKuSSV8580.jpg]
        String[] str = storageClient.upload_appender_file("C:/Users/Administrator/Desktop/Me.jpg","jpg",null);
       // http://192.168.0.8/group1/M00/00/00/wKgCbl5-C2eEKryOAAAAAKuSSV8090.jpg
        //http://192.168.0.8/group1/M00/00/00/wKgCbl5-C--EAm6NAAAAAKuSSV8660.jpg
        //前缀
        String url = "http://192.168.0.8";
        for (String s : str) {
          url=url+"/"+s;
        }
        System.out.println(url);
    }

}
