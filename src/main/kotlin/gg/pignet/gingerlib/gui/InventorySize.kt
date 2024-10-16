package gg.pignet.gingerlib.gui

/**
 * Represents the different sizes of inventories in terms of number of slots.
 */
enum class InventorySize(val size: Int) {
    /**
     * Represents an inventory with one row (9 slots).
     */
    ONE_ROW(9),

    /**
     * Represents an inventory with two rows (18 slots).
     */
    TWO_ROW(18),

    /**
     * Represents an inventory with three rows (27 slots).
     */
    THREE_ROW(27),

    /**
     * Represents an inventory with four rows (36 slots).
     */
    FOUR_ROW(36),

    /**
     * Represents an inventory with five rows (45 slots).
     */
    FIVE_ROW(45),

    /**
     * Represents an inventory with six rows (54 slots).
     */
    SIX_ROW(54),
}
