package com.kiyeon.spring.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j()
@Configuration
@RequiredArgsConstructor
public class LoopJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditionalJob() {
        return jobBuilderFactory.get("conditionalLoopJob")
                .start(conditionalJobStep2())
                .on("FAILED")// Step1이 FAILED일 경우
                .to(conditionalJobStep3())// Step3으로 이동한다.
                .on("*")// Step3의 결과 관계 없이
                .end()//Step3으로 이동하면 Flow가 종료된다.
                .from(conditionalJobStep1())//Step1로부터
                .on("*")//FAILED 외의 모든 경우
                .to(conditionalJobStep3())//Step 2로 이동한다.
                .next(conditionalJobStep2())//Step 2가 정상 종료하면, Step3으로 이동한다.
                .on("*")//Step 3의 결과와 관계 없이
                .to(conditionalJobStep1())
                .on("*")
                .end()//Step3으로 이동하면 Flow가 종료된다.
                .end()//Job 종료
                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step1");

                    /**
                     ExitStatus를 FAILED로 지정한다.
                     해당 status를 보고 flow가 진행된다.
                     **/
                    contribution.setExitStatus(ExitStatus.FAILED);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep2() {
        return stepBuilderFactory.get("conditionalJobStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return stepBuilderFactory.get("conditionalJobStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
