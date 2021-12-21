package com.example.fin.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mv ;
    private MutableLiveData<List<String>> mg;

    public MutableLiveData<String> gem(){
        if(mv == null ){
            mv = new MutableLiveData<String>();
        }
        return mv;
    }

    public MutableLiveData<List<String>> getmg(){
        if (mg == null ){
            mg = new MutableLiveData<List<String>>();
        }
        return mg;
    }
}