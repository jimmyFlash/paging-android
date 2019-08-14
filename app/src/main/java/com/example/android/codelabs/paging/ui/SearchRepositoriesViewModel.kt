/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult

/**
 * ViewModel for the [SearchRepositoriesActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {


    private val queryLiveData = MutableLiveData<String>()

    /*
      Transformation.map : Returns a LiveData mapped from the input source LiveData by
        applying mapFunction to each value set on source.
        This method is analogous to io.reactivex.Observable.map.
        transform will be executed on the main thread.
   */
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it) // returns RepoSearchResult live data by executing search method from GithubRepository with every update to the queryLiveData
    }

    /*
    Transformation methods for LiveData.
    These methods permit functional composition and delegation of LiveData instances.
    The transformations are calculated lazily, and will run only when the returned LiveData
    is observed. Lifecycle behavior is propagated from the input source LiveData to the returned one.

    Transformation.switchMap : Returns a LiveData mapped from the input source LiveData by applying
        switchMapFunction to each value set on source.
     */
    val repos: LiveData<PagedList<Repo>> = Transformations.switchMap(repoResult) { it.data } // returns the list of repos live data from RepoSearchResult
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) {
        it.networkErrors // // returns error string live data from RepoSearchResult
    }

    /**
     * Search a repository based on a query string.
     * post a new value from queryLiveData
     * resulting in initiating a search request from repository manager through repoResult
     */
    fun searchRepo(queryString: String) {
        queryLiveData.postValue(queryString)
    }



    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value
}
