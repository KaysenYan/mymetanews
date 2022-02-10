package com.kaysen;

import com.kaysen.domain.TianXingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class NewsServiceTest {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${newsapp.tianxing-key}")
    private String key;

//    @Test
    public void updateAllNewsTest(){
        String url = "http://api.tianapi.com/caijing/index?key="+key+"&num=10&page=1";
        ResponseEntity<TianXingData> responseEntity = restTemplate.getForEntity(url, TianXingData.class);
        System.out.println(responseEntity.getBody().getNewslist());
    }
}
