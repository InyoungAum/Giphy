package com.inyoung.giphy.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LikeImage(
    @PrimaryKey var id: String = "",
    var like: Boolean = true
) : RealmObject()