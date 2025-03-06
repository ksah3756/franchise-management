package com.goorm.friendchise.global.redis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.friendchise.domain.store.dto.StoreRedisDto;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.exception.NoAuthenticationException;
import com.goorm.friendchise.domain.store.exception.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper= new ObjectMapper();

    public void saveData(String key, String value) {
        redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public List<StoreRedisDto> getAllStoresFromRedis() {
        List<StoreRedisDto> stores = new ArrayList<>();

        // store:로 시작하는 모든 키를 찾기 위한 패턴
        String pattern = "store:*";

        // Redis에서 모든 key를 찾기 위한 scan
        // TODO:
        Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                connection -> connection.scan(ScanOptions.scanOptions().match(pattern).build())
        );


        // 모든 키에 대해 값을 가져와서 Store 객체로 변환
        while (cursor.hasNext()) {
            byte[] key = cursor.next();
            String storeJson = new String(key);  // Key가 byte[]로 반환되므로, 문자열로 변환

            // Redis에서 해당 키에 해당하는 값을 가져옴
            String storeKey = new String(key);
            String storeJsonValue = (String) redisTemplate.opsForValue().get(storeKey);

            try {
                // JSON 값을 Store 객체로 변환
                StoreRedisDto store = objectMapper.readValue(storeJsonValue, StoreRedisDto.class);
                stores.add(store);  // 변환된 Store 객체를 리스트에 추가
            } catch (Exception e) {
                e.printStackTrace();  // 예외 처리 (예: JSON 파싱 오류)
            }
        }

        return stores;
    }

    public StoreRedisDto getStoreFromRedisById(Long id) throws JsonProcessingException {
        String storeKey = "store:"+id;
        Object value = redisTemplate.opsForValue().get(storeKey);
        if(value==null)
            throw new StoreNotFoundException();
        String jsonValue = value.toString();
        return objectMapper.readValue(jsonValue, StoreRedisDto.class);
    }
}