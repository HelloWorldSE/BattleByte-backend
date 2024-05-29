package com.battlebyte.battlebyte.oj;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
public class ProblemContainer {
    Data data;
}

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
class Data {
    List<Result> results;
}

@lombok.Data
class CreatedBy {
    int id;
    String username;
}

@lombok.Data
class Sample {
    String input;
    String output;
}

@lombok.Data
class TestCaseScore {
    int score;
    String input_name;
    String output_name;
}

@lombok.Data
class StatisticInfo {
    int zero;
}

@lombok.Data
class Result {
    int id;
    List<String> tags;
    CreatedBy created_by;
    Integer _id;
    boolean is_public;
    String title;
    String description;
    String input_description;
    String output_description;
    List<Sample> samples;
    String test_case_id;
    List<TestCaseScore> test_case_score;
    String hint;
    List<String> languages;
    Object template; // Assuming it can be of various types
    String create_time;
    int time_limit;
    int memory_limit;
    IoMode io_mode;
    boolean spj;
    boolean spj_compile_ok;
    String rule_type;
    boolean visible;
    String difficulty;
    String source;
    int total_score;
    int submission_number;
    int accepted_number;
    StatisticInfo statistic_info;
    boolean share_submission;

    public boolean haveTag(String tag) {
        for (String tag1: tags) {
            if (Objects.equals(tag1, tag)) {
                return true;
            }
        }
        return false;
    }
}

@lombok.Data
class IoMode {
    String input;
    String output;
    String io_mode;
}
