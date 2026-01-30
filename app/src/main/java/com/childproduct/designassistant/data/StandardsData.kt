package com.childproduct.designassistant.data

import com.childproduct.designassistant.model.*

/**
 * ECE R129:2013 标准条款数据
 */
object ECE_R129_Clauses {
    // 头托调节
    val HEADREST_ADJUSTMENT = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§5.4.2",
        clauseTitle = "头托适配性要求",
        clauseContent = "头托应可调节，以适应不同身高儿童。头托调节范围应至少为154mm，每档调节步长不大于22mm，至少提供7档调节位置。",
        clauseType = ClauseType.DIMENSIONAL_SPEC,
        relatedSections = listOf("§5.4", "§5.4.1", "§5.4.3")
    )

    // 座椅调节角度（正向）
    val SEAT_ANGLE_FORWARD = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§5.2.3",
        clauseTitle = "坐姿稳定性要求",
        clauseContent = "正向安装时，座椅应提供至少10°的固定调节角度，以确保儿童坐姿稳定性和安全性。",
        clauseType = ClauseType.REQUIREMENT,
        relatedSections = listOf("§5.2", "§5.2.1", "§5.2.2")
    )

    // 倾斜角度（后向）
    val RECLINE_ANGLE_REAR = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§5.2.4",
        clauseTitle = "脊柱保护要求",
        clauseContent = "后向安装时，座椅应提供至少10°的倾斜角度调节，以保护儿童脊柱发育，特别是在长时间使用时。",
        clauseType = ClauseType.REQUIREMENT,
        relatedSections = listOf("§5.2", "§5.2.1")
    )

    // ISOFIX接口尺寸
    val ISOFIX_INTERFACE = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§5.5.1",
        clauseTitle = "接口尺寸规范",
        clauseContent = "ISOFIX接口间距应为280mm±5mm，接口应能承受至少5000N的拉力，持续10秒无变形或断裂。",
        clauseType = ClauseType.DIMENSIONAL_SPEC,
        relatedSections = listOf("§5.5", "§5.5.2")
    )

    // 支撑腿
    val SUPPORT_LEG = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§5.5.3",
        clauseTitle = "支撑腿适配要求",
        clauseContent = "支撑腿长度应可调，调节范围120-200mm。支撑腿应能承受15kg负重，静置30分钟无压缩失效，座椅无倾斜。",
        clauseType = ClauseType.REQUIREMENT,
        relatedSections = listOf("§5.5", "§5.5.2")
    )

    // 正向撞击测试
    val FRONT_IMPACT_TEST = StandardClause(
        standardName = "ECE R129 Rev.4",
        clauseId = "§6.6.4.3.1",
        clauseTitle = "正向碰撞测试要求（Table 4）",
        clauseContent = "使用Q0、Q1.5、Q3、Q6、Q10假人进行正向碰撞测试，碰撞速度50km/h。合格标准：头部位移符合平面限制；HPC≤600(Q0,Q1,Q1.5,Q3)或≤800(Q6,Q10)；头部加速度3ms≤75g(Q0,Q1,Q1.5,Q3)或≤80g(Q6,Q10)；胸部加速度3ms≤55g；腹部压力≤1.2bar(Q1.5,Q10)或≤1.0bar(Q3,Q6)。",
        clauseType = ClauseType.TESTING_METHOD,
        relatedSections = listOf("§6", "§6.6.4", "§6.6.4.3", "§7.1.3")
    )

    // 侧面撞击测试
    val SIDE_IMPACT_TEST = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§6.3",
        clauseTitle = "侧面碰撞测试要求",
        clauseContent = "使用Q1.5假人进行侧面碰撞测试，侧撞台车速度32km/h。合格标准：侧面防护结构无破裂；安全带无松脱；头部位移≤15cm。",
        clauseType = ClauseType.TESTING_METHOD,
        relatedSections = listOf("§6", "§6.2")
    )

    // ISOFIX接口强度测试
    val ISOFIX_STRENGTH_TEST = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§6.4.1",
        clauseTitle = "ISOFIX接口强度测试",
        clauseContent = "对ISOFIX接口施加5000N拉力，持续10秒。合格标准：接口无变形、无断裂，位移不超过2mm。",
        clauseType = ClauseType.ACCEPTANCE_CRITERIA,
        relatedSections = listOf("§6.4", "§6.4.2")
    )

    // 支撑腿稳定性测试
    val SUPPORT_LEG_STABILITY_TEST = StandardClause(
        standardName = "ECE R129:2013",
        clauseId = "§6.4.3",
        clauseTitle = "支撑腿稳定性测试",
        clauseContent = "支撑腿承载15kg负重，静置30分钟。合格标准：支撑腿无压缩失效；座椅无倾斜；支撑腿变形不超过1mm。",
        clauseType = ClauseType.ACCEPTANCE_CRITERIA,
        relatedSections = listOf("§6.4")
    )

    // 产品分类
    val PRODUCT_CLASSIFICATION = StandardClause(
        standardName = "ECE R129 Rev.4",
        clauseId = "§6.1.2.7",
        clauseTitle = "年龄和身高限制要求",
        clauseContent = "15个月以下儿童必须使用后向或侧向儿童约束系统。后向座椅应能容纳身高至83cm的儿童。前向座椅不应设计用于容纳身高低于76cm的儿童。可转换座椅在其后向配置下应能容纳身高至83cm的儿童。非整体式儿童约束系统不得批准低于100cm的身高，上限不能低于105cm，头部保护应覆盖至135cm。",
        clauseType = ClauseType.REQUIREMENT,
        relatedSections = listOf("§6", "§6.1", "§6.1.2")
    )

    fun getAllClauses(): List<StandardClause> {
        return listOf(
            HEADREST_ADJUSTMENT,
            SEAT_ANGLE_FORWARD,
            RECLINE_ANGLE_REAR,
            ISOFIX_INTERFACE,
            SUPPORT_LEG,
            FRONT_IMPACT_TEST,
            SIDE_IMPACT_TEST,
            ISOFIX_STRENGTH_TEST,
            SUPPORT_LEG_STABILITY_TEST,
            PRODUCT_CLASSIFICATION
        )
    }
}

