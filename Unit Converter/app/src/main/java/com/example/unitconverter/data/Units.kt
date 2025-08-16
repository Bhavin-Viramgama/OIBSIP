package com.example.unitconverter.data

enum class Category { LENGTH, WEIGHT, TEMPERATURE }

data class UnitItem(
    val id: String,
    val name: String,
    val symbol: String,
    val toBase: (Double) -> Double,     // -> base unit of the category
    val fromBase: (Double) -> Double    // base -> this unit
)

object UnitsRepo {

    // Base: meter
    val lengthUnits = listOf(
        UnitItem("m",  "Meter",       "m",   { it },                    { it }),
        UnitItem("km", "Kilometer",   "km",  { it * 1_000.0 },          { it / 1_000.0 }),
        UnitItem("cm", "Centimeter",  "cm",  { it / 100.0 },            { it * 100.0 }),
        UnitItem("mm", "Millimeter",  "mm",  { it / 1_000.0 },          { it * 1_000.0 }),
        UnitItem("ft", "Foot",        "ft",  { it * 0.3048 },           { it / 0.3048 }),
        UnitItem("in", "Inch",        "in",  { it * 0.0254 },           { it / 0.0254 }),
        UnitItem("mi", "Mile",        "mi",  { it * 1609.344 },         { it / 1609.344 })
    )

    // Base: kilogram
    val weightUnits = listOf(
        UnitItem("kg", "Kilogram",    "kg",  { it },                    { it }),
        UnitItem("g",  "Gram",        "g",   { it / 1_000.0 },          { it * 1_000.0 }),
        UnitItem("lb", "Pound",       "lb",  { it * 0.453_592_37 },     { it / 0.453_592_37 }),
        UnitItem("oz", "Ounce",       "oz",  { it * 0.028_349_523_1 },  { it / 0.028_349_523_1 })
    )

    // Base: Celsius
    val temperatureUnits = listOf(
        UnitItem("c", "Celsius",     "°C", { it },               { it }),
        UnitItem("f", "Fahrenheit",  "°F", { (it - 32) * 5/9 },  { it * 9/5 + 32 }),
        UnitItem("k", "Kelvin",      "K",  { it - 273.15 },      { it + 273.15 })
    )

    fun units(category: Category) = when (category) {
        Category.LENGTH -> lengthUnits
        Category.WEIGHT -> weightUnits
        Category.TEMPERATURE -> temperatureUnits
    }

    fun convert(value: Double, from: UnitItem, to: UnitItem): Double {
        val base = from.toBase(value)
        return to.fromBase(base)
    }
}
