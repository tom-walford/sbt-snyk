# Snyk Plugin

This plugin is a thin wrapper around the Snyk CLI to better support Snyk when running with multi-project sbt builds. It provides three tasks from the SLI:
* snykAuth (auth)
* snykTest (test)
* snykMonitor (monitor)

## Usage

To set the plugin up in your project, simply add the following dependency to your `plugins.sbt` file.
`addSbtPlugin("org.tom.walford" % "sbt-snyk" % "0.1")`

You will need to then enable the plugin on any project you want to test, with
`enablePlugins(SnykPlugin)`
A project that has the plugin enabled will need to set the organization setting:
`snykOrganization := "my-org"`

This corresponds to the organization you'll be monitoring against inside Snyk.

## Tasks

Below is a short description of the tasks and what they perform

### snykAuth

This simply runs the `snyk auth` command via the CLI. If you have the `SNYK_TOKEN` environment variable set, it will use this to authenticate against the snyk backend.
Otherwise it will try and authenticate you using implicit grant.

### snykTest

This tests the project using `snyk test`. The task is run against the active project.
Using `aggregate` with child projects will run the test on all child projects that have the plugin enabled.

### snykMonitor

This tests the project using `snyk monitor`. It passes the organization set with `snykOrganization`, and uses the name of the project as the `project-name` field in snyk.
The task against is run against the active project. Using `aggregate` with child projects will run the test on all child projects that have the plugin enabled.