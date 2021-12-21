package com.example.fin.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.fin.db.Test;

import java.util.List;


@Dao
public interface TestDao {

    @Query("SELECT * FROM test")
    List<Test> getAll();

    @Query("SELECT * FROM test WHERE id IN (:testIds)")
    List<Test> loadAllByIds(int[] testIds);

    @Query("SELECT * FROM test WHERE message LIKE :message")
    Test findByMessage(String message);

    @Insert
    void insertAll(Test... tests);

    @Delete
    void delete(Test test);
}
