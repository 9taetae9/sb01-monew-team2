package com.codeit.team2.monew.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "app.timezone=Asia/Seoul")
@ActiveProfiles("test")
class TimeConfigTest {

    @Autowired
    private ZoneId zoneId;

    @Autowired
    private String timezoneId;

    @Test
    void testTimeConfigBeans() {
        assertNotNull(zoneId);
        assertEquals(ZoneId.of("Asia/Seoul"), zoneId);

        assertNotNull(timezoneId);
        assertEquals("Asia/Seoul", timezoneId);
    }
}
