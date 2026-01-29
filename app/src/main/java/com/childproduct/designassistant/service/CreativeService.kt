package com.childproduct.designassistant.service

import com.childproduct.designassistant.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CreativeService {

    private val ageGroupThemes = mapOf(
        AgeGroup.INFANT to listOf("柔和色彩", "圆形设计", "安全材料", "触感刺激"),
        AgeGroup.TODDLER to listOf("鲜艳色彩", "简单互动", "大尺寸", "耐摔材料"),
        AgeGroup.PRESCHOOL to listOf("教育主题", "创意涂鸦", "角色扮演", "拼图游戏"),
        AgeGroup.SCHOOL_AGE to listOf("科技元素", "团队合作", "探索发现", "竞技挑战"),
        AgeGroup.TEEN to listOf("个性化设计", "社交元素", "技能培养", "潮流风格")
    )

    private val productTypeFeatures = mapOf(
        ProductType.TOY to listOf("互动功能", "教育价值", "安全性", "耐用性"),
        ProductType.STATIONERY to listOf("人体工学", "环保材料", "收纳功能", "个性化"),
        ProductType.CLOTHING to listOf("舒适透气", "易穿脱", "成长设计", "安全标识"),
        ProductType.FURNITURE to listOf("稳定结构", "圆角设计", "可调节", "环保材料"),
        ProductType.EDUCATIONAL to listOf("循序渐进", "趣味性", "实践操作", "成果可视化"),
        ProductType.CREATIVE to listOf("自由发挥", "材料丰富", "指导清晰", "成果展示")
    )

    private val colorPalettes = mapOf(
        AgeGroup.INFANT to listOf("#FFB6C1", "#E0FFFF", "#F0E68C", "#E6E6FA"),
        AgeGroup.TODDLER to listOf("#FF6347", "#4169E1", "#32CD32", "#FFD700"),
        AgeGroup.PRESCHOOL to listOf("#FF69B4", "#00CED1", "#FFA500", "#9370DB"),
        AgeGroup.SCHOOL_AGE to listOf("#1E90FF", "#00FA9A", "#FF4500", "#9932CC"),
        AgeGroup.TEEN to listOf("#000000", "#FFFFFF", "#808080", "#C0C0C0", "#FF1493")
    )

    suspend fun generateCreativeIdea(
        ageGroup: AgeGroup,
        productType: ProductType,
        customTheme: String = ""
    ): CreativeIdea = withContext(Dispatchers.IO) {
        val themes = ageGroupThemes[ageGroup] ?: emptyList()
        val features = productTypeFeatures[productType] ?: emptyList()
        val colors = colorPalettes[ageGroup] ?: emptyList()

        val finalTheme = if (customTheme.isNotEmpty()) customTheme else themes.random()
        val selectedFeatures = features.shuffled().take(4)
        val selectedColors = colors.shuffled().take(4)

        val title = generateTitle(ageGroup, productType, finalTheme)
        val description = generateDescription(ageGroup, productType, finalTheme, selectedFeatures)
        val safetyNotes = generateSafetyNotes(ageGroup, productType)

        CreativeIdea(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            ageGroup = ageGroup,
            productType = productType,
            theme = finalTheme,
            features = selectedFeatures,
            colorPalette = selectedColors,
            safetyNotes = safetyNotes
        )
    }

    private fun generateTitle(ageGroup: AgeGroup, productType: ProductType, theme: String): String {
        return when {
            ageGroup == AgeGroup.INFANT && productType == ProductType.TOY ->
                "婴幼儿安抚${productType.displayName} - $theme"
            ageGroup == AgeGroup.TODDLER && productType == ProductType.TOY ->
                "早教互动${productType.displayName} - $theme"
            else ->
                "${ageGroup.displayName}${productType.displayName} - $theme"
        }
    }

    private fun generateDescription(
        ageGroup: AgeGroup,
        productType: ProductType,
        theme: String,
        features: List<String>
    ): String {
        return "专为${ageGroup.displayName}儿童设计的${productType.displayName}，" +
                "融入${theme}设计理念。主要特点包括：${features.joinToString("、")}。 " +
                "产品设计充分考虑儿童发展特点，注重安全性、教育性和趣味性。"
    }

    private fun generateSafetyNotes(ageGroup: AgeGroup, productType: ProductType): List<String> {
        val notes = mutableListOf<String>()

        when (ageGroup) {
            AgeGroup.INFANT, AgeGroup.TODDLER -> {
                notes.add("确保所有部件尺寸大于3.5cm，防止吞咽风险")
                notes.add("使用食品级安全材料，无甲醛、无重金属")
                notes.add("所有边缘需圆角处理，无尖锐部位")
            }
            AgeGroup.PRESCHOOL -> {
                notes.add("避免细小零件脱落风险")
                notes.add("材料需通过欧盟EN71安全认证")
                notes.add("结构稳固，不易倒塌")
            }
            else -> {
                notes.add("符合国家玩具安全标准GB 6675")
                notes.add("注意电气安全（如适用）")
                notes.add("提供清晰的使用说明和安全警示")
            }
        }

        return notes
    }
}
