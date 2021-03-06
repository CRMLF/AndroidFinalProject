package com.example.fin.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.fin.db.WeatherData;

import java.util.List;

@Dao
public interface WeatherDataDao {

    @Query("SELECT * FROM weatherData")
    List<WeatherData> getAll();

    @Query("SELECT * FROM weatherData Order by id desc limit 8")
    List<WeatherData> getRecently();

    @Query("SELECT * FROM weatherData WHERE date = :date")
    WeatherData findByDate(String date);

    @Insert
    void insertAll(WeatherData... weatherDatas);

    @Delete
    void delete(WeatherData weatherData);
}
