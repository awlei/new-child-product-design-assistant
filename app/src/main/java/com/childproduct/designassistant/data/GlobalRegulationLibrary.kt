package com.childproduct.designassistant.data

import com.childproduct.designassistant.model.*

/**
 * 全球法规库
 * 按地区+品类分类展示全球法规，支持点击展开查看标准文本核心片段
 */
object GlobalRegulationLibrary {

    /**
     * 获取所有法规分类
     */
    fun getAllRegulations(): List<RegulationCategory> {
        return listOf(
            RegulationCategory(
                region = Region.EU,
                regionEmoji = "🇪🇺",
                regionName = "欧盟",
                regulations = euRegulations
            ),
            RegulationCategory(
                region = Region.US,
                regionEmoji = "🇺🇸",
                regionName = "美国",
                regulations = usRegulations
            ),
            RegulationCategory(
                region = Region.CHINA,
                regionEmoji = "🇨🇳",
                regionName = "中国",
                regulations = chinaRegulations
            ),
            RegulationCategory(
                region = Region.JAPAN,
                regionEmoji = "🇯🇵",
                regionName = "日本",
                regulations = japanRegulations
            ),
            RegulationCategory(
                region = Region.AUSTRALIA,
                regionEmoji = "🇦🇺",
                regionName = "澳大利亚",
                regulations = australiaRegulations
            )
        )
    }

    /**
     * 根据产品类型获取法规
     */
    fun getRegulationsByProductType(productType: ProductType): List<RegulationDetail> {
        return when (productType) {
            ProductType.CHILD_SAFETY_SEAT -> safetySeatRegulations
            ProductType.BABY_STROLLER -> strollerRegulations
            else -> emptyList()
        }
    }

    /**
     * 根据标准编号获取法规详情
     */
    fun getRegulationByCode(code: String): RegulationDetail? {
        return allRegulations.find { it.code == code }
    }

    /**
     * 获取法规详情（含章节）
     */
    fun getRegulationWithSections(code: String): RegulationDetail? {
        return allRegulations.find { it.code == code }
    }

    // ========== 地区枚举 ==========
    enum class Region(val displayName: String) {
        EU("欧盟"),
        US("美国"),
        CHINA("中国"),
        JAPAN("日本"),
        AUSTRALIA("澳大利亚")
    }

    // ========== 法规分类 ==========
    data class RegulationCategory(
        val region: Region,
        val regionEmoji: String,
        val regionName: String,
        val regulations: List<RegulationDetail>
    )

    // ========== 法规详情 ==========
    data class RegulationDetail(
        val code: String,
        val name: String,
        val region: Region,
        val productType: ProductType,
        val version: String,
        val publishDate: String,
        val updateDate: String,
        val description: String,
        val applicableScope: String,
        val keyFeatures: List<String>,
        val sections: List<RegulationSection>,
        val url: String?,
        val isLatest: Boolean = true
    )

    // ========== 法规章节 ==========
    data class RegulationSection(
        val sectionId: String,
        val sectionTitle: String,
        val sectionContent: String,
        val isMandatory: Boolean,
        val relatedTestItems: List<String>
    )

    // ========== 所有法规 ==========
    private val allRegulations = listOf(
        *euRegulations.toTypedArray(),
        *usRegulations.toTypedArray(),
        *chinaRegulations.toTypedArray(),
        *japanRegulations.toTypedArray(),
        *australiaRegulations.toTypedArray()
    )

