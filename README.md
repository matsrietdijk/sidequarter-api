# Sidequarter API

An API backend to monitor multiple [Sidekiq](http://sidekiq.org) instances. Keeping an eye out for your Sidekiqs without having to find them individually.

# Code Status

[![Build Status](https://travis-ci.org/matsrietdijk/sidequarter-api.svg?branch=master)](https://travis-ci.org/matsrietdijk/sidequarter-api)
[![Dependency Status](https://www.versioneye.com/user/projects/564a29c8cc00b0001900004f/badge.svg?style=flat)](https://www.versioneye.com/user/projects/564a29c8cc00b0001900004f)

# Flow

Within this project the following flow is used regarding version/source control:

- All changes are made and committed on a dedicated branch (except documentation changes)
- Branches are categorized by prefixing the branch name, example: `feature/flow`
- Pull Requests (PR) are used to merge changes into the `master` branch
- Before a PR is merged:
  - [Travis-CI](https://travis-ci.org/matsrietdijk/sidequarter-api/pull_requests) should return a positive build result


The categories that are used in this project for branches are:

`feature`, `patch`, `update` and `release`
