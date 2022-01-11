package com.example.serviceapplication.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.serviceapplication.data.db.OidcDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideSearchDatabase( @ApplicationContext context : Context) =
        Room.databaseBuilder( context, OidcDatabase::class.java, "OidcDatabase")
            .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2)
            .build()

    val MIGRATION_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL( "CREATE TABLE IF NOT EXISTS AuthResponseInfo (" +
                    " id INTEGER PRIMARY KEY NOT NULL, " +
                    " idToken TEXT, " +
                    " accessToken TEXT, " +
                    " desc TEXT " +
                    ")")

            database.execSQL( "INSERT INTO AuthResponseInfo (id, idToken, accessToken) " +
                    " SELECT id, idToken, accessToken" +
                    " FROM TokenInfo")

            database.execSQL("DROP TABLE TokenInfo")
        }
    }

}