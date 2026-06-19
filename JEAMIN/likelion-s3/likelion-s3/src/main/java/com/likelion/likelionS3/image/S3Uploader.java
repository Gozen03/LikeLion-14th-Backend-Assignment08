package com.likelion.likelionS3.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor    // final 필드 생성자 자동 생성 (Lombok)
public class S3Uploader {

    private final AmazonS3 amazonS3;  // S3Config에서 만든 빈 자동 주입

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;            // application.yml에서 버킷명 주입

    public String upload(MultipartFile file) throws IOException {

        // ① 중복 방지 파일명 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // ② S3에 전달할 파일 정보 세팅
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // ③ 실제 업로드
        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);

        // ④ 업로드된 파일 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        try {
            // ① S3 URL에서 객체 키(Object Key) 추출 (마지막 '/' 이후의 문자열)
            String objectKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // ② 한글/특수문자 파일명이 URL 인코딩되어 저장되었을 수 있으므로 디코딩
            String decodedKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8.name());

            // ③ S3 버킷에서 파일 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, decodedKey));
        } catch (Exception e) {
            throw new RuntimeException("S3 버킷에서 이전 이미지를 삭제하는 중 오류가 발생했습니다.", e);
        }
    }
}