package com.nurmuhimawann.gastromap.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity

@Database(entities = [FavoriteRestaurantEntity::class], version = 1, exportSchema = false)
abstract class FavoriteRestaurantDatabase: RoomDatabase() {
    abstract fun favoriteRestaurantDao(): FavoriteRestaurantDao

    companion object {
        @Volatile
        private var instance: FavoriteRestaurantDatabase? = null

        fun getInstance(context: Context): FavoriteRestaurantDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteRestaurantDatabase::class.java, "FavoriteRestaurant.db"
                ).build()
            }
    }
}
