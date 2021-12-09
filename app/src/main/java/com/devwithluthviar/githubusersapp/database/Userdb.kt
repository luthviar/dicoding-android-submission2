package com.devwithluthviar.githubusersapp.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Userdb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "username")
    var username: String? = null,
    @ColumnInfo(name = "name")
    var name: String? = null,
    @ColumnInfo(name = "avatar")
    var avatar: String? = null,
    @ColumnInfo(name = "company")
    var company: String? = null,
    @ColumnInfo(name = "location")
    var location: String? = null,
    @ColumnInfo(name = "repository")
    var repository: Int? = null,
    @ColumnInfo(name = "follower")
    var follower: Int? = null,
    @ColumnInfo(name = "following")
    var following: Int? = null

    /*
        var username: String,
        var name: String,
        var avatar: String,
        var company: String,
        var location: String,
        var repository: Int,
        var follower: Int,
        var following: Int
    */
): Parcelable