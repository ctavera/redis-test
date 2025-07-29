package org.revenatium.redistest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.revenatium.redistest.domain.PaymentGateway;
import org.revenatium.redistest.domain.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BaseTests {

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected static final String baseUrl = "/api/v1";
    protected ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected PrintStream originalOut = System.out;
    protected Boolean isRedisAvailable = true;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        objectMapper.registerModule(new JavaTimeModule());

        // Console output catch
        System.setOut(new PrintStream(outContent));

        try {
            String response = redisTemplate.getConnectionFactory().getConnection().ping();
            assertEquals("PONG", response, "Redis no responde correctamente");
        } catch (Exception e) {
            isRedisAvailable = false;
        }
    }

    @AfterEach
    void restoreStreams() {
        System.out.flush();
        System.setOut(originalOut);
        System.out.println(outContent.toString());
    }

    protected PaymentRequest getValidPaymentRequest() {
        return PaymentRequest.builder()
                .amount(1485.50)
                .paymentGateway(PaymentGateway.BANORTE)
                .build();
    }
}

