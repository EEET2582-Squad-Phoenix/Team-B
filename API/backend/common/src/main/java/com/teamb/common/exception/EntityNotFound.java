package com.teamb.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class EntityNotFound extends RuntimeException {

    private Map<String, Object> details;

    public EntityNotFound(Object... details) {
        super("No item found with provided information");
        assert details.length % 2 == 0;
        Map<String, Object> detailsMap = new HashMap<>();
        for (int i = 0; i < details.length; i += 2) {
            assert details[i] instanceof String;
            detailsMap.put((String) details[i], details[i+1]);
        }
        this.details = detailsMap;
    }
}
