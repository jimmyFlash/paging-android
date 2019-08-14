package com.example.android.codelabs.paging.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.searchRepos
import com.example.android.codelabs.paging.db.GithubLocalCache
import com.example.android.codelabs.paging.model.Repo

/**
 *
 * A problem appears when the data source doesn't have any more data to give us,
 * either because zero items were returned from the initial data request or because
 * we've reached the end of the data from the DataSource.
 * To resolve this issue, implement a RepoBoundaryCallback.
 * This class notifies us when either situation occurs, so we know when to request more data.
 * Because our DataSource is a Room database, backed by network data, the callbacks
 * let us know that we should request more data from the API.
 *
 */
class RepoBoundaryCallback (private val query: String,
                            private val service: GithubService,
                            private val cache: GithubLocalCache) :  PagedList.BoundaryCallback<Repo>(){

    companion object {
        private const val NETWORK_PAGE_SIZE = 50 // number of items per page to  load form network
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()

    // LiveData of network errors.
    /*
        internally, in the RepoBoundaryCallback class, we can work with a MutableLiveData,
        but outside the class, we only expose a LiveData object, whose values can't be modified.
     */
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    override fun onZeroItemsLoaded() {
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        requestAndSaveData(query)
    }

    /**
     * request more items from network with query string
     * cache results in db on success
     * or post error message on fail
     */
    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        /*
        make a service call to github with specified query and increment request page counter
        on success insert the list of repo objects into db table and increment last requested page counter
        on fail/error post the error string to livedata
         */
        searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos
            ) { // lambda that executes when insertion is done, moved outside parenthesis
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }



}