package com.childproduct.designassistant.service

import com.childproduct.designassistant.data.BrandDatabase
import com.childproduct.designassistant.data.StandardDatabase
import com.childproduct.designassistant.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 技术分析引擎
 * 负责标准匹配、品牌参数整合和技术建议生成
 */
class TechnicalAnalysisEngine {

    private val standardDatabase = StandardDatabase
    private val brandDatabase = BrandDatabase

    suspend fun generateTechnicalRecommendation(
        heightRange: String,
        weightRange: String,
        productType: ProductType,
        technicalQuestion: TechnicalQuestion
    ): TechnicalRecommendation = withContext(Dispatchers.IO) {

        // 1. 匹配标准
        val standardCategory = mapProductTypeToStandardCategory(productType)
        val matchedStandards = matchStandards(heightRange, weightRange, standardCategory)

        // 2. 品牌比较
        val brandComparison = brandDatabase.getBrandComparison(heightRange, weightRange)

        // 3. 生成建议规格
        val suggestedSpecs = generateSuggestedSpecifications(
            matchedStandards,
            brandComparison,
            productType
        )

        // 4. 生成 DVP
        val dvp = generateDVP(
            productType,
            matchedStandards.map { it.standard.region },
            suggestedSpecs,
            technicalQuestion
        )

        // 5. 生成附加说明
        val additionalNotes = generateAdditionalNotes(
            matchedStandards,
            technicalQuestion,
            suggestedSpecs
        )

        TechnicalRecommendation(
            id = UUID.randomUUID().toString(),
            inputParameters = InputParameters(
                heightRange = heightRange,
                weightRange = weightRange,
                productType = productType,
                technicalQuestion = technicalQuestion
            ),
            matchedStandards = matchedStandards,
            brandComparison = brandComparison,
            suggestedSpecifications = suggestedSpecs,
            dvp = dvp,
            additionalNotes = additionalNotes
        )
    }

    /**
     * 匹配标准
     */
    private fun matchStandards(
        heightRange: String,
        weightRange: String,
        category: StandardCategory
    ): List<StandardMatch> {
        val matchedGroups = standardDatabase.findMatchingGroups(heightRange, weightRange, category)
        val results = mutableListOf<StandardMatch>()

        matchedGroups.forEach { (standard, group) ->
            // 计算匹配分数
            val matchScore = calculateMatchScore(heightRange, weightRange, group)
            val notes = generateStandardNotes(standard, group, matchScore)

            results.add(
                StandardMatch(
                    standard = standard,
                    matchingGroup = group,
                    matchScore = matchScore,
                    notes = notes
                )
            )
        }

        // 按匹配分数排序
        return results.sortedByDescending { it.matchScore }
    }

    /**
     * 计算匹配分数
     */
    private fun calculateMatchScore(
        inputHeightRange: String,
        inputWeightRange: String,
        group: StandardGroup
    ): Double {
        val (inMinH, inMaxH) = parseRange(inputHeightRange)
        val (inMinW, inMaxW) = parseRange(inputWeightRange)
        val (gMinH, gMaxH) = parseRange(group.heightRange)
        val (gMinW, gMaxW) = parseRange(group.weightRange)

        // 计算范围重叠程度
        val hOverlap = calculateOverlap(inMinH, inMaxH, gMinH, gMaxH)
        val wOverlap = calculateOverlap(inMinW, inMaxW, gMinW, gMaxW)

        // 权重：重量匹配更重要（60%），身高匹配（40%）
        return (wOverlap * 0.6 + hOverlap * 0.4)
    }

    /**
     * 计算范围重叠度（0-1）
     */
    private fun calculateOverlap(min1: Double, max1: Double, min2: Double, max2: Double): Double {
        val overlapStart = maxOf(min1, min2)
        val overlapEnd = minOf(max1, max2)

        if (overlapEnd <= overlapStart) {
            return 0.0
        }

        val overlapLength = overlapEnd - overlapStart
        val totalLength = maxOf(max1, max2) - minOf(min1, min2)

        return overlapLength / totalLength
    }

