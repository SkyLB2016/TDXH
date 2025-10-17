package com.sky.oa.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by SKY on 16/5/10 下午3:50.
 * 列表类
 */
data class CourseEntity(
    var id: Int = 0,
    var name: String? = null,
    var picSmall: String? = null,
    var picBig: String? = null,
    var description: String? = null,
    var learner: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    //    constructor(parcel: Parcel) : this() {
//        id = parcel.readInt()
//        name = parcel.readString()
//        picSmall = parcel.readString()
//        picBig = parcel.readString()
//        description = parcel.readString()
//        learner = parcel.readInt()
//    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(picSmall)
        parcel.writeString(picBig)
        parcel.writeString(description)
        parcel.writeInt(learner)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CourseEntity> {
        override fun createFromParcel(parcel: Parcel) = CourseEntity(parcel)

        override fun newArray(size: Int): Array<CourseEntity?> = arrayOfNulls(size)
    }
}
