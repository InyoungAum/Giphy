package com.inyoung.giphy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.inyoung.giphy.likeImageDao
import com.inyoung.giphy.model.LikeImage
import io.realm.Realm

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val realm = Realm.getDefaultInstance()

    val likeImages: LiveData<List<LikeImage>>
        get() = realm.likeImageDao().getLikeImages()

    fun findLikeImage(id: String) =
        realm.likeImageDao().findLikeImage(id)

    fun changeLikeState(id: String) =
        realm.likeImageDao().changeLikeState(id)

    fun addImage(id: String) =
        realm.likeImageDao().addNewLikeImage(id)

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}
