package com.inyoung.giphy.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.inyoung.giphy.asLiveData
import io.realm.Realm
import io.realm.kotlin.delete

class LikeImageDao(val realm: Realm) {
    fun addNewLikeImage(id: String) {
        realm.executeTransaction {
            it.insert(LikeImage(id))
        }
    }

    fun deleteLikeImage(id: String) {
        realm.executeTransaction {
            val result = it.where(LikeImage::class.java)
                .equalTo("id", id)
                .findFirst()
            result?.deleteFromRealm()
        }
    }

    fun getLikeImages(): LiveData<List<LikeImage>> {
        realm.refresh()
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

    fun changeLikeState(image: LikeImage) {
        realm.executeTransaction {
            image.like = !image.like
        }
    }
}
