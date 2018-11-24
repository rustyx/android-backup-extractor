Android backup extractor
========================

Utility to extract and repack Android backups created with ```adb backup``` (ICS+). 
Largely based on ```BackupManagerService.java``` from AOSP. 

# Building

Requires [Java 7](https://jdk.java.net/) or later (Oracle or OpenJDK).

Use the steps mentioned below to build or see [Releases](#releases) for pre-built binaries (runnable jar files).

Use [Apache Maven](https://maven.apache.org/download.html) to create an all-in-one jar:
```mvn package -Prelease```. The resulting binary will be at `target/abe.jar`.

# Usage

```java -jar target/abe.jar pack|unpack|pack-kk|x|c ...```

## Syntax: 
* unpack:       ```abe unpack  <backup.ab> <backup.tar> [password]```
* pack:         ```abe pack    <backup.tar> <backup.ab> [password]```
* pack for 4.4+: ```abe pack-kk <backup.tar> <backup.ab> [password]```
  (creates version 2 backups, to be used starting from Android 4.4.3)
* extract tar:  ```abe x <backup.tar> <folder>```
* create tar:   ```abe c <backup.tar> <folder>```

If the filename is `-`, then data is read from standard input or written to
standard output.

If the password is not given on the command line, then the environment variable
`ABE_PASSWD` is tried. If you don't specify a password the backup archive won't
be encrypted but only compressed. 

## Packing tar archives

- Android is **very** particular about the order of files in the tar archive. The format is [described here](https://android.googlesource.com/platform/frameworks/base/+/4a627c71ff53a4fca1f961f4b1dcc0461df18a06).
- Incompatible tar archives lead to errors or even system crashes.
- Apps with the `allowBackup` flag set to `false` are [not backed up nor restored](https://android.googlesource.com/platform/frameworks/base/+/a858cb075d0c87e2965d401656ff2d5bc16406da).
  - *(you can try restoring manually via `adb push` and `adb shell`)*
- Errors are only printed to logcat, look out for `BackupManagerService`.

The safest way to pack a tar archive is to get the list of files from the original backup.tar file.
The `x` and `c` commands write and read a file list in `.filelist`.

```shell
java -jar abe.jar x backup.tar .
```

And build the new tar file:
```shell
java -jar abe.jar c restore.tar .
```

You can now pack `restore.tar` and try `adb restore restore.ab`

# Releases

[Releases](https://github.com/nelenkov/android-backup-extractor/releases/latest) are built with Travis CI from the master branch and include a runnable fat jar.

Use the binaries at your own risk. No warranty or support provided.

Please report only bugs in backup extractor itself, I can't answer questions regrading unpacking/repacking backups or tar files.
(See [Usage](#usage) for a mini usage guide.)

[![Build Status](https://travis-ci.org/nelenkov/android-backup-extractor.svg?branch=master)](https://travis-ci.org/nelenkov/android-backup-extractor)

# Notes

More details about the backup format and the tool implementation in the [associated blog post](https://nelenkov.blogspot.de/2012/06/unpacking-android-backups.html).

