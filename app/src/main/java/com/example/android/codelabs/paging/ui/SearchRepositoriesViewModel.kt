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
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult

/**
 * ViewModel for the [SearchRepositoriesActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

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
    val repos: LiveData<List<Repo>> = Transformations.switchMap(repoResult) { it.data } // returns the list of repos live data from RepoSearchResult
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
     * when scrolling the rv if the visible items count + last vis. item pos + the threshold is
     * larger than the total items per page count, get the last query string, if it's not null
     * request more items from repository manager using the query string
     * @param visibleItemCount : current visible items in rv
     * @param lastVisibleItemPosition : position of last item in the retrieved list
     * @param totalItemCount :  total count of items retrieved per page
     */
    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = lastQueryValue() // last stored value of query string
            if (immutableQuery != null) {
                repository.requestMore(immutableQuery)// retrieve more results from network and store in db on success
            }
        }
    }

    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value
}
