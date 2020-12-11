package com.ekosoftware.financialpreview.data.local

import androidx.room.TypeConverter
import java.util.Date


object RoomConverters {

    /**
     * Parses a long to a [java.util.Date].
     *
     * @param value timestamp in [Long] format.
     */
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?) = if (value == null) null else Date(value)

    /**
     * Parses a [java.util.Date] into [Long] to make it readable for [androidx.room].
     *
     * @param date to parse.
     */
    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?) = date?.time

}