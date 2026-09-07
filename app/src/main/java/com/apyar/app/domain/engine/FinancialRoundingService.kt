package com.apyar.app.domain.engine

/**
 * Centralized rounding and monetary distribution service.
 * Guarantees conservation of money without floating point drift:
 * sum(distributedAmounts) == totalAmount.
 */
object FinancialRoundingService {

    /**
     * Distributes an integer total amount according to floating-point shares/weights
     * using the Hamilton / Hare-Niemeyer largest-remainder method.
     *
     * @param totalAmount The total integer monetary amount to distribute.
     * @param weights A list of non-negative weights (e.g. areas, resident counts, or proportions).
     * @return A list of integer allocations whose sum is GUARANTEED to equal [totalAmount].
     */
    fun distributeProportionally(totalAmount: Long, weights: List<Double>): List<Long> {
        if (weights.isEmpty()) return emptyList()
        if (totalAmount <= 0L) return List(weights.size) { 0L }

        val totalWeight = weights.sum()
        if (totalWeight <= 0.0) {
            // If all weights are 0, distribute equally
            return distributeEqually(totalAmount, weights.size)
        }

        // Calculate integer floor allocations and keep track of fractional remainders
        val exactAllocations = weights.map { (it / totalWeight) * totalAmount }
        val floorAllocations = exactAllocations.map { it.toLong() }.toMutableList()
        val remainders = exactAllocations.mapIndexed { index, exact ->
            index to (exact - floorAllocations[index])
        }

        // Calculate total remainder units to distribute
        val currentSum = floorAllocations.sum()
        val remainderUnits = (totalAmount - currentSum).toInt()

        // Distribute remaining 1-unit amounts to the items with the largest fractional remainders
        val sortedRemainders = remainders.sortedByDescending { it.second }
        for (i in 0 until remainderUnits.coerceAtMost(weights.size)) {
            val targetIndex = sortedRemainders[i].first
            floorAllocations[targetIndex] += 1L
        }

        return floorAllocations
    }

    /**
     * Distributes an integer amount equally across N items.
     * Remainder units are distributed 1 by 1 to guarantee sum == totalAmount.
     */
    fun distributeEqually(totalAmount: Long, count: Int): List<Long> {
        if (count <= 0) return emptyList()
        if (totalAmount <= 0L) return List(count) { 0L }

        val baseShare = totalAmount / count
        val remainder = (totalAmount % count).toInt()

        return List(count) { index ->
            if (index < remainder) baseShare + 1L else baseShare
        }
    }
}
