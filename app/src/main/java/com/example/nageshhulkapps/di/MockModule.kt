package com.example.nageshhulkapps.di

import android.content.Context
import com.example.nageshhulkapps.data.models.Mock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object MockModule {
    @Provides
    @Singleton
    fun providesMockDependency(@ApplicationContext context: Context): Mock {
        return Mock(context)
    }
}