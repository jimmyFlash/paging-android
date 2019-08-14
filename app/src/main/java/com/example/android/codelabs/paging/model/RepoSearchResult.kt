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

package com.example.android.codelabs.paging.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * RepoSearchResult from a search, which contains LiveData<List<Repo>> holding query data,
 * and a LiveData<String> of network error state.
 */
data class RepoSearchResult(

        /**
         * The alternative to List<Repo> is a PagedList<Repo>. A PagedList is a version of a List
         * that loads content in chunks. Similar to the List, the PagedList holds a snapshot of content,
         * so updates occur when new instances of PagedList are delivered via LiveData.
         * When a PagedList is created, it immediately loads the first chunk of data and expands
         * over time as content is loaded in future passes. The size of PagedList is the number of
         * items loaded during each pass. The class supports both infinite lists and very large
         * lists with a fixed number of elements.
         *
         * The PagedList loads content dynamically from a source. In our case, because the database
         * is the main source of truth for the UI, it also represents the source for the PagedList.
         * If your app gets data directly from the network and displays it without caching,
         * then the class that makes network requests would be your data source.
         */
        val data: LiveData<PagedList<Repo>>,
        val networkErrors: LiveData<String>
)
