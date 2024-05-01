package ui


internal class Result
    (
// Аккамулятор
    @JvmField
    var acc: Double, // остаток строки, которую мы еще не обработали
    @JvmField
    var rest: String
)