/**
 * 儿童安全座椅产品配置项
 */
object ChildSafetySeatConfigurations {
    val ISOFIX_INTERFACE = ProductConfiguration(
        configId = "isofix_interface",
        configName = "ISOFIX接口",
        applicableProductTypes = listOf(ProductType.CHILD_SAFETY_SEAT),
        isRequired = true,
        relatedClauses = listOf(ECE_R129_Clauses.ISOFIX_INTERFACE),
        description = "国际标准化固定接口，提供更稳固的安装方式"
    )

    val SUPPORT_LEG = ProductConfiguration(
        configId = "support_leg",
        configName = "支撑腿",
        applicableProductTypes = listOf(ProductType.CHILD_SAFETY_SEAT),
        isRequired = true,
        relatedClauses = listOf(ECE_R129_Clauses.SUPPORT_LEG),
        description = "防止座椅在碰撞中向前倾倒，提高稳定性"
    )

    val TOP_TETHER = ProductConfiguration(
        configId = "top_tether",
        configName = "顶部系带",
        applicableProductTypes = listOf(ProductType.CHILD_SAFETY_SEAT),
        isRequired = false,
        relatedClauses = emptyList(),
        description = "提供额外的固定点，适用于正向安装"
    )

    val SIDE_IMPACT_PROTECTION = ProductConfiguration(
        configId = "side_impact_protection",
        configName = "侧面碰撞保护",
        applicableProductTypes = listOf(ProductType.CHILD_SAFETY_SEAT),
        isRequired = true,
        relatedClauses = listOf(ECE_R129_Clauses.SIDE_IMPACT_TEST),
        description = "提供侧面碰撞时的额外保护"
    )

    val ADJUSTABLE_HEADREST = ProductConfiguration(
        configId = "adjustable_headrest",
        configName = "可调头托",
        applicableProductTypes = listOf(ProductType.CHILD_SAFETY_SEAT),
        isRequired = true,
        relatedClauses = listOf(ECE_R129_Clauses.HEADREST_ADJUSTMENT),
        description = "根据儿童身高调节头托高度，提供最佳保护"
    )

    fun getConfigurationsForProduct(productType: ProductType): List<ProductConfiguration> {
        return when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> listOf(
                ISOFIX_INTERFACE,
                SUPPORT_LEG,
                TOP_TETHER,
                SIDE_IMPACT_PROTECTION,
                ADJUSTABLE_HEADREST
            )
            else -> emptyList()
        }
    }
}

/**
 * 标准匹配服务
 */
class StandardMatchingService {

    /**
     * 根据身高范围匹配标准
     */
    fun matchStandardByHeight(
        minHeightCm: Int,
        maxHeightCm: Int,
        productType: ProductType
    ): StandardMatchResult? {
        return when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> matchChildSafetySeatStandard(minHeightCm, maxHeightCm)
            else -> null
        }
    }

    /**
     * 匹配儿童安全座椅标准
     */
    private fun matchChildSafetySeatStandard(
        minHeightCm: Int,
        maxHeightCm: Int
    ): StandardMatchResult {
        val standardName = "ECE R129 Rev.4"
        var productClassification = ""
        var ageRange = ""
        var configurationRequirements = mutableListOf<String>()
        val relevantClauses = mutableListOf<StandardClause>()

        // 根据身高范围确定分组（基于R129r4e §6.1.2.7要求）
        when {
            // 新生儿到15个月（40-83cm）- 必须后向
            maxHeightCm <= 83 -> {
                productClassification = "后向安装 / i-Size"
                ageRange = "新生儿 - 15个月"
                configurationRequirements.add("后向安装（ECE R129 §6.1.2.7）")
                configurationRequirements.add("ISOFIX接口（ECE R129 §6.1.2.1）")
                configurationRequirements.add("支撑腿或顶部系带（ECE R129 §6.1.2.1）")
                relevantClauses.addAll(listOf(
                    ECE_R129_Clauses.PRODUCT_CLASSIFICATION,
                    ECE_R129_Clauses.RECLINE_ANGLE_REAR,
                    ECE_R129_Clauses.ISOFIX_INTERFACE,
                    ECE_R129_Clauses.SUPPORT_LEG
                ))
            }
            // 15个月以上，76-105cm - 可以前向
            minHeightCm >= 76 && maxHeightCm <= 105 -> {
                productClassification = "前向安装 / i-Size"
                ageRange = "15个月 - 4岁"
                configurationRequirements.add("ISOFIX接口（ECE R129 §6.1.2.1）")
                configurationRequirements.add("支撑腿或顶部系带（ECE R129 §6.1.2.1）")
                relevantClauses.addAll(listOf(
                    ECE_R129_Clauses.PRODUCT_CLASSIFICATION,
                    ECE_R129_Clauses.ISOFIX_INTERFACE,
                    ECE_R129_Clauses.SUPPORT_LEG
                ))
            }
            // 增高垫（100cm以上）- 非整体式
            minHeightCm >= 100 && maxHeightCm >= 105 -> {
                productClassification = "非整体式 / i-Size booster seat"
                ageRange = "4岁 - 12岁"
                configurationRequirements.add("侧面碰撞保护至135cm（ECE R129 §6.1.3.3）")
                configurationRequirements.add("ISOFIX接口（可选）")
                relevantClauses.addAll(listOf(
                    ECE_R129_Clauses.PRODUCT_CLASSIFICATION,
                    ECE_R129_Clauses.SIDE_IMPACT_TEST
                ))
            }
            else -> {
                // 可转换座椅（覆盖多个分组）
                productClassification = "可转换座椅 / i-Size"
                ageRange = "新生儿 - 12岁"
                configurationRequirements.add("后向前向可转换（ECE R129 §6.1.2.7）")
                configurationRequirements.add("ISOFIX接口（ECE R129 §6.1.2.1）")
                configurationRequirements.add("支撑腿或顶部系带（ECE R129 §6.1.2.1）")
                relevantClauses.addAll(listOf(
                    ECE_R129_Clauses.PRODUCT_CLASSIFICATION,
                    ECE_R129_Clauses.RECLINE_ANGLE_REAR,
                    ECE_R129_Clauses.ISOFIX_INTERFACE,
                    ECE_R129_Clauses.SUPPORT_LEG
                ))
            }
        }

        // 添加通用相关条款
        relevantClauses.add(ECE_R129_Clauses.HEADREST_ADJUSTMENT)
        relevantClauses.add(ECE_R129_Clauses.SEAT_ANGLE_FORWARD)

        // 计算对应的重量范围（按标准公式）
        val minWeightKg = (minHeightCm * 0.25).toInt()
        val maxWeightKg = (maxHeightCm * 0.30).toInt()
        val weightRange = "${minWeightKg}-${maxWeightKg} kg"

        return StandardMatchResult(
            standardName = standardName,
            productClassification = productClassification,
            ageRange = ageRange,
            heightRange = "${minHeightCm}-${maxHeightCm} cm",
            weightRange = weightRange,
            relevantClauses = relevantClauses,
            configurationRequirements = configurationRequirements
        )
    }

    /**
     * 根据选中的配置获取设计参数
     */
    fun getDesignParameters(
        productType: ProductType,
        selectedConfigs: List<ProductConfiguration>,
        heightRange: HeightRangeInput
    ): List<DesignParameter> {
        val parameters = mutableListOf<DesignParameter>()

        when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> {
                // 头托调节范围（根据身高范围计算）
                val adjustmentRange = 154
                val steps = 7
                parameters.add(DesignParameter(
                    parameterName = "头托调节范围",
                    specificParameter = "${adjustmentRange}mm（分度：每档22mm，共${steps}档）",
                    relatedClause = ECE_R129_Clauses.HEADREST_ADJUSTMENT
                ))

                // 座椅调节角度
                parameters.add(DesignParameter(
                    parameterName = "座椅调节角度（正向）",
                    specificParameter = "10°（固定调节档位）",
                    relatedClause = ECE_R129_Clauses.SEAT_ANGLE_FORWARD
                ))

                // 倾斜角度（后向）
                parameters.add(DesignParameter(
                    parameterName = "倾斜角度（后向兼容）",
                    specificParameter = "10°（后向安装时启用）",
                    relatedClause = ECE_R129_Clauses.RECLINE_ANGLE_REAR
                ))

                // ISOFIX接口尺寸
                if (selectedConfigs.any { it.configId == "isofix_interface" }) {
                    parameters.add(DesignParameter(
                        parameterName = "ISOFIX接口尺寸",
                        specificParameter = "间距280mm±5mm",
                        tolerance = "±5mm",
                        relatedClause = ECE_R129_Clauses.ISOFIX_INTERFACE
                    ))
                }

                // 支撑腿长度
                if (selectedConfigs.any { it.configId == "support_leg" }) {
                    parameters.add(DesignParameter(
                        parameterName = "支撑腿有效长度",
                        specificParameter = "可调范围：120-200mm",
                        relatedClause = ECE_R129_Clauses.SUPPORT_LEG
                    ))
                }
            }
            else -> {}
        }

        return parameters
    }

    /**
     * 获取合规测试矩阵
     */
    fun getComplianceTests(
        productType: ProductType,
        selectedConfigs: List<ProductConfiguration>
    ): List<ComplianceTestItem> {
        val tests = mutableListOf<ComplianceTestItem>()

        when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> {
                // 正向撞击测试
                tests.add(ComplianceTestItem(
                    testName = "正向撞击测试（Q0, Q1, Q1.5）",
                    testDummy = "Q0, Q1, Q1.5",
                    testConditions = "碰撞速度50km/h，ΔV=52km/h",
                    acceptanceCriteria = "HPC≤600；头部加速度3ms≤75g；胸部加速度3ms≤55g；腹部压力≤1.2bar(Q1.5)",
                    relatedClause = ECE_R129_Clauses.FRONT_IMPACT_TEST,
                    testStandard = "ECE R129 §7.1.3, §6.6.4.3.1"
                ))

                tests.add(ComplianceTestItem(
                    testName = "正向撞击测试（Q3）",
                    testDummy = "Q3",
                    testConditions = "碰撞速度50km/h，ΔV=52km/h",
                    acceptanceCriteria = "HPC≤800；头部加速度3ms≤80g；胸部加速度3ms≤55g；腹部压力≤1.0bar",
                    relatedClause = ECE_R129_Clauses.FRONT_IMPACT_TEST,
                    testStandard = "ECE R129 §7.1.3, §6.6.4.3.1"
                ))

                tests.add(ComplianceTestItem(
                    testName = "正向撞击测试（Q6, Q10）",
                    testDummy = "Q6, Q10",
                    testConditions = "碰撞速度50km/h，ΔV=52km/h",
                    acceptanceCriteria = "HPC≤800；头部加速度3ms≤80g；胸部加速度3ms≤55g；腹部压力≤1.0bar(Q6), 1.2bar(Q10)",
                    relatedClause = ECE_R129_Clauses.FRONT_IMPACT_TEST,
                    testStandard = "ECE R129 §7.1.3, §6.6.4.3.1"
                ))

                // 侧面撞击测试
                if (selectedConfigs.any { it.configId == "side_impact_protection" }) {
                    tests.add(ComplianceTestItem(
                        testName = "侧面撞击测试",
                        testDummy = "Q1.5",
                        testConditions = "侧撞台车速度32km/h",
                        acceptanceCriteria = "侧面防护结构无破裂；安全带无松脱",
                        relatedClause = ECE_R129_Clauses.SIDE_IMPACT_TEST,
                        testStandard = "ECE R129 §6.3"
                    ))
                }

                // ISOFIX接口强度测试
                if (selectedConfigs.any { it.configId == "isofix_interface" }) {
                    tests.add(ComplianceTestItem(
                        testName = "ISOFIX接口强度",
                        testDummy = "-",
                        testConditions = "施加5000N拉力，持续10s",
                        acceptanceCriteria = "接口无变形、无断裂",
                        relatedClause = ECE_R129_Clauses.ISOFIX_STRENGTH_TEST,
                        testStandard = "ECE R129 §6.4.1"
                    ))
                }

                // 支撑腿稳定性测试
                if (selectedConfigs.any { it.configId == "support_leg" }) {
                    tests.add(ComplianceTestItem(
                        testName = "支撑腿稳定性",
                        testDummy = "-",
                        testConditions = "承载15kg负重，静置30min",
                        acceptanceCriteria = "支撑腿无压缩失效；座椅无倾斜",
                        relatedClause = ECE_R129_Clauses.SUPPORT_LEG_STABILITY_TEST,
                        testStandard = "ECE R129 §6.4.3"
                    ))
                }
            }
            else -> {}
        }

        return tests
    }
}

/**
 * 标准核心参数预览数据
 */
data class StandardParameterPreview(
    val paramName: String,
    val paramValue: String,
    val standardSource: String,
    val description: String
)

/**
 * 标准关键词联想
 */
data class StandardKeywordSuggestion(
    val keyword: String,
    val displayText: String,
    val clause: StandardClause
)

/**
 * 标准参数预览服务
 */
class StandardParameterPreviewService {

    /**
     * 获取标准核心参数预览
     */
    fun getParameterPreview(
        productType: ProductType,
        minHeight: Int,
        maxHeight: Int
    ): List<StandardParameterPreview> {
        return when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> getChildSafetySeatParameters(minHeight, maxHeight)
            ProductType.BABY_STROLLER -> getStrollerParameters(minHeight, maxHeight)
            ProductType.CHILD_HOUSEHOLD_GOODS -> getHouseholdGoodsParameters(minHeight, maxHeight)
            ProductType.CHILD_HIGH_CHAIR -> getHighChairParameters(minHeight, maxHeight)
        }
    }

    /**
     * 儿童安全座椅核心参数
     */
    private fun getChildSafetySeatParameters(minHeight: Int, maxHeight: Int): List<StandardParameterPreview> {
        val params = mutableListOf<StandardParameterPreview>()

        // 外尺寸上限（Envelope要求）
        params.add(StandardParameterPreview(
            paramName = "外尺寸上限",
            paramValue = "宽≤44cm、长≤75cm",
            standardSource = "ECE R129 Envelope",
            description = "确保座椅在车辆安装空间内"
        ))

        // 头托调节范围
        val headrestRange = when {
            maxHeight <= 95 -> "10-25cm"
            maxHeight <= 105 -> "20-35cm"
            maxHeight <= 125 -> "30-50cm"
            else -> "40-60cm"
        }
        params.add(StandardParameterPreview(
            paramName = "头托调节范围",
            paramValue = headrestRange,
            standardSource = "ECE R129 §5.4.2",
            description = "适配身高${minHeight}-${maxHeight}cm"
        ))

        // ISOFIX接口
        params.add(StandardParameterPreview(
            paramName = "ISOFIX接口间距",
            paramValue = "280mm±5mm",
            standardSource = "ECE R129 §5.5.1",
            description = "国际标准化固定接口尺寸"
        ))

        // 支撑腿长度
        params.add(StandardParameterPreview(
            paramName = "支撑腿可调范围",
            paramValue = "120-200mm",
            standardSource = "ECE R129 §5.5.3",
            description = "防止座椅向前倾倒"
        ))

        // 侧撞防护
        params.add(StandardParameterPreview(
            paramName = "侧撞防护结构",
            paramValue = "需含EPS吸能结构",
            standardSource = "GB 27887-2024 §6.4",
            description = "侧面碰撞保护要求"
        ))

        // 座椅角度
        params.add(StandardParameterPreview(
            paramName = "座椅调节角度",
            paramValue = "≥10°固定档位",
            standardSource = "ECE R129 §5.2.3",
            description = "坐姿稳定性要求"
        ))

        return params
    }

    /**
     * 婴儿推车核心参数
     */
    private fun getStrollerParameters(minHeight: Int, maxHeight: Int): List<StandardParameterPreview> {
        val params = mutableListOf<StandardParameterPreview>()

        params.add(StandardParameterPreview(
            paramName = "折叠后尺寸",
            paramValue = "长×宽×高≤ 100×60×30cm",
            standardSource = "GB 14748-2020",
            description = "便于收纳和携带"
        ))

        params.add(StandardParameterPreview(
            paramName = "刹车距离",
            paramValue = "≤ 1.0m（10°斜坡）",
            standardSource = "EN 1888",
            description = "刹车性能要求"
        ))

        params.add(StandardParameterPreview(
            paramName = "座椅角度",
            paramValue = "可调范围135°-175°",
            standardSource = "GB 14748-2020 §4.3",
            description = "保护婴儿脊柱"
        ))

        return params
    }

    /**
     * 儿童家庭用品核心参数
     */
    private fun getHouseholdGoodsParameters(minHeight: Int, maxHeight: Int): List<StandardParameterPreview> {
        return listOf(
            StandardParameterPreview(
                paramName = "材料安全",
                paramValue = "无有害物质释放",
                standardSource = "ISO 8124-3",
                description = "重金属和塑化剂限制"
            ),
            StandardParameterPreview(
                paramName = "结构强度",
                paramValue = "承受1.5倍额定载荷",
                standardSource = "GB 28007-2011",
                description = "使用安全性"
            )
        )
    }

    /**
     * 儿童高脚椅核心参数
     */
    private fun getHighChairParameters(minHeight: Int, maxHeight: Int): List<StandardParameterPreview> {
        return listOf(
            StandardParameterPreview(
                paramName = "座面高度",
                paramValue = "可调范围60-90cm",
                standardSource = "ISO 8124-3",
                description = "适配不同餐桌高度"
            ),
            StandardParameterPreview(
                paramName = "安全带",
                paramValue = "五点式安全带",
                standardSource = "GB 28007-2011 §5.2",
                description = "防止儿童跌落"
            ),
            StandardParameterPreview(
                paramName = "稳定性",
                paramValue = "15°倾斜不翻倒",
                standardSource = "ISO 8124-3 §4.4",
                description = "抗倾倒要求"
            )
        )
    }
}

