package com.inyoung.giphy.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.inyoung.giphy.asLiveData
import io.realm.Realm

class LikeImageDao(val realm: Realm) {
    fun addNewLikeImage(id: String) {
        realm.beginTransaction()
        realm.insert(LikeImage(id))
        realm.commitTransaction()
    }

    fun getLikeImages(): LiveData<List<LikeImage>> {
        val result = realm.where(LikeImage::class.java)
            .equalTo("like", true)
            .findAllAsync()
            .asLiveData()

        return Transformations.map(result) {
            mutableListOf<LikeImage>().apply { addAll(it) }
        }
    }

    fun findLikeImage(id: String) =
        realm.where(LikeImage::class.java)
            .equalTo("id", id)
            .findFirst()

    fun changeLikeState(id: String) {
        realm.beginTransaction()
        findLikeImage(id)?.let {
            it.like = !it.like
        }
        realm.commitTransaction()
    }
}
