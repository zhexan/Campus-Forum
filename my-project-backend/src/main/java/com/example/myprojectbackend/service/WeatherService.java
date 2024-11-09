package com.example.myprojectbackend.service;

import com.example.myprojectbackend.entity.vo.response.WeatherVO;

public interface WeatherService {
    WeatherVO fetchWeather(double longitude, double latitude);


}