    /**
     * 生成标准说明
     */
    private fun generateStandardNotes(
        standard: TechnicalStandard,
        group: StandardGroup,
        matchScore: Double
    ): String {
        val matchLevel = when {
            matchScore >= 0.8 -> "高度匹配"
            matchScore >= 0.6 -> "中度匹配"
            matchScore >= 0.4 -> "部分匹配"
            else -> "匹配度较低"
        }

        return """匹配度: ${String.format("%.0f%%", matchScore * 100)} ($matchLevel)
适用标准: ${standard.code} - ${standard.name}
分组: ${group.code}
重量范围: ${group.weightRange}
身高范围: ${group.heightRange}
${group.ageRange?.let { "建议年龄: $it" } ?: ""}
${group.envelopeClass?.let { "i-Size分类: $it" } ?: ""}
主要要求: ${standard.requirements.size} 项""".trimIndent()
    }

    /**
     * 生成建议规格
     */
    private fun generateSuggestedSpecifications(
        matchedStandards: List<StandardMatch>,
        brandComparison: BrandComparison,
        productType: ProductType
    ): SuggestedSpecifications {
        val avgSpecs = brandComparison.averageSpecs

        // 基于品牌平均值，考虑标准要求进行微调
        val internalWidth = (avgSpecs.avgInternalWidth * 1.1).coerceAtMost(50.0) // 增加10%余量
        val internalDepth = (avgSpecs.avgInternalDepth * 1.05).coerceAtMost(60.0)
        val externalWidth = internalWidth + 12.0
        val externalHeight = internalDepth + 10.0
        val externalDepth = internalDepth + 5.0

        // 提取常用功能
        val features = generateRecommendedFeatures(matchedStandards, avgSpecs.commonFeatures)

        // 推荐标准
        val recommendedStandards = matchedStandards.map { it.standard.code }.distinct()

        return SuggestedSpecifications(
            internalDimensions = InternalDimensions(
                seatWidth = internalWidth,
                seatDepth = internalDepth,
                backrestHeight = externalHeight * 0.85,
                headrestWidth = internalWidth * 0.9,
                shoulderWidth = internalWidth * 0.85
            ),
            externalDimensions = ExternalDimensions(
                width = externalWidth,
                height = externalHeight,
                depth = externalDepth
            ),
            weight = (avgSpecs.avgWeight + 1.5).coerceAtMost(20.0),
            features = features,
            recommendedStandards = recommendedStandards
        )
    }

    /**
     * 生成推荐功能
     */
    private fun generateRecommendedFeatures(
        matchedStandards: List<StandardMatch>,
        commonFeatures: List<String>
    ): List<ProductFeature> {
        val features = mutableListOf<ProductFeature>()

        // 添加常见功能
        commonFeatures.forEach { featureName ->
            when {
                featureName.contains("头托") -> {
                    features.add(
                        ProductFeature(
                            name = "头托高度调节",
                            description = "多档位头托高度调节，适应不同身高儿童",
                            specifications = mapOf(
                                "建议调节范围" to "10-35cm",
                                "建议档位" to "8-12档",
                                "操作方式" to "单手操作"
                            )
                        )
                    )
                }
                featureName.contains("ISOFIX") -> {
                    features.add(
                        ProductFeature(
                            name = "ISOFIX固定",
                            description = "集成ISOFIX接口，确保正确安装",
                            specifications = mapOf(
                                "固定方式" to "ISOFIX + 支撑腿/顶部系带",
                                "安装指示" to "视觉+声音提示"
                            )
                        )
                    )
                }
                featureName.contains("旋转") -> {
                    features.add(
                        ProductFeature(
                            name = "360°旋转",
                            description = "360度旋转，方便抱娃进出",
                            specifications = mapOf(
                                "旋转角度" to "360°",
                                "旋转方式" to "单手操作"
                            )
                        )
                    )
                }
            }
        }

        // 根据标准要求添加功能
        matchedStandards.forEach { match ->
            match.standard.requirements.forEach { req ->
                when (req.category) {
                    RequirementCategory.IMPACT_TEST -> {
                        if (!features.any { it.name.contains("侧面保护") }) {
                            features.add(
                                ProductFeature(
                                    name = "侧面碰撞保护",
                                    description = "侧面碰撞保护系统，符合${match.standard.code}要求",
                                    specifications = mapOf(
                                        "类型" to "吸能材料/侧翼设计",
                                        "测试标准" to match.standard.code
                                    )
                                )
                            )
                        }
                    }
                    RequirementCategory.STRUCTURAL_INTEGRITY -> {
                        if (!features.any { it.name.contains("结构") }) {
                            features.add(
                                ProductFeature(
                                    name = "加强结构设计",
                                    description = "满足${match.standard.code}结构强度要求",
                                    specifications = mapOf(
                                        "材料" to "高强度钢+工程塑料",
                                        "测试" to "静态+动态负载测试"
                                    )
                                )
                            )
                        }
                    }
                    else -> {}
                }
            }
        }

        return features
    }

