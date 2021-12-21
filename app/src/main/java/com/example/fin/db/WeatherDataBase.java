package com.example.fin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.fin.dao.WeatherDataDao;


@Database(entities = {WeatherData.class}, version = 1)
public abstract class WeatherDataBase extends RoomDatabase {
    public abstract WeatherDataDao weatherDataDao();

}
