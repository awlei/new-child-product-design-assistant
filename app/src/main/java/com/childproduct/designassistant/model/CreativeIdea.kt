package com.childproduct.designassistant.model

data class CreativeIdea(
    val id: String,
    val title: String,
    val description: String,
    val ageGroup: AgeGroup,
    val productType: ProductType,
    val theme: String,
    val features: List<String>,
    val materials: List<String>,
    val colorPalette: List<String>,
    val safetyNotes: List<String>
)

enum class AgeGroup(val displayName: String) {
    INFANT("0-3岁"),
    TODDLER("3-6岁"),
    PRESCHOOL("6-9岁"),
    SCHOOL_AGE("9-12岁"),
    TEEN("12岁以上")
}

enum class ProductType(val displayName: String, val standardAbbr: String, val mainStandards: String) {
    CHILD_SAFETY_SEAT("儿童安全座椅", "ECE/GB", "主标准：ECE R129、GB 27887-2024"),
    BABY_STROLLER("婴儿推车", "EN/GB", "主标准：EN 1888、GB 14748-2020"),
    CHILD_HOUSEHOLD_GOODS("儿童家庭用品", "ISO/GB", "主标准：ISO 8124-3、GB 28007-2011"),
    CHILD_HIGH_CHAIR("儿童高脚椅", "ISO/GB", "主标准：ISO 8124-3、GB 28007-2011")
}
