# Astrobee Android, ASAP Interface

This is the Astrobee Science Application Package (ASAP) interface for Astrobee Android, allowing GDS commanding of science payloads.

Keenan Albee, Charles Oestreich, Phillip Johnson, Abhi Cauligi. Based on a template by Ruben Garcia-Ruiz.

## Usage

`guest_science_projects` contains a template APK, `commandasap`, and another helpful example by Stanford ASL intended for direct commanding from the APK, `geckoperchinggripper`.

- ASAP APK: `guest_science_projects/roamcommandasap/app/src/main/java/edu/mit/ssl/commandasap`

- Helpful example: `guest_science_projects/geckoperchinggripper/app/src/main/java/edu/stanford/asl/geckoperchinggripper`

- `main/res/xml/commands.xml`: defines commands you want to handle from APK

- *The 4 Guest Science functions (NASA API)*:
  - onGuestScienceCommand
  - onGuestScienceStop
  - onGuestScienceStart
  - onGuestScienceCustomCmd (most important, uses command input from GDS)
