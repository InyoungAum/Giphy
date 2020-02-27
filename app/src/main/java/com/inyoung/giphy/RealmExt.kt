package com.inyoung.giphy

import com.inyoung.giphy.model.LikeImageDao
import com.inyoung.giphy.model.LiveRealmData
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults


fun Realm.likeImageDao() : LikeImageDao = LikeImageDao(this)
fun <T: RealmModel> RealmResults<T>.asLiveData() = LiveRealmData(this)