    // ========== 欧盟法规 ==========
    private val euRegulations = listOf(
        RegulationDetail(
            code = "ECE R129",
            name = "关于儿童约束系统审批的统一规定",
            region = Region.EU,
            productType = ProductType.CHILD_SAFETY_SEAT,
            version = "2023修订版",
            publishDate = "2013-07-09",
            updateDate = "2023-01-15",
            description = "ECE R129（i-Size）是欧盟最新的儿童安全座椅标准，取代了ECE R44标准。该标准基于儿童身高而非体重进行分类，强制要求使用ISOFIX接口，提高了侧面碰撞保护要求。",
            applicableScope = "体重<36kg的儿童约束系统，适用于欧盟成员国及认可该标准的其他国家",
            keyFeatures = listOf(
                "基于身高分类（40-150cm）",
                "强制使用ISOFIX接口",
                "增强侧面碰撞保护要求",
                "延长后向安装年龄至15个月",
                "使用Q系列假人进行测试",
                "提供更明确的车型兼容性"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.3.2",
                    sectionTitle = "正面碰撞测试",
                    sectionContent = "使用Hybrid III 3岁假人，碰撞速度50km/h±1km/h，加速度峰值50g±5g。合格标准：头部伤害指数（HIC）< 700，胸部压缩量< 50mm，头托无脱落，安全带无松脱。",
                    isMandatory = true,
                    relatedTestItems = listOf("IMP-001")
                ),
                RegulationSection(
                    sectionId = "§5.3.3",
                    sectionTitle = "侧面碰撞测试",
                    sectionContent = "使用Q3s假人，碰撞速度32km/h，侧撞角度90°。合格标准：头部位移< 25cm，胸部侧面加速度< 50g，头托无断裂。",
                    isMandatory = true,
                    relatedTestItems = listOf("IMP-002")
                ),
                RegulationSection(
                    sectionId = "§5.4.2",
                    sectionTitle = "头托调节可靠性测试",
                    sectionContent = "头托调节500次循环，每100次施加100N压力。合格标准：无卡滞、无松动、调节顺畅。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-001")
                ),
                RegulationSection(
                    sectionId = "§5.5.1",
                    sectionTitle = "ISOFIX接口强度测试",
                    sectionContent = "ISOFIX连接器施加5000N拉力，持续10秒。合格标准：无变形、无断裂、无永久变形> 2mm。",
                    isMandatory = true,
                    relatedTestItems = listOf("SAFE-001")
                ),
                RegulationSection(
                    sectionId = "Annex 7",
                    sectionTitle = "i-Size包络尺寸",
                    sectionContent = "外部尺寸：宽度≤ 44cm，长度≤ 75cm，高度≤ 81cm。所有i-Size座椅必须在此包络尺寸内，确保与多数车型兼容。",
                    isMandatory = true,
                    relatedTestItems = listOf("DIM-001")
                )
            ),
            url = "https://unece.org/transport/documents/2023/standing-documents/"
        ),
        RegulationDetail(
            code = "EN 1888",
            name = "儿童护理用品—轮式推车",
            region = Region.EU,
            productType = ProductType.BABY_STROLLER,
            version = "2018版",
            publishDate = "2018-04-01",
            updateDate = "2018-04-01",
            description = "EN 1888是欧盟婴儿推车安全标准，规定了推车的机械安全、结构强度、稳定性、制动性能等要求。",
            applicableScope = "重量≤ 22kg的婴儿推车，适用于欧盟市场",
            keyFeatures = listOf(
                "机械安全要求",
                "结构强度测试",
                "稳定性测试",
                "制动性能要求",
                "折叠锁定可靠性",
                "无尖锐边缘和夹伤风险"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§8.1",
                    sectionTitle = "折叠机构安全性",
                    sectionContent = "折叠机构必须防止意外折叠，折叠后必须有锁定装置，解锁操作需要两个独立动作。",
                    isMandatory = true,
                    relatedTestItems = listOf("SAFE-002")
                ),
                RegulationSection(
                    sectionId = "§8.2",
                    sectionTitle = "制动性能",
                    sectionContent = "在12°斜坡上，施加500N拉力，推车不得移动。驻车制动必须明确标识且易于操作。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-002")
                ),
                RegulationSection(
                    sectionId = "§8.3",
                    sectionTitle = "稳定性测试",
                    sectionContent = "在平地上施加50N力于把手，推车不得倾倒。在10°斜坡上，装有15kg假人时不得倾倒。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-003")
                ),
                RegulationSection(
                    sectionId = "§8.4",
                    sectionTitle = "车轮强度测试",
                    sectionContent = "车轮施加300N冲击力，重复1000次，无断裂、无永久变形> 2mm。",
                    isMandatory = true,
                    relatedTestItems = listOf("DUR-001")
                )
            ),
            url = "https://www.cen.eu/"
        )
    )

    // ========== 美国法规 ==========
    private val usRegulations = listOf(
        RegulationDetail(
            code = "FMVSS 213",
            name = "儿童约束系统",
            region = Region.US,
            productType = ProductType.CHILD_SAFETY_SEAT,
            version = "2023版",
            publishDate = "2007-03-01",
            updateDate = "2023-06-01",
            description = "FMVSS 213是美国联邦机动车安全标准，规定了儿童安全座椅的要求，适用于所有在美国销售的新型车辆和儿童安全座椅。",
            applicableScope = "体重< 36kg的儿童约束系统，适用于美国市场",
            keyFeatures = listOf(
                "基于体重分类",
                "使用LATCH系统（类似ISOFIX）",
                "碰撞测试要求",
                "材料阻燃性要求",
                "标签和说明书要求"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§4.3",
                    sectionTitle = "正面碰撞测试",
                    sectionContent = "使用Hybrid III 3岁假人，碰撞速度48km/h，加速度峰值30g。合格标准：头部伤害指数（HIC）< 1000，胸部加速度< 60g。",
                    isMandatory = true,
                    relatedTestItems = listOf("IMP-003")
                ),
                RegulationSection(
                    sectionId = "§4.5",
                    sectionTitle = "靠背角度锁定测试",
                    sectionContent = "在各档位施加200N推力，持续30秒。合格标准：无位移、无失效。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-004")
                ),
                RegulationSection(
                    sectionId = "§5",
                    sectionTitle = "材料阻燃性",
                    sectionContent = "座椅材料必须符合16 CFR 1632阻燃标准，燃烧速度≤ 2.5英寸/分钟。",
                    isMandatory = true,
                    relatedTestItems = listOf("MAT-001")
                )
            ),
            url = "https://www.nhtsa.gov/"
        ),
        RegulationDetail(
            code = "ASTM F833",
            name = "轮式婴儿推车标准消费者安全规范",
            region = Region.US,
            productType = ProductType.BABY_STROLLER,
            version = "ASTM F833-22",
            publishDate = "2022-04-01",
            updateDate = "2022-04-01",
            description = "ASTM F833是美国婴儿推车安全标准，由ASTM国际组织制定，规定了推车的安全要求。",
            applicableScope = "重量≤ 22kg的婴儿推车，适用于美国市场",
            keyFeatures = listOf(
                "安全带要求",
                "刹车性能要求",
                "稳定性要求",
                "折叠锁定要求",
                "无夹伤风险"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§7.3",
                    sectionTitle = "安全带强度测试",
                    sectionContent = "安全带施加200N拉力，持续30秒，无断裂、无松脱。",
                    isMandatory = true,
                    relatedTestItems = listOf("SAFE-003")
                ),
                RegulationSection(
                    sectionId = "§7.4",
                    sectionTitle = "刹车性能测试",
                    sectionContent = "在平地上，装有15kg假人时，刹车后推车不得移动。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-005")
                )
            ),
            url = "https://www.astm.org/"
        )
    )

    // ========== 中国法规 ==========
    private val chinaRegulations = listOf(
        RegulationDetail(
            code = "GB 27887",
            name = "机动车儿童乘员用约束系统",
            region = Region.CHINA,
            productType = ProductType.CHILD_SAFETY_SEAT,
            version = "GB 27887-2024",
            publishDate = "2024-01-01",
            updateDate = "2024-01-01",
            description = "GB 27887是中国儿童安全座椅国家标准，规定了儿童安全座椅的技术要求、试验方法、检验规则等。",
            applicableScope = "体重< 36kg的儿童约束系统，适用于中国市场",
            keyFeatures = listOf(
                "基于体重分类",
                "碰撞测试要求",
                "材料安全要求",
                "标签和说明书要求",
                "与ECE R129对标"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.1",
                    sectionTitle = "正面碰撞测试",
                    sectionContent = "使用Hybrid III 3岁假人，碰撞速度50km/h。合格标准：HIC< 1000，胸部加速度< 60g。",
                    isMandatory = true,
                    relatedTestItems = listOf("IMP-004")
                ),
                RegulationSection(
                    sectionId = "§5.2",
                    sectionTitle = "材料阻燃性",
                    sectionContent = "座椅材料必须符合GB 8410阻燃标准，燃烧速度≤ 100mm/min。",
                    isMandatory = true,
                    relatedTestItems = listOf("MAT-002")
                )
            ),
            url = "https://openstd.samr.gov.cn/"
        ),
        RegulationDetail(
            code = "GB 14748",
            name = "婴儿推车安全要求",
            region = Region.CHINA,
            productType = ProductType.BABY_STROLLER,
            version = "GB 14748-2020",
            publishDate = "2020-07-01",
            updateDate = "2020-07-01",
            description = "GB 14748是中国婴儿推车国家标准，规定了推车的安全要求。",
            applicableScope = "重量≤ 22kg的婴儿推车，适用于中国市场",
            keyFeatures = listOf(
                "机械安全要求",
                "结构强度测试",
                "稳定性测试",
                "制动性能要求"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.1",
                    sectionTitle = "折叠锁定要求",
                    sectionContent = "折叠机构必须有锁定装置，防止意外折叠。",
                    isMandatory = true,
                    relatedTestItems = listOf("SAFE-004")
                ),
                RegulationSection(
                    sectionId = "§5.2",
                    sectionTitle = "制动性能测试",
                    sectionContent = "在平地上，装有15kg假人时，刹车后推车不得移动。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-006")
                )
            ),
            url = "https://openstd.samr.gov.cn/"
        )
    )

    // ========== 日本法规 ==========
    private val japanRegulations = listOf(
        RegulationDetail(
            code = "JIS D 0161",
            name = "儿童约束装置",
            region = Region.JAPAN,
            productType = ProductType.CHILD_SAFETY_SEAT,
            version = "JIS D 0161:2023",
            publishDate = "2023-03-01",
            updateDate = "2023-03-01",
            description = "JIS D 0161是日本儿童安全座椅工业标准，规定了儿童安全座椅的技术要求。",
            applicableScope = "体重< 36kg的儿童约束系统，适用于日本市场",
            keyFeatures = listOf(
                "基于重量分类",
                "碰撞测试要求",
                "材料安全要求",
                "与ECE R129对标"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.1",
                    sectionTitle = "正面碰撞测试",
                    sectionContent = "使用Hybrid III 3岁假人，碰撞速度50km/h。合格标准：HIC< 1000，胸部加速度< 60g。",
                    isMandatory = true,
                    relatedTestItems = listOf("IMP-005")
                )
            ),
            url = "https://www.jisc.go.jp/"
        ),
        RegulationDetail(
            code = "JIS D 9302",
            name = "婴儿推车",
            region = Region.JAPAN,
            productType = ProductType.BABY_STROLLER,
            version = "JIS D 9302:2022",
            publishDate = "2022-06-01",
            updateDate = "2022-06-01",
            description = "JIS D 9302是日本婴儿推车工业标准，规定了推车的安全要求。",
            applicableScope = "重量≤ 22kg的婴儿推车，适用于日本市场",
            keyFeatures = listOf(
                "机械安全要求",
                "结构强度测试",
                "稳定性测试",
                "制动性能要求"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.1",
                    sectionTitle = "制动性能测试",
                    sectionContent = "在平地上，装有15kg假人时，刹车后推车不得移动。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-007")
                )
            ),
            url = "https://www.jisc.go.jp/"
        )
    )

    // ========== 澳大利亚法规 ==========
    private val australiaRegulations = listOf(
        RegulationDetail(
            code = "AS/NZS 1754",
            name = "儿童约束系统",
            region = Region.AUSTRALIA,
            productType = ProductType.CHILD_SAFETY_SEAT,
            version = "AS/NZS 1754:2024",
            publishDate = "2024-01-01",
            updateDate = "2024-01-01",
            description = "AS/NZS 1754是澳大利亚/新西兰儿童安全座椅标准，规定了儿童安全座椅的技术要求。",
            applicableScope = "体重< 36kg的儿童约束系统，适用于澳大利亚/新西兰市场",
            keyFeatures = listOf(
                "基于身高分类",
                "碰撞测试要求",
                "材料安全要求",
                "与ECE R129对标"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.1",
                    sectionTitle = "正面碰撞测试",
                    sectionContent = "使用Hybrid III 3岁假人，碰撞速度50km/h。合格标准：HIC< 1000，胸部加速度< 60g。",
                    isMandatory = true,
                    relatedTestItems = listOf("IMP-006")
                )
            ),
            url = "https://www.standards.org.au/"
        ),
        RegulationDetail(
            code = "AS/NZS 2088",
            name = "婴儿推车",
            region = Region.AUSTRALIA,
            productType = ProductType.BABY_STROLLER,
            version = "AS/NZS 2088:2023",
            publishDate = "2023-06-01",
            updateDate = "2023-06-01",
            description = "AS/NZS 2088是澳大利亚/新西兰婴儿推车标准，规定了推车的安全要求。",
            applicableScope = "重量≤ 22kg的婴儿推车，适用于澳大利亚/新西兰市场",
            keyFeatures = listOf(
                "机械安全要求",
                "结构强度测试",
                "稳定性测试",
                "制动性能要求"
            ),
            sections = listOf(
                RegulationSection(
                    sectionId = "§5.1",
                    sectionTitle = "制动性能测试",
                    sectionContent = "在平地上，装有15kg假人时，刹车后推车不得移动。",
                    isMandatory = true,
                    relatedTestItems = listOf("FUNC-008")
                )
            ),
            url = "https://www.standards.org.au/"
        )
    )

    // ========== 按产品类型分类 ==========
    private val safetySeatRegulations = allRegulations.filter { it.productType == ProductType.CHILD_SAFETY_SEAT }
    private val strollerRegulations = allRegulations.filter { it.productType == ProductType.BABY_STROLLER }
}
