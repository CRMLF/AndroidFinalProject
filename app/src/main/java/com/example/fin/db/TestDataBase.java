package com.example.fin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.fin.dao.TestDao;


@Database(entities = {Test.class}, version = 1)
public abstract class TestDataBase extends RoomDatabase {

    public abstract TestDao userDao();

}
