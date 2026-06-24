package com.localizzatore.prodotti.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Prodotto::class], version = 1, exportSchema = false)
abstract class ProdottoDatabase : RoomDatabase() {

    abstract fun prodottoDao(): ProdottoDao

    companion object {
        @Volatile
        private var INSTANCE: ProdottoDatabase? = null

        fun getInstance(context: Context): ProdottoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProdottoDatabase::class.java,
                    "prodotti_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
