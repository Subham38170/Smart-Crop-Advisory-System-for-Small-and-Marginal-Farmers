package com.example.krishimitra.di

import com.example.krishimitra.data.repo.RepoImpl
import com.example.krishimitra.domain.repo.Repo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    abstract fun bindRepo(
        repoImpl: RepoImpl
    ): Repo

}