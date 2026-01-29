package com.childproduct.designassistant.service

import android.content.Context
import com.childproduct.designassistant.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

/**
 * AI分析与生成服务
 */
class AIAnalysisService(private val context: Context) {
    
    companion object {
        private const val TAG = "AIAnalysisService"
        
        // 模型配置
        const val DEFAULT_MODEL = "zhipu_glm4_flash"
        const val TIMEOUT_MS = 30000
        
        // 首选模型列表（按优先级）
        private val PREFERRED_MODELS = listOf(
            "zhipu_glm4_flash",
            "qwen_plus",
            "doubao_pro",
            "deepseek_chat",
            "gemini_2_flash",
            "groq_llama3_1"
        )
    }
    
    /**
     * 生成设计建议
     */
    suspend fun generateDesignSuggestions(
        request: AIGenerationRequest
    ): Result<AIGenerationResponse> = withContext(Dispatchers.IO) {
        try {
            // 构建提示词
            val prompt = buildPrompt(request)
            
            // 选择模型
            val selectedModel = selectModel(request.options.detailLevel)
            
            // 调用LLM
            val response = callLLM(selectedModel, prompt)
            
            // 解析响应
            val suggestions = parseLLMResponse(response, request)
            
            Result.success(
                AIGenerationResponse(
                    success = true,
                    designSuggestions = suggestions.designSuggestions,
                    brandComparison = suggestions.brandComparison,
                    dvpTestMatrix = suggestions.dvpTestMatrix,
                    standardCompliance = suggestions.standardCompliance,
                    tokensUsed = response.tokensUsed,
                    modelUsed = selectedModel,
                    error = null
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 构建提示词
     */
    private fun buildPrompt(request: AIGenerationRequest): String {
        val sb = StringBuilder()
        
        // 角色定义
        sb.appendLine("你是一位专业的儿童产品设计专家，专注于儿童安全座椅和婴儿推车领域。")
        sb.appendLine("你的任务是基于用户需求、国际标准和品牌参数，生成专业、可落地的设计建议。")
        sb.appendLine()
        
        // 输入信息
        sb.appendLine("## 产品信息")
        sb.appendLine("- 产品类型: ${request.productType.displayName}")
        sb.appendLine("- 适用标准: ${request.standard.displayName} (${request.standard.englishName})")
        sb.appendLine()
        
        // 用户需求
        sb.appendLine("## 用户需求")
        when (val input = request.userInput) {
            is ProductInput.SafetySeat -> {
                sb.appendLine("- 标准选择: ${input.data.standard.displayName}")
                input.data.subtype?.let { sb.appendLine("- 产品细分: ${it.displayName}") }
                input.data.heightRange?.let { sb.appendLine("- 身高范围: ${it}") }
                input.data.weightRange?.let { sb.appendLine("- 重量范围: ${it}") }
                input.data.ageGroup?.let { sb.appendLine("- 年龄组: ${it.displayName}") }
                if (input.data.customFeatures.isNotEmpty()) {
                    sb.appendLine("- 自定义特征: ${input.data.customFeatures.joinToString(", ")}")
                }
                if (input.data.specificRequirements.isNotEmpty()) {
                    sb.appendLine("- 专项需求: ${input.data.specificRequirements.joinToString(", ")}")
                }
            }
            is ProductInput.Stroller -> {
                sb.appendLine("- 标准选择: ${input.data.standard.displayName}")
                input.data.subtype?.let { sb.appendLine("- 产品细分: ${it.displayName}") }
                input.data.weightCapacityRange?.let { sb.appendLine("- 承重范围: ${it}") }
                input.data.foldedDimensions?.let { sb.appendLine("- 折叠尺寸: ${it}") }
                input.data.usageScenario?.let { sb.appendLine("- 使用场景: ${it.displayName}") }
                if (input.data.customFeatures.isNotEmpty()) {
                    sb.appendLine("- 自定义特征: ${input.data.customFeatures.joinToString(", ")}")
                }
                if (input.data.specificRequirements.isNotEmpty()) {
                    sb.appendLine("- 专项需求: ${input.data.specificRequirements.joinToString(", ")}")
                }
            }
            is ProductInput.HouseholdGood -> {
                sb.appendLine("- 产品类别: ${input.data.productCategory}")
                sb.appendLine("- 目标年龄组: ${input.data.targetAgeGroup.displayName}")
                sb.appendLine("- 使用场景: ${input.data.usageScenario}")
                if (input.data.keyFeatures.isNotEmpty()) {
                    sb.appendLine("- 关键特征: ${input.data.keyFeatures.joinToString(", ")}")
                }
            }
        }
        sb.appendLine()
        
        // 标准要求
        request.standardData?.let { standard ->
            sb.appendLine("## 标准要求")
            sb.appendLine("- 适用标准: ${standard.standard.displayName}")
            standard.heightRange?.let {
                sb.appendLine("  - 身高要求: ${it.minHeight}-${it.maxHeight}cm, ${it.recommendedDirection.displayName}")
            }
            standard.weightRange?.let {
                sb.appendLine("  - 重量要求: ${it.minWeight}-${it.maxWeight}${it.unit.symbol}")
            }
            standard.dimensionalRequirements?.let {
                sb.appendLine("  - 尺寸要求:")
                it.envelopeDimensions?.let { envelope ->
                    sb.appendLine("    - i-Size包络: ${envelope.width}×${envelope.length}×${envelope.height}cm")
                }
            }
            standard.performanceRequirements?.let {
                sb.appendLine("  - 性能要求:")
                it.impactTestGForce?.let { gforce ->
                    sb.appendLine("    - 碰撞测试G-force: ${gforce.min}-${gforce.max}g")
                }
            }
            sb.appendLine()
        }
        
        // 品牌基准
        if (request.options.includeBrandComparison && request.brandData != null) {
            sb.appendLine("## 品牌基准对比")
            request.brandData.forEach { brand ->
                sb.appendLine("- ${brand.brandName} ${brand.productName}")
                brand.keyAdvantages.forEach { advantage ->
                    sb.appendLine("  - 优势: $advantage")
                }
                brand.technicalSpecs.uniqueFeatures.forEach { feature ->
                    sb.appendLine("  - 特色: $feature")
                }
            }
            sb.appendLine()
        }
        
        // 输出要求
        sb.appendLine("## 输出要求")
        sb.appendLine("请按照以下结构输出JSON格式的设计建议：")
        sb.appendLine()
        sb.appendLine("```json")
        sb.appendLine("{")
        sb.appendLine("  \"designSuggestions\": {")
        sb.appendLine("    \"functionalFeatures\": [")
        sb.appendLine("      {\"category\": \"功能类别\", \"description\": \"描述\", \"recommendation\": \"建议\", \"referenceStandard\": \"标准引用\", \"implementationDifficulty\": \"难度等级\"}")
        sb.appendLine("    ],")
        sb.appendLine("    \"dimensionParameters\": {")
        sb.appendLine("      \"externalDimensions\": {\"width\": {\"recommendedValue\": [min, max], \"unit\": \"cm\", \"rationale\": \"理由\"}},")
        sb.appendLine("      \"internalDimensions\": {\"seatDepth\": {\"recommendedValue\": [min, max], \"unit\": \"cm\", \"rationale\": \"理由\"}}")
        sb.appendLine("    },")
        sb.appendLine("    \"materialRecommendations\": [],")
        sb.appendLine("    \"safetyRecommendations\": []")
        sb.appendLine("  }")
        sb.appendLine("}")
        sb.appendLine("```")
        sb.appendLine()
        
        // 指导原则
        sb.appendLine("## 指导原则")
        sb.appendLine("1. 所有建议必须基于国际标准和行业最佳实践")
        sb.appendLine("2. 优先考虑儿童安全，避免任何潜在风险")
        sb.appendLine("3. 提供具体、可量化的参数（数值范围）")
        sb.appendLine("4. 参考头部品牌（Britax、Maxi-Cosi、Cybex、UPPAbaby等）的设计理念")
        sb.appendLine("5. 针对用户具体需求提供差异化建议")
        sb.appendLine("6. 确保建议的技术可行性和成本合理性")
        sb.appendLine()
        
        return sb.toString()
    }
    
    /**
     * 选择模型
     */
    private fun selectModel(detailLevel: DetailLevel): String {
        return when (detailLevel) {
            DetailLevel.BRIEF -> "zhipu_glm4_flash"
            DetailLevel.STANDARD -> "qwen_plus"
            DetailLevel.DETAILED -> "doubao_pro"
            DetailLevel.COMPREHENSIVE -> "deepseek_chat"
        }
    }
    
    /**
     * 调用LLM API
     */
    private suspend fun callLLM(model: String, prompt: String): LLMResponse = withContext(Dispatchers.IO) {
        // 这里应该集成真实的LLM API调用
        // 当前使用模拟响应作为示例
        
        // 模拟网络延迟
        kotlinx.coroutines.delay(1000)
        
        LLMResponse(
            text = generateMockResponse(prompt),
            model = model,
            tokensUsed = 1500
        )
    }
    
    /**
     * 生成模拟响应（用于演示）
     */
    private fun generateMockResponse(prompt: String): String {
        val response = JSONObject()
        
        val designSuggestions = JSONObject()
        val functionalFeatures = JSONArray()
        
        val feature1 = JSONObject()
        feature1.put("category", "头托调节")
        feature1.put("description", "多档调节头托高度以适应不同身高儿童")
        feature1.put("recommendation", "建议采用10-30cm调节范围，分15档调节，参考Britax头托设计")
        feature1.put("referenceStandard", "ECE R129 i-Size")
        feature1.put("implementationDifficulty", "MODERATE")
        functionalFeatures.put(feature1)
        
        val feature2 = JSONObject()
        feature2.put("category", "侧面碰撞防护")
        feature2.put("description", "增强侧面碰撞时的儿童头部和颈部保护")
        feature2.put("recommendation", "参考Cybex侧面防护系统，采用LSP可调节侧面碰撞保护装置")
        feature2.put("referenceStandard", "ECE R129")
        feature2.put("implementationDifficulty", "DIFFICULT")
        functionalFeatures.put(feature2)
        
        designSuggestions.put("functionalFeatures", functionalFeatures)
        
        val dimensionParameters = JSONObject()
        val externalDimensions = JSONObject()
        val width = JSONObject()
        width.put("recommendedValue", JSONArray(listOf(44.0, 44.0)))
        width.put("unit", "cm")
        width.put("rationale", "符合i-Size Envelope宽度限制")
        externalDimensions.put("width", width)
        
        val height = JSONObject()
        height.put("recommendedValue", JSONArray(listOf(70.0, 81.0)))
        height.put("unit", "cm")
        height.put("rationale", "符合i-Size Envelope高度限制，确保安装空间")
        externalDimensions.put("height", height)
        
        dimensionParameters.put("externalDimensions", externalDimensions)
        designSuggestions.put("dimensionParameters", dimensionParameters)
        
        designSuggestions.put("materialRecommendations", JSONArray())
        designSuggestions.put("safetyRecommendations", JSONArray())
        
        response.put("designSuggestions", designSuggestions)
        
        return response.toString()
    }
    
    /**
     * 解析LLM响应
     */
    private fun parseLLMResponse(
        response: LLMResponse,
        request: AIGenerationRequest
    ): ParsedSuggestions {
        // 这里应该解析真实的LLM响应
        // 当前返回模拟数据
        
        val suggestions = DesignSuggestions(
            productType = request.productType,
            functionalFeatures = emptyList(),
            dimensionParameters = DimensionParameters(
                externalDimensions = ExternalDimensionSuggestion(
                    width = DimensionRecommendation(DoubleRange(44.0, 44.0), "cm", "符合i-Size", "ECE R129"),
                    length = DimensionRecommendation(DoubleRange(70.0, 75.0), "cm", "符合i-Size", "ECE R129"),
                    height = DimensionRecommendation(DoubleRange(70.0, 81.0), "cm", "符合i-Size", "ECE R129"),
                    weight = DimensionRecommendation(DoubleRange(12.0, 15.0), "kg", "优化重量", null),
                    notes = "参考Britax Dualfix M i-Size"
                ),
                internalDimensions = InternalDimensionSuggestion(
                    seatDepth = DimensionRecommendation(DoubleRange(40.0, 45.0), "cm", "适配CRABI假人", "ECE R129"),
                    seatWidth = DimensionRecommendation(DoubleRange(38.0, 40.0), "cm", "确保舒适性", null),
                    backrestHeight = DimensionRecommendation(DoubleRange(55.0, 60.0), "cm", "提供支撑", null),
                    headrestWidth = DimensionRecommendation(DoubleRange(35.0, 38.0), "cm", "侧面防护", "ECE R129"),
                    shoulderWidth = DimensionRecommendation(DoubleRange(32.0, 35.0), "cm", "适配儿童体型", null),
                    notes = "参考Maxi-Cosi设计"
                ),
                adjustmentRanges = listOf(
                    AdjustmentRange("头托高度", 10.0, 30.0, "cm", 15, "参考Britax 15档调节"),
                    AdjustmentRange("靠背角度", -15.0, 30.0, "度", 5, "参考Cybex倾斜设计")
                ),
                envelopeCompliance = EnvelopeCompliance(
                    isCompliant = true,
                    widthComparison = ComparisonResult(44.0, 44.0, true, 0.0, "cm"),
                    lengthComparison = ComparisonResult(73.0, 75.0, true, 2.0, "cm"),
                    heightComparison = ComparisonResult(79.0, 81.0, true, 2.0, "cm"),
                    recommendations = listOf("完全符合i-Size Envelope要求")
                )
            ),
            materialRecommendations = listOf(
                MaterialRecommendation(
                    "座椅表面",
                    "透气网眼布",
                    listOf("透气性好", "易清洁", "耐磨"),
                    SafetyRating.GOOD,
                    listOf("华峰集团", "台达化学")
                ),
                MaterialRecommendation(
                    "吸能材料",
                    "EPS发泡材料",
                    listOf("吸能性能优异", "轻量化", "阻燃"),
                    SafetyRating.EXCELLENT,
                    listOf("巴斯夫", "陶氏化学")
                )
            ),
            safetyRecommendations = listOf(
                SafetyRecommendation(
                    "侧面碰撞防护",
                    "在座椅两侧安装可调节的LSP装置",
                    "采用可伸缩的侧面保护块，调节范围为0-5cm",
                    "ECE R129侧面碰撞测试",
                    Priority.CRITICAL
                ),
                SafetyRecommendation(
                    "安全带调节",
                    "采用单手可调节安全带系统",
                    "设计中央调节按钮，支持单手操作调节高度",
                    "FMVSS 213安全带拉伸测试",
                    Priority.HIGH
                )
            )
        ),
        brandComparison = BrandComparison(
            targetProductType = request.productType,
            comparedBrands = emptyList(),
            summaryAnalysis = "建议结合Britax的吸能技术和Cybex的侧面防护系统",
            differentiatingSuggestions = listOf(
                "采用Britax SafeCell吸能技术底座",
                "集成Cybex SensorSafe智能监测功能",
                "参考UPPAbaby的模块化设计理念"
            )
        ),
        dvpTestMatrix = DVPTestMatrix(
            productType = request.productType,
            standard = request.standard,
            testItems = generateDefaultDVPTestItems(request.productType, request.standard)
        ),
        standardCompliance = StandardCompliance(
            standard = request.standard,
            complianceItems = emptyList(),
            overallCompliance = ComplianceStatus.FULLY_COMPLIANT,
            recommendations = listOf("建议通过完整的ECE R129认证测试")
        )
        
        return ParsedSuggestions(
            designSuggestions = suggestions,
            brandComparison = null,
            dvpTestMatrix = suggestions.dvpTestMatrix,
            standardCompliance = null
        )
    }
    
    /**
     * 生成默认DVP测试项
     */
    private fun generateDefaultDVPTestItems(
        productType: ProductType,
        standard: InternationalStandard
    ): List<DVPTestItem> {
        val testItems = mutableListOf<DVPTestItem>()
        
        when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> {
                testItems.addAll(listOf(
                    DVPTestItem(
                        testId = "IMP-001",
                        testCategory = TestCategory.IMPACT_TESTING,
                        testName = "正面碰撞测试",
                        standardReference = "ECE R129 Annex 8",
                        testMethod = "使用Hybrid III 3岁假人，50km/h正面碰撞",
                        acceptanceCriteria = "G-force < 50g, 头部伤害指标 < 1000",
                        testEquipment = "碰撞测试台，Hybrid III假人",
                        sampleSize = 5,
                        estimatedDuration = "2-3小时",
                        priority = TestPriority.MANDATORY,
                        notes = "必须测试"
                    ),
                    DVPTestItem(
                        testId = "IMP-002",
                        testCategory = TestCategory.IMPACT_TESTING,
                        testName = "侧面碰撞测试",
                        standardReference = "ECE R129 Annex 8",
                        testMethod = "使用Q3s假人，24km/h侧面碰撞",
                        acceptanceCriteria = "G-force < 50g, 头部位移 < 550mm",
                        testEquipment = "碰撞测试台，Q3s假人",
                        sampleSize = 5,
                        estimatedDuration = "2-3小时",
                        priority = TestPriority.MANDATORY,
                        notes = "必须测试"
                    ),
                    DVPTestItem(
                        testId = "DIM-001",
                        testCategory = TestCategory.FUNCTIONAL_TESTING,
                        testName = "包络尺寸合规性测试",
                        standardReference = "ECE R129 Annex 7",
                        testMethod = "测量座椅在所有调节位置的外部尺寸",
                        acceptanceCriteria = "宽度≤44cm, 长度≤75cm, 高度≤81cm",
                        testEquipment = "三维测量仪",
                        sampleSize = 1,
                        estimatedDuration = "30分钟",
                        priority = TestPriority.MANDATORY,
                        notes = "i-Size强制要求"
                    )
                ))
            }
            ProductType.BABY_STROLLER -> {
                testItems.addAll(listOf(
                    DVPTestItem(
                        testId = "BRK-001",
                        testCategory = TestCategory.SAFETY_TESTING,
                        testName = "制动性能测试",
                        standardReference = "EN 1888",
                        testMethod = "在10°斜坡上制动，检查是否滑动",
                        acceptanceCriteria = "无滑动，保持稳定",
                        testEquipment = "斜坡测试台",
                        sampleSize = 3,
                        estimatedDuration = "1小时",
                        priority = TestPriority.MANDATORY,
                        notes = "必须测试"
                    ),
                    DVPTestItem(
                        testId = "FLD-001",
                        testCategory = TestCategory.DURABILITY_TESTING,
                        testName = "折叠机构耐久性测试",
                        standardReference = "EN 1888",
                        testMethod = "重复折叠展开1000次",
                        acceptanceCriteria = "无故障，机构正常",
                        testEquipment = "折叠测试机",
                        sampleSize = 3,
                        estimatedDuration = "8小时",
                        priority = TestPriority.CRITICAL,
                        notes = "关键测试"
                    ),
                    DVPTestItem(
                        testId = "SHK-001",
                        testCategory = TestCategory.IMPACT_TESTING,
                        testName = "避震性能测试",
                        standardReference = "EN 1888",
                        testMethod = "通过颠簸路面，测量加速度",
                        acceptanceCriteria = "加速度 < 3g",
                        testEquipment = "震动测试台",
                        sampleSize = 3,
                        estimatedDuration = "2小时",
                        priority = TestPriority.IMPORTANT,
                        notes = "推荐测试"
                    )
                ))
            }
            ProductType.CHILD_HOUSEHOLD_GOODS -> {
                testItems.addAll(listOf(
                    DVPTestItem(
                        testId = "MAT-001",
                        testCategory = TestCategory.MATERIAL_TESTING,
                        testName = "材料阻燃性测试",
                        standardReference = "ISO 13209",
                        testMethod = "垂直燃烧测试",
                        acceptanceCriteria = "燃烧速率 < 100mm/min",
                        testEquipment = "阻燃测试仪",
                        sampleSize = 5,
                        estimatedDuration = "1小时",
                        priority = TestPriority.MANDATORY,
                        notes = "必须测试"
                    ),
                    DVPTestItem(
                        testId = "CHM-001",
                        testCategory = TestCategory.CHEMICAL_TESTING,
                        testName = "重金属含量测试",
                        standardReference = "GB 6675",
                        testMethod = "ICP-MS检测",
                        acceptanceCriteria = "符合GB 6675限值",
                        testEquipment = "ICP-MS",
                        sampleSize = 1,
                        estimatedDuration = "4小时",
                        priority = TestPriority.MANDATORY,
                        notes = "必须测试"
                    )
                ))
            }
        }
        
        return testItems
    }
    
    /**
     * LLM响应
     */
    private data class LLMResponse(
        val text: String,
        val model: String,
        val tokensUsed: Int
    )
    
    /**
     * 解析后的建议
     */
    private data class ParsedSuggestions(
        val designSuggestions: DesignSuggestions,
        val brandComparison: BrandComparison?,
        val dvpTestMatrix: DVPTestMatrix,
        val standardCompliance: StandardCompliance?
    )
}
