package com.gy;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPassportWebApplicationTests {

	public  void main(String[] args) throws IOException {

		String ip = "";
		 contextLoads();

	}
	@Test
	public  void contextLoads() throws IOException {

		String key =  "ipName";
		String url = "D:\\puhui-gitspace\\gulimall0314\\gmall-passport-web\\src\\main\\resources\\application.properties";
		String ip = Strings.EMPTY;
			Properties properties = new Properties();
			// 使用InPutStream流读取properties文件
			BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
			properties.load(bufferedReader);
			// 获取key对应的value值
		String property = properties.getProperty(key);

	}

}
