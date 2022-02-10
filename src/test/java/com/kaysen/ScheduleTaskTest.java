package com.kaysen;

import com.kaysen.task.ScheduleTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScheduleTaskTest {
    @Autowired
    private ScheduleTask scheduleTask;

    /**
     * 测试定时更新所有新闻数据
     */
//    @Test
    public void updateAllNewsTest(){
        scheduleTask.updateAllNews();
    }
}
