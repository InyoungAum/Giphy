package com.inyoung.giphy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.inyoung.giphy.likeImageDao
import com.inyoung.giphy.model.LikeImage
import io.realm.Realm

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val realm = Realm.getDefaultInstance()
    lateinit var likeImages: LiveData<List<LikeImage>>
    init {
        loadLikeImage()
    }

    fun loadLikeImage() {
        likeImages = realm.likeImageDao().getLikeImages()
    }

    fun findLikeImage(id: String) =
        realm.likeImageDao().findLikeImage(id)

    fun changeLikeState(likeImage: LikeImage) =
        realm.likeImageDao().changeLikeState(likeImage)

    fun addImage(id: String) =
        realm.likeImageDao().addNewLikeImage(id)

    fun deleteImage(id: String) =
        realm.likeImageDao().deleteLikeImage(id)

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}