    /**
     * 生成 DVP（设计验证计划）
     */
    private fun generateDVP(
        productType: ProductType,
        targetMarkets: List<StandardRegion>,
        suggestedSpecs: SuggestedSpecifications,
        technicalQuestion: TechnicalQuestion
    ): DVP {
        val testCases = generateTestCases(
            productType,
            targetMarkets,
            technicalQuestion
        )

        val summary = DVPSummary(
            totalTests = testCases.size,
            criticalTests = testCases.count { it.priority == Priority.CRITICAL },
            estimatedTimeline = estimateTimeline(testCases),
            resourceRequirements = estimateResources(testCases),
            keyRisks = identifyKeyRisks(testCases)
        )

        return DVP(
            id = UUID.randomUUID().toString(),
            projectName = "Child Product Design",
            productType = productType,
            targetMarket = targetMarkets,
            version = "1.0",
            createdDate = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            matrix = testCases,
            summary = summary
        )
    }

    /**
     * 生成测试用例
     */
    private fun generateTestCases(
        productType: ProductType,
        targetMarkets: List<StandardRegion>,
        technicalQuestion: TechnicalQuestion
    ): List<TestCase> {
        val testCases = mutableListOf<TestCase>()

        // 根据目标市场添加标准测试用例
        targetMarkets.forEach { region ->
            val standards = standardDatabase.getStandardsByRegion(region)
            standards.forEach { standard ->
                if (standard.category == mapProductTypeToStandardCategory(productType)) {
                    standard.requirements.forEachIndexed { index, req ->
                        testCases.add(
                            TestCase(
                                id = "TC-${standard.code}-${index + 1}",
                                category = mapRequirementCategory(req.category),
                                testItem = req.description,
                                testMethod = req.testMethod ?: "To be defined",
                                acceptanceCriteria = req.passCriteria ?: "To be defined",
                                testSpec = TestSpecification(
                                    testCondition = "标准条件",
                                    sampleSize = determineSampleSize(req.category),
                                    testDuration = estimateTestDuration(req.category),
                                    equipment = getRequiredEquipment(req.category)
                                ),
                                responsibility = getResponsibleDepartment(req.category),
                                priority = getTestPriority(req.category),
                                status = TestStatus.NOT_STARTED
                            )
                        )
                    }
                }
            }
        }

        // 根据技术问题添加自定义测试
        addCustomTests(testCases, technicalQuestion)

        return testCases.sortedBy { it.priority }
    }

