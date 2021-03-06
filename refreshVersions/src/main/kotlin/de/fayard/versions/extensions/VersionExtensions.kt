package de.fayard.versions.extensions

import de.fayard.versions.StabilityLevel
import de.fayard.versions.internal.Version

/**
 * Check order is important. From least stable to most stable, then unknown
 */
internal fun Version.stabilityLevel(): StabilityLevel = when {
    "SNAPSHOT" in value -> StabilityLevel.Snapshot
    "preview" in value -> StabilityLevel.Preview
    "dev" in value -> StabilityLevel.Development
    "alpha" in value -> StabilityLevel.Alpha
    "beta" in value -> StabilityLevel.Beta
    "eap" in value -> StabilityLevel.EarlyAccessProgram
    isMilestone() -> StabilityLevel.Milestone
    "rc" in value -> StabilityLevel.ReleaseCandidate
    isStable() -> StabilityLevel.Stable
    else -> StabilityLevel.Unknown
}

internal val versionComparator: Comparator<Version> = createVersionComparator()

private val knownVersionSuffixes = listOf("-android", "-jre")
private val knownStableKeywords = listOf("RELEASE", "FINAL", "GA")
private val digitsOnlyBasedVersionNumberRegex = "^[0-9,.v-]+$".toRegex()

private fun Version.isStable(): Boolean {
    val version = value
    val uppercaseVersion = version.toUpperCase()
    val hasStableKeyword = knownStableKeywords.any { it in uppercaseVersion }
    return hasStableKeyword || digitsOnlyBasedVersionNumberRegex.matches(version.withoutKnownSuffixes())
}

private fun Version.isMilestone(): Boolean {
    val version = value
    return when (val indexOfM = version.indexOfLast { it == 'M' }) {
        -1 -> false
        version.lastIndex -> false
        else -> version.substring(startIndex = indexOfM + 1).all { it.isDigit() }
    }
}

@Suppress("NOTHING_TO_INLINE") // used only once above, to keep internal symbols before private ones.
private inline fun createVersionComparator(): Comparator<Version> = object : Comparator<Version> {

    override fun compare(o1: Version, o2: Version): Int {
        if (o1 == o2) return 0

        val v1 = o1.toComparableList()
        val v2 = o2.toComparableList()
        val lastCommonIndex = minOf(v1.lastIndex, v2.lastIndex)
        for (i in 0..lastCommonIndex) {
            val e1 = v1[i]
            val e2 = v2[i]
            when (e1) {
                is Int -> {
                    if (e2 is Int) {
                        val comparison = e1.compareTo(e2)
                        if (comparison != 0) return comparison
                    } else {
                        check(e2 is StabilityLevel)
                        return +1
                    }
                }
                else -> {
                    check(e1 is StabilityLevel)
                    if (e2 is StabilityLevel) {
                        val comparison = reverseStabilityComparator.compare(e1, e2)
                        if (comparison != 0) return comparison
                    } else {
                        check(e2 is Int)
                        return -1
                    }
                }
            }
        }
        return when {
            v1.lastIndex > lastCommonIndex -> {
                val e1 = v1[lastCommonIndex + 1]
                if (e1 is Int) {
                    +1
                } else {
                    check(e1 is StabilityLevel)
                    -1
                }
            }
            v2.lastIndex > lastCommonIndex -> {
                val e2 = v2[lastCommonIndex + 1]
                if (e2 is Int) {
                    -1
                } else {
                    check(e2 is StabilityLevel)
                    +1
                }
            }
            else -> o1.value.compareTo(o2.value)
        }
    }
}

private val reverseStabilityComparator: Comparator<StabilityLevel> = compareByDescending { it }

private fun Version.toComparableList(): List<Comparable<*>> {
    return value.withoutKnownStableKeywordsOrSuffixes().split(".", "-").flatMap {
        it.toIntOrNull()?.let { number -> listOf(number) }
            ?: Version(it).stabilityLevel().let { level ->
                val indexOfLastNonDigit = it.indexOfLast { c -> c.isDigit().not() }
                if (indexOfLastNonDigit == -1 || indexOfLastNonDigit == it.lastIndex) listOf(level)
                else listOf(level, it.substring(startIndex = indexOfLastNonDigit + 1).toInt())
            }
    }
}

private fun String.withoutKnownStableKeywordsOrSuffixes(): String {
    return withoutKnownSuffixes().withoutKnownStableKeywords()
}

private fun String.withoutKnownSuffixes(): String {
    var result: String = this
    for (suffix in knownVersionSuffixes) {
        result = result.removeSuffix(suffix)
    }
    return result
}

private fun String.withoutKnownStableKeywords(): String {
    var result: String = this
    for (suffix in knownStableKeywords) {
        result = result.replace(suffix, "")
    }
    return result
}