/**
 * 标准关键词联想服务
 */
class StandardKeywordSuggestionService {

    fun getSuggestions(input: String): List<StandardKeywordSuggestion> {
        if (input.length < 2) return emptyList()

        val suggestions = mutableListOf<StandardKeywordSuggestion>()

        // Envelope相关
        if (input.contains("env", ignoreCase = true)) {
            suggestions.add(StandardKeywordSuggestion(
                keyword = "Envelope",
                displayText = "ECE R129 §5.2.1 Envelope尺寸要求：宽≤44cm",
                clause = ECE_R129_Clauses.SEAT_ANGLE_FORWARD
            ))
        }

        // 侧撞防护相关
        if (input.contains("侧", ignoreCase = true) || input.contains("side", ignoreCase = true)) {
            suggestions.add(StandardKeywordSuggestion(
                keyword = "侧撞防护",
                displayText = "GB 27887-2024 §6.4 侧撞防护：需含EPS吸能结构",
                clause = ECE_R129_Clauses.SIDE_IMPACT_TEST
            ))
        }

        // 头托相关
        if (input.contains("头托", ignoreCase = true) || input.contains("head", ignoreCase = true)) {
            suggestions.add(StandardKeywordSuggestion(
                keyword = "头托调节",
                displayText = "ECE R129 §5.4.2 头托调节范围≥154mm",
                clause = ECE_R129_Clauses.HEADREST_ADJUSTMENT
            ))
        }

        // ISOFIX相关
        if (input.contains("isofix", ignoreCase = true)) {
            suggestions.add(StandardKeywordSuggestion(
                keyword = "ISOFIX",
                displayText = "ECE R129 §5.5.1 ISOFIX接口间距280mm±5mm",
                clause = ECE_R129_Clauses.ISOFIX_INTERFACE
            ))
        }

        // 支撑腿相关
        if (input.contains("支撑", ignoreCase = true) || input.contains("leg", ignoreCase = true)) {
            suggestions.add(StandardKeywordSuggestion(
                keyword = "支撑腿",
                displayText = "ECE R129 §5.5.3 支撑腿可调范围120-200mm",
                clause = ECE_R129_Clauses.SUPPORT_LEG
            ))
        }

        // 碰撞测试相关
        if (input.contains("碰撞", ignoreCase = true) || input.contains("test", ignoreCase = true)) {
            suggestions.add(StandardKeywordSuggestion(
                keyword = "正向碰撞测试",
                displayText = "ECE R129 §6.2 正向碰撞：头部位移＜25cm；HIC＜700",
                clause = ECE_R129_Clauses.FRONT_IMPACT_TEST
            ))
        }

        return suggestions
    }
}