    /**
     * 添加自定义测试用例
     */
    private fun addCustomTests(
        testCases: MutableList<TestCase>,
        question: TechnicalQuestion
    ) {
        when (question.category) {
            QuestionCategory.HEADREST_ADJUSTMENT -> {
                testCases.add(
                    TestCase(
                        id = "TC-CUSTOM-001",
                        category = TestCategory.FUNCTIONAL_TESTING,
                        testItem = "头托调节功能测试",
                        testMethod = "手动调节测试",
                        acceptanceCriteria = "所有档位正常调节，无卡顿",
                        testSpec = TestSpecification(
                            testCondition = "室温25°C，湿度50%",
                            sampleSize = 3,
                            testDuration = "1小时",
                            equipment = "测力计、量具"
                        ),
                        responsibility = "研发部",
                        priority = Priority.HIGH,
                        status = TestStatus.NOT_STARTED
                    )
                )
            }
            QuestionCategory.IMPACT_TESTING -> {
                testCases.add(
                    TestCase(
                        id = "TC-CUSTOM-002",
                        category = TestCategory.IMPACT_TESTING,
                        testItem = "增强碰撞测试",
                        testMethod = "高于标准要求的碰撞测试",
                        acceptanceCriteria = "满足超出标准20%的冲击要求",
                        testSpec = TestSpecification(
                            testCondition = "60km/h 正面碰撞",
                            sampleSize = 5,
                            testDuration = "2周",
                            equipment = "碰撞测试设备、高速摄像机"
                        ),
                        responsibility = "测试部",
                        priority = Priority.CRITICAL,
                        status = TestStatus.NOT_STARTED
                    )
                )
            }
            else -> {}
        }
    }

    /**
     * 生成附加说明
     */
    private fun generateAdditionalNotes(
        matchedStandards: List<StandardMatch>,
        technicalQuestion: TechnicalQuestion,
        suggestedSpecs: SuggestedSpecifications
    ): List<String> {
        val notes = mutableListOf<String>()

        // 标准相关说明
        if (matchedStandards.isNotEmpty()) {
            val topMatch = matchedStandards.first()
            notes.add("💡 主要参考标准: ${topMatch.standard.code}")
            notes.add("   匹配度: ${String.format("%.0f%%", topMatch.matchScore * 100)}")
        }

        // 规格相关说明
        notes.add("📐 建议尺寸基于主流品牌平均值 + 10% 安全余量")
        notes.add("   内部宽度: ${String.format("%.1f", suggestedSpecs.internalDimensions.seatWidth)} cm")
        notes.add("   内部深度: ${String.format("%.1f", suggestedSpecs.internalDimensions.seatDepth)} cm")

        // 技术问题相关说明
        when (technicalQuestion.category) {
            QuestionCategory.HEADREST_ADJUSTMENT -> {
                notes.add("🔧 头托调节建议: 建议8-12档位，调节范围10-35cm")
                notes.add("   头托宽度建议: 座椅宽度的90%左右")
            }
            QuestionCategory.IMPACT_TESTING -> {
                notes.add("🛡️ 碰撞测试: 需同时满足正面和侧面碰撞要求")
                notes.add("   建议进行超越标准10-20%的强化测试")
            }
            QuestionCategory.INSTALLATION -> {
                notes.add("🔌 安装方式: ISOFIX + 支撑腿/顶部系带双重固定")
                notes.add("   建议增加安装错误指示系统")
            }
            else -> {}
        }

        // 合规提醒
        if (suggestedSpecs.recommendedStandards.size > 1) {
            notes.add("⚠️  多市场合规: 建议进行国际标准兼容性测试")
            notes.add("   推荐标准: ${suggestedSpecs.recommendedStandards.joinToString(", ")}")
        }

        return notes
    }

    /**
     * 辅助方法
     */
    private fun parseRange(rangeStr: String): Pair<Double, Double> {
        val cleaned = rangeStr.replace("[^0-9-]".toRegex(), "")
        val parts = cleaned.split("-")
        return if (parts.size == 2) {
            Pair(parts[0].toDouble(), parts[1].toDouble())
        } else {
            Pair(parts[0].toDouble(), parts[0].toDouble())
        }
    }

