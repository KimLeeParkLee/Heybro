package com.heybro.heybro.support;

import com.google.firebase.FirebaseApp;
import com.heybro.heybro.common.s3.service.S3UploadService;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 통합 테스트 공통 설정. 외부 연동(Firebase, S3)은 목으로 대체하고 실제 MySQL(heybro_test)·Redis 를 사용한다.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected FirebaseApp firebaseApp;

    @MockitoBean
    protected S3UploadService s3UploadService;

    @Autowired
    protected UserRepository userRepository;

    protected User saveUser(String email, int point) {
        return userRepository.save(User.builder()
                .name("테스트")
                .nickname("nick_" + email.replace("@", "_").replace(".", "_"))
                .email(email)
                .gender("M")
                .broPoint(point)
                .totalBroPoint(point)
                .privacyConsent(true)
                .provider("local")
                .build());
    }

    /** 동시 실행 결과: 성공·실패 건수 */
    public record ConcurrentResult(int success, int failure) {}

    /** threads 개의 작업을 동시에 시작해 모두 끝날 때까지 기다린다. */
    protected ConcurrentResult runConcurrently(int threads, Runnable task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    task.run();
                    success.incrementAndGet();
                } catch (Exception e) {
                    failure.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await(10, TimeUnit.SECONDS);
        start.countDown();
        done.await(60, TimeUnit.SECONDS);
        pool.shutdownNow();
        return new ConcurrentResult(success.get(), failure.get());
    }
}
