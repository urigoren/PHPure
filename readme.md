PHPure
===

PHPure is a command-line tool to generate php unit tests/

How it works ?
---
The process is simple, given an **existing** php website,
1. Run the patching, by launching `java phpure <base dir>`
2. Let the website run for a while
3. Unpatch by renaming all `*.*.uri` to `*.*`
4. Unit tests are saved to `/logger.txt`

If you want to override the test location or storage method, write your own `phpure_save` method.