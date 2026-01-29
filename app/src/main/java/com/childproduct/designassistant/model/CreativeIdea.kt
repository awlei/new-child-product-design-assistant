package com.childproduct.designassistant.model

data class CreativeIdea(
    val id: String,
    val title: String,
    val description: String,
    val ageGroup: AgeGroup,
    val productType: ProductType,
    val theme: String,
    val features: List<String>,
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

enum class ProductType(val displayName: String) {
    CHILD_SAFETY_SEAT("儿童安全座椅"),
    BABY_STROLLER("婴儿推车"),
    CHILD_HOUSEHOLD_GOODS("儿童家庭用品")
}
