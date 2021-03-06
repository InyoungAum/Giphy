package com.inyoung.giphy.model

import io.realm.RealmResults
import io.realm.RealmChangeListener
import androidx.lifecycle.LiveData
import io.realm.RealmModel


class LiveRealmData<T : RealmModel>(
    private val results: RealmResults<T>
) : LiveData<RealmResults<T>>() {
    init {
        value = results
    }
    private val listener =
        RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }
}
