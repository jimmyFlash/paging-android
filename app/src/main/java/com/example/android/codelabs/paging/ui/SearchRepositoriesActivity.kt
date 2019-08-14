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

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.codelabs.paging.Injection
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.model.Repo
import kotlinx.android.synthetic.main.activity_search_repositories.*

class SearchRepositoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchRepositoriesViewModel
    private val adapter = ReposAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_repositories)

        // get the view model
        viewModel = ViewModelProviders.of(this,
                Injection.provideViewModelFactory(this)) // use dependency injection to provide viewmwodel factory instance with context
                .get(SearchRepositoriesViewModel::class.java)// create instance of the SearchRepositoriesViewModel from factory, with instance GithubRepository injected through constructor

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)


        initAdapter()// initiate adapter and load default repo list from network
        // check if the search query string
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        // initiate / update search query string livedata in vm
        viewModel.searchRepo(query)
        initSearch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // keep track of last search query string
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    /**
     * initiate and set the rv adapter
     * observe the vm repos live data for returned list of repo from network call
     *
     * observes network error messages
     */
    private fun initAdapter() {
        list.adapter = adapter
        // observer list of repo live data returned form network call
        viewModel.repos.observe(this, Observer<PagedList<Repo>> {
            Log.d("Activity", "list: ${it?.size}")
            showEmptyList(it?.size == 0) // hide list if size is 0, show tv with msg
            adapter.submitList(it) // Submits a new list to be diffed, and displayed.
        })
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })
    }

    /**
     * display query sting
     * initiate search
     */
    private fun initSearch(query: String) {
        search_repo.setText(query)

        search_repo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        search_repo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateRepoListFromInput() {
        search_repo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)// reset rv scroll
                viewModel.searchRepo(it.toString()) // initiate a search for query
                adapter.submitList(null) // empty/reset the adapter
            }
        }
    }

    /**
     * helper method to show or hide a textview relaying no data message
     * or the list of repos
     * @param show flag indicating ui state
     */
    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }


    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query" // last saved search string
        private const val DEFAULT_QUERY = "Android" // default git repo search query string
    }
}
