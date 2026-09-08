package com.example.photoapp.users.data;

import com.example.photoapp.users.ui.model.AlbumResponseModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient("albums-ws")
public interface AlbumServiceClient {

    Logger log = LoggerFactory.getLogger(AlbumServiceClient.class);

    @GetMapping("/users/{id}/albums")
    @Retry(name = "albums-ws")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
    List<AlbumResponseModel> getAlbums(@PathVariable String id);

    default List<AlbumResponseModel> getAlbumsFallback(@PathVariable String id, Throwable throwable) {
        log.error(
                "Album service call failed. id={}, exceptionType={}, message={}",
                id, throwable.getClass().getSimpleName(),
                throwable.getMessage(),
                throwable
        );
        return new ArrayList<>();
    }
}
