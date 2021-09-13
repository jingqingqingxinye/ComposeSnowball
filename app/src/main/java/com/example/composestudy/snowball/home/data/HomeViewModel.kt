package com.example.composestudy.snowball.home.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    var recItems = mutableStateListOf<PostData>()
//        private set

    var followItems = mutableStateListOf<PostData>()
//        private set

    var hotItems = mutableStateListOf<PostData>()
//        private set

    var liveData = MutableLiveData<String>()


    fun addRecItem(item: PostData) {
        recItems.add(item)
    }

    fun removeRecItem(item: PostData) {
        recItems.remove(item)
    }

    fun addFollowItem(item: PostData) {
        followItems.add(item)
    }
    fun addHotItem(item: PostData) {
        hotItems.add(item)
    }

}