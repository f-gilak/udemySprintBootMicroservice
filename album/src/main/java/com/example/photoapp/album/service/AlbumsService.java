/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.photoapp.album.service;

import com.example.photoapp.album.data.AlbumEntity;

import java.util.List;

public interface AlbumsService {
    List<AlbumEntity> getAlbums(String userId);
}
