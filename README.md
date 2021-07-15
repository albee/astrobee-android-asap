# Astrobee Robot Software - Android Submodule (ASAP Interface)

This is the Astrobee Science Application Package (ASAP) interface for Astrobee Android, allowing GDS commanding of science payloads.

## Usage

`guest_science_projects` contains a template for our APK, `roamcommandasap`, and a helpful example, `geckoperchinggripper`.

ASAP APK: `guest_science_projects/roamcommandasap/app/src/main/java/edu/mit/ssl/roamcommandasap`

Helpful example: `guest_science_projects/geckoperchinggripper/app/src/main/java/edu/stanford/asl/geckoperchinggripper`

* main/res/xml/commands.xml: defines commands you want to handle from apk
* functions in our class:
  onGuestScienceCommand
  onGuestScienceStop
  onGuestScienceStart
  onGuestScienceCustomCmd
  most important, uses command input from GDS