    private fun mapProductTypeToStandardCategory(productType: ProductType): StandardCategory {
        return when (productType) {
            ProductType.TOY -> StandardCategory.SAFETY_SEAT
            ProductType.FURNITURE -> StandardCategory.CARRIER
            else -> StandardCategory.SAFETY_SEAT
        }
    }

    private fun mapRequirementCategory(reqCategory: RequirementCategory): TestCategory {
        return when (reqCategory) {
            RequirementCategory.IMPACT_TEST -> TestCategory.IMPACT_TESTING
            RequirementCategory.MATERIAL_SAFETY -> TestCategory.MATERIAL_TESTING
            RequirementCategory.STRUCTURAL_INTEGRITY -> TestCategory.DURABILITY_TESTING
            RequirementCategory.FLAMMABILITY -> TestCategory.MATERIAL_TESTING
            RequirementCategory.CHEMICAL_SAFETY -> TestCategory.CHEMICAL_TESTING
            else -> TestCategory.FUNCTIONAL_TESTING
        }
    }

    private fun determineSampleSize(category: RequirementCategory): Int {
        return when (category) {
            RequirementCategory.IMPACT_TEST -> 5
            RequirementCategory.STRUCTURAL_INTEGRITY -> 3
            else -> 1
        }
    }

    private fun estimateTestDuration(category: RequirementCategory): String {
        return when (category) {
            RequirementCategory.IMPACT_TEST -> "1-2周"
            RequirementCategory.STRUCTURAL_INTEGRITY -> "3-5天"
            else -> "1-2天"
        }
    }

    private fun getRequiredEquipment(category: RequirementCategory): String {
        return when (category) {
            RequirementCategory.IMPACT_TEST -> "碰撞测试设备、高速摄像机、数据采集系统"
            RequirementCategory.FLAMMABILITY -> "阻燃测试仪、计时器"
            RequirementCategory.CHEMICAL_SAFETY -> "化学分析仪"
            else -> "标准测试工具"
        }
    }

    private fun getResponsibleDepartment(category: RequirementCategory): String {
        return when (category) {
            RequirementCategory.IMPACT_TEST -> "测试部"
            RequirementCategory.STRUCTURAL_INTEGRITY -> "研发部"
            RequirementCategory.MATERIAL_SAFETY -> "材料部"
            else -> "品质部"
        }
    }

    private fun getTestPriority(category: RequirementCategory): Priority {
        return when (category) {
            RequirementCategory.IMPACT_TEST,
            RequirementCategory.STRUCTURAL_INTEGRITY -> Priority.CRITICAL
            RequirementCategory.MATERIAL_SAFETY,
            RequirementCategory.CHEMICAL_SAFETY -> Priority.HIGH
            else -> Priority.MEDIUM
        }
    }

    private fun estimateTimeline(testCases: List<TestCase>): String {
        val criticalCount = testCases.count { it.priority == Priority.CRITICAL }
        val highCount = testCases.count { it.priority == Priority.HIGH }
        val estimatedWeeks = (criticalCount * 2 + highCount) / 5.0
        return "${String.format("%.1f", estimatedWeeks)} 周"
    }

    private fun estimateResources(testCases: List<TestCase>): List<String> {
        val resources = mutableListOf<String>()
        resources.add("测试工程师: 2-3人")
        resources.add("测试设备: 碰撞测试仪、耐久测试设备等")
        if (testCases.any { it.category == TestCategory.CHEMICAL_TESTING }) {
            resources.add("化学实验室")
        }
        return resources
    }

    private fun identifyKeyRisks(testCases: List<TestCase>): List<String> {
        val risks = mutableListOf<String>()
        val impactTests = testCases.filter { it.category == TestCategory.IMPACT_TESTING }
        if (impactTests.isNotEmpty()) {
            risks.add("碰撞测试可能需要多次迭代")
        }
        risks.add("多市场标准差异可能导致设计冲突")
        return risks
    }
}
