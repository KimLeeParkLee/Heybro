package com.heybro.heybro.common.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        // S3에 저장될 파일명 (중복 방지를 위해 UUID 추가)
        String savedFileName = createS3FileName(originalFilename);

        // S3에 파일을 업로드하기 위한 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(savedFileName) // S3 버킷에 저장될 파일의 전체 경로
                .contentType(multipartFile.getContentType())
                .build();

        // 파일 업로드
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

        // 업로드된 파일의 S3 URL 반환
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(savedFileName)).toString();
    }

    // 업로드 후 DB 저장이 실패했을 때 고아 파일이 남지 않도록 삭제 (보상 처리). 실패해도 예외를 밖으로 던지지 않는다.
    public void deleteFiles(List<String> fileUrls) {
        for (String url : fileUrls) {
            try {
                String key = url.substring(url.indexOf("images/"));
                s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
            } catch (Exception e) {
                log.warn("S3 보상 삭제 실패: {} ({})", url, e.getMessage());
            }
        }
    }

    // S3에 저장될 파일명을 생성 (UUID를 사용하여 유일성 보장)
    private String createS3FileName(String originalFileName) {
        return "images/" + UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    // 파일의 확장자 추출
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("잘못된 형식의 파일 (%s) 입니다.", fileName));
        }
    }
}