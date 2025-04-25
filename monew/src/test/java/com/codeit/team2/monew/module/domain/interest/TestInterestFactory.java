package com.codeit.team2.monew.module.domain.interest;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.List;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestInterestFactory {

    public static Interest create(String name, List<String> keywords) {
        Interest interest = Interest.create(name);
        for (String keyword : keywords) {
            interest.addKeyword(new Keyword(keyword));
        }
        ReflectionTestUtils.setField(interest, "id", UUID.randomUUID());
        return interest;
    }

}
