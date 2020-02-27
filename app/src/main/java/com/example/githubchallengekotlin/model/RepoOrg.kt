package com.example.githubchallengekotlin.model

import com.squareup.moshi.Json

class RepoOrg{
    @Json(name = "full_name") var companyName : String ?= null
    @Json(name = "name") var repoName : String ?= null
    @Json(name = "html_url") var repoUrl : String ?= null
    @Json(name = "stargazers_count") var starCount : Int ?= null
    @Json(name = "public_repos") var repoCount :Int ?= null
}