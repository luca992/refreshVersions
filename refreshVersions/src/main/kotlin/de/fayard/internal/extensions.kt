package de.fayard.internal

import de.fayard.OrderBy

fun Dependency.gradleNotation() = "$group:$name:$version"

fun List<Dependency>.orderDependencies(): List<Dependency> {
    return this.sortedBy { it.gradleNotation() }
}

fun List<Dependency>.findCommonVersions(): List<Dependency> {
    val map = groupBy { d: Dependency -> d.groupOrVirtualGroup() }
    for (deps in map.values) {
        val sameVersions = deps.map { it.version }.distinct().size == 1
        val hasVirtualGroup = deps.any { it.groupOrVirtualGroup() != it.group }
        if (sameVersions && (hasVirtualGroup || deps.size > 1)) {
            deps.forEach { d -> d.mode = VersionMode.GROUP }
        }
    }
    return this
}

// TODO: remove
fun List<Dependency>.sortedBeautifullyBy(orderBy: OrderBy, selection: (Dependency) -> String?) : List<Dependency> {
    val unsorted = this.filterNot { selection(it) == null }
        .sortedBy { selection(it)!! }
    return when(orderBy) {
        OrderBy.GROUP_AND_LENGTH -> unsorted.sortedByDescending { selection(it)!!.length }.sortedBy { it.mode }
        OrderBy.GROUP_AND_ALPHABETICAL -> unsorted.sortedBy { it.mode }
    }
}

fun logFileWasModified(path: String, existed: Boolean) {
    val ANSI_RESET = "\u001B[0m"
    val ANSI_GREEN = "\u001B[32m"

    val status = if (existed) {
        "        modified:   "
    } else {
        "        new file:   "
    }
    println("$ANSI_GREEN$status$path$ANSI_RESET")
}
