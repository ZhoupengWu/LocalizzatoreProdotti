package com.localizzatore.prodotti.di

import android.content.Context
import com.localizzatore.prodotti.data.local.ProdottoDao
import com.localizzatore.prodotti.data.local.ProdottoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProdottoDatabase =
        ProdottoDatabase.getInstance(context)

    @Provides
    fun provideProdottoDao(database: ProdottoDatabase): ProdottoDao = database.prodottoDao()
}
