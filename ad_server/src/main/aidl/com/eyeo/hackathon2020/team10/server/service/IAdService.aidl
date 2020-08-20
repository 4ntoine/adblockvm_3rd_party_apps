package com.eyeo.hackathon2020.team10.server.service;

interface IAdService {
    // return Ad banner URL
    String getAdUrl();

    // return Ad banner Bitmap
    byte[] getAdBitmap();
}