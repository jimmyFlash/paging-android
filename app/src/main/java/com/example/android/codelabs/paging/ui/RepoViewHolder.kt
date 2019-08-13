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

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.databinding.RepoViewItemBinding
import com.example.android.codelabs.paging.model.Repo

/**
 * View Holder for a [Repo] RecyclerView list item.
 */
class RepoViewHolder(view: RepoViewItemBinding) : RecyclerView.ViewHolder(view.root) {

    private val name: TextView = view.repoName
    private val description: TextView = view.repoDescription
    private val stars: TextView = view.repoStars
    private val language: TextView = view.repoLanguage
    private val forks: TextView = view.repoForks

    private var repo: Repo? = null


    init {
        // register click listener on the view and open the url link to view repo in browser
        view.root.setOnClickListener {
            repo?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.root.context.startActivity(intent)
            }
        }
    }

    /**
     * handles displaying the repo data per item, or dummy text until it's loaded
     */
    fun bind(repo: Repo?) {
        // when repo isn't loaded yet
        if (repo == null) {
            val resources = itemView.resources // Returns the resources associated with this view.

            name.text = resources.getString(R.string.loading)
            description.visibility = View.GONE
            language.visibility = View.GONE
            stars.text = resources.getString(R.string.unknown)
            forks.text = resources.getString(R.string.unknown)
        } else {
            showRepoData(repo)
        }
    }

    /**
     * displays the full dat of the repo when loaded
     */
    private fun showRepoData(repo: Repo) {
        this.repo = repo
        name.text = repo.fullName

        // if the description is missing, hide the TextView
        var descriptionVisibility = View.GONE
        if (repo.description != null) {
            description.text = repo.description
            descriptionVisibility = View.VISIBLE
        }
        description.visibility = descriptionVisibility

        stars.text = repo.stars.toString()
        forks.text = repo.forks.toString()

        // if the language is missing, hide the label and the value
        var languageVisibility = View.GONE
        if (!repo.language.isNullOrEmpty()) {
            val resources = this.itemView.context.resources
            language.text = resources.getString(R.string.language, repo.language)
            languageVisibility = View.VISIBLE
        }
        language.visibility = languageVisibility
    }

    companion object {
        /**
         * inflates the view through databinding and instantiates the RepoViewHolder
         */
        fun create(parent: ViewGroup): RepoViewHolder {
            val inflator = LayoutInflater.from(parent.context)

            val inflatedViewBinding = DataBindingUtil.inflate<RepoViewItemBinding>(inflator,
                    R.layout.repo_view_item,
                    parent, false)
            return RepoViewHolder(inflatedViewBinding)
        }
    }
}
