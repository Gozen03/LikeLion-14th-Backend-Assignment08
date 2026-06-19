package com.likelion.likelionS3.common.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration          // "나는 설정 클래스야" → Spring이 앱 시작 시 가장 먼저 읽음
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;   // application.yml에서 값 꺼내서 여기에 넣어줌

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;      // 실제 값: ap-northeast-2

    @Bean                       // "이 메서드가 반환하는 객체를 Spring이 관리해줘"
    public AmazonS3 amazonS3() {

        // 1단계: 아이디/비밀번호 묶기
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        // 2단계: 인증 정보 + 리전으로 S3 클라이언트 생성
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();       // 완성된 S3 클라이언트 반환
    }
}
