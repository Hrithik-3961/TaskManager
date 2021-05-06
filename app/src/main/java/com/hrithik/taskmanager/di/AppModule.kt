package com.hrithik.taskmanager.di

import android.app.Application
import androidx.room.Room
import com.hrithik.taskmanager.data.TasksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, TasksDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideTaskDao(db: TasksDatabase) = db.taskDao()

}