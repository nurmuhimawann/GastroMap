package com.nurmuhimawann.gastromap.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteRestaurantDao {
    @Query("SELECT * FROM favorite_restaurant")
    fun getFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteRestaurant(favoriteRestaurant: FavoriteRestaurantEntity)

    @Query("SELECT * FROM favorite_restaurant WHERE name LIKE '%' || :query || '%'")
    fun searchFavoriteRestaurant(query: String): Flow<List<FavoriteRestaurantEntity>>

    @Query("DELETE FROM favorite_restaurant WHERE id = :id")
    suspend fun deleteFavoriteRestaurant(id: String)

    @Query("SELECT EXISTS(SELECT * FROM favorite_restaurant WHERE id = :id)")
    fun isFavoriteRestaurant(id: String): Flow<Boolean>
}
