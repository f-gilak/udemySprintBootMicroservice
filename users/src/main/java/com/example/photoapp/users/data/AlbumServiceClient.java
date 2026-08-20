package com.example.photoapp.users.data;

import com.example.photoapp.users.ui.model.AlbumResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("albums-ws")
public interface AlbumServiceClient {

    @GetMapping("/users/{id}/albumss")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);
}
