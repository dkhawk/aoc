# advent-of-code[🐔](spherical_chickens.md)

This project uses [Gradle](https://gradle.org/)
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew run` to build and run the application.
* Run `./gradlew build` to only build the application.
* Run `./gradlew check` to run all checks, including tests.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup and consists of the `app` and `utils` subprojects.
The shared build logic was extracted to a convention plugin located in `buildSrc`.

This project uses a version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies
and both a build cache and a configuration cache (see `gradle.properties`).

## Runtimes

### [2025](app/src/main/kotlin/com/sphericalchickens/aoc2025/README.md)

| Day | Part 1 | Part 2 | Total |
|:---:|:---:|:---:|:--:|
| [Day 1](app/src/main/kotlin/com/sphericalchickens/aoc2025/day01/README.md) <br> ([Solution](app/src/main/kotlin/com/sphericalchickens/aoc2025/day01/Day01.kt)) | 1ms (1816µs) | 2ms (2045µs) | 23ms |
| [Day 2](app/src/main/kotlin/com/sphericalchickens/aoc2025/day02/README.md) <br> ([Solution](app/src/main/kotlin/com/sphericalchickens/aoc2025/day02/Day02.kt)) | 88ms | 322ms | 414ms |
| [Day 3](app/src/main/kotlin/com/sphericalchickens/aoc2025/day03/README.md) <br> ([Solution](app/src/main/kotlin/com/sphericalchickens/aoc2025/day03/Day03.kt)) | 0ms (739µs) | 1ms (1338µs) | 4ms (4157µs) |
| [Day 4](app/src/main/kotlin/com/sphericalchickens/aoc2025/day04/README.md) <br> ([Solution](app/src/main/kotlin/com/sphericalchickens/aoc2025/day04/Day04.kt)) | 7ms | 12ms | 24ms |
| [Day 5](app/src/main/kotlin/com/sphericalchickens/aoc2025/day05/README.md) <br> ([Solution](app/src/main/kotlin/com/sphericalchickens/aoc2025/day05/Day05.kt)) | 5ms | 0ms (555µs) | 9ms |
| [Day 6](app/src/main/kotlin/com/sphericalchickens/aoc2025/day06/Day06.kt) |  |  | |
| [Day 7](app/src/main/kotlin/com/sphericalchickens/aoc2025/day07/Day07.kt) |  |  | |
| [Day 8](app/src/main/kotlin/com/sphericalchickens/aoc2025/day08/Day08.kt) |  |  | |
| [Day 9](app/src/main/kotlin/com/sphericalchickens/aoc2025/day09/Day09.kt) |  |  | |
| [Day 10](app/src/main/kotlin/com/sphericalchickens/aoc2025/day10/Day10.kt) |  |  | |
| [Day 11](app/src/main/kotlin/com/sphericalchickens/aoc2025/day11/Day11.kt) |  |  | |
| [Day 12](app/src/main/kotlin/com/sphericalchickens/aoc2025/day12/Day12.kt) |  |  | |

