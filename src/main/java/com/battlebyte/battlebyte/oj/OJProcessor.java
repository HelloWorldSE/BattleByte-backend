package com.battlebyte.battlebyte.oj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
public class OJProcessor {
    private ProblemContainer problemContainer;

    public OJProcessor(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        problemContainer = objectMapper.readValue(json, ProblemContainer.class);
    }

    public ProblemContainer search(String tag) {
        ArrayList<Result> results = new ArrayList<>();
        for (Result result: problemContainer.getData().getResults()) {
            if (result.haveTag(tag)) {
                results.add(result);
            }
        }
        return new ProblemContainer(new Data(results));
    }


}
