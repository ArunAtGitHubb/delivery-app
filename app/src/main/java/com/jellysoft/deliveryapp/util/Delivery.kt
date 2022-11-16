package com.jellysoft.deliveryapp.util

import android.provider.BaseColumns
import com.jellysoft.deliveryapp.util.DeliveryReaderContract.DeliveryEntry.COLUMN_NAME_USER_NAME

object DeliveryReaderContract {
    object DeliveryEntry : BaseColumns {
        const val TABLE_DELIVERY = "delivery"
        const val COLUMN_NAME_USER_NAME = "user_name"
    }

    const val SQL_CREATE_TABLE_DELIVERY =
        "CREATE TABLE IF NOT EXISTS ${DeliveryEntry.TABLE_DELIVERY} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME_USER_NAME TEXT) "

    const val SQL_DELETE_TABLE_DELIVERY = "DROP TABLE IF EXISTS ${DeliveryEntry.TABLE_DELIVERY}"
}