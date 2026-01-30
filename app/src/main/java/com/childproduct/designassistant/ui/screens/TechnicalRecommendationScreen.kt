package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.childproduct.designassistant.data.*
import com.childproduct.designassistant.model.*
import com.childproduct.designassistant.ui.MainViewModel
import com.childproduct.designassistant.ui.UiState
import com.childproduct.designassistant.ui.components.StandardClauseDialog

/**
 * 改进的技术建议界面
 * 支持精准身高范围输入、产品配置选择、实时标准匹配、结构化合规设计方案输出
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalRecommendationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // 输入状态
    var minHeight by remember { mutableStateOf("") }
    var maxHeight by remember { mutableStateOf("") }
    var selectedProductType by remember { mutableStateOf(ProductType.CHILD_SAFETY_SEAT) }
    var selectedConfigurations by remember { mutableStateOf<Set<String>>(emptySet()) }

    // 实时匹配结果
    var standardMatchResult by remember { mutableStateOf<StandardMatchResult?>(null) }
    var designParameters by remember { mutableStateOf<List<DesignParameter>>(emptyList()) }
    var complianceTests by remember { mutableStateOf<List<ComplianceTestItem>>(emptyList()) }

    // 条款对话框状态
    var selectedClause by remember { mutableStateOf<StandardClause?>(null) }
    var showClauseDialog by remember { mutableStateOf(false) }

    // 标准匹配服务
    val matchingService = remember { StandardMatchingService() }

    // 实时标准匹配逻辑
    LaunchedEffect(minHeight, maxHeight, selectedProductType) {
        val minHeightInt = minHeight.toIntOrNull()
        val maxHeightInt = maxHeight.toIntOrNull()

        if (minHeightInt != null && maxHeightInt != null && minHeightInt <= maxHeightInt) {
            standardMatchResult = matchingService.matchStandardByHeight(
                minHeightInt,
                maxHeightInt,
                selectedProductType
            )
        } else {
            standardMatchResult = null
        }
    }

    // 当配置变化时更新设计参数和测试矩阵
    LaunchedEffect(selectedProductType, selectedConfigurations, minHeight, maxHeight) {
        val heightRange = HeightRangeInput(minHeight, maxHeight)
        val configs = ChildSafetySeatConfigurations.getConfigurationsForProduct(selectedProductType)
            .filter { selectedConfigurations.contains(it.configId) }

        designParameters = matchingService.getDesignParameters(
            selectedProductType,
            configs,
            heightRange
        )

        complianceTests = matchingService.getComplianceTests(
            selectedProductType,
            configs
        )
    }

    if (designParameters.isEmpty()) {
        // 无结果时，使用可滚动的 Column
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "🔬 合规设计方案生成",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 输入表单
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 身高范围输入（双栏）
                    Text(
                        text = "身高范围输入（cm）",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = minHeight,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    minHeight = it
                                }
                            },
                            label = { Text("最小身高") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = { Text("如: 40") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = maxHeight,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    maxHeight = it
                                }
                            },
                            label = { Text("最大身高") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = { Text("如: 105") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    // 实时匹配结果显示
                    standardMatchResult?.let { result ->
                        StandardMatchResultCard(result = result)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 产品类型选择
                    Text(
                        text = "产品类型",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ProductTypeSelector(
                        selectedProductType = selectedProductType,
                        onProductTypeSelected = { selectedProductType = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 产品专属配置
                    Text(
                        text = "产品专属配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ProductConfigurationSelector(
                        productType = selectedProductType,
                        selectedConfigurations = selectedConfigurations,
                        onConfigurationSelected = { configId, isSelected ->
                            selectedConfigurations = if (isSelected) {
                                selectedConfigurations + configId
                            } else {
                                selectedConfigurations - configId
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 生成方案按钮
                    Button(
                        onClick = {
                            val question = TechnicalQuestion(
                                category = QuestionCategory.STRUCTURAL_DESIGN,
                                question = "生成$selectedProductType的合规设计方案",
                                context = null
                            )
                            viewModel.generateTechnicalRecommendation(
                                "${minHeight}-${maxHeight}",
                                "0-0", // 重量范围暂不需要
                                selectedProductType,
                                question
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is UiState.Loading &&
                                  minHeight.isNotBlank() &&
                                  maxHeight.isNotBlank() &&
                                  standardMatchResult != null
                    ) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("生成合规设计方案")
                        }
                    }
                }
            }
        }
    } else {
        // 有结果时，使用不带滚动的 Column + LazyColumn
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "🔬 合规设计方案生成",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 输入表单
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 身高范围输入（双栏）
                    Text(
                        text = "身高范围输入（cm）",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = minHeight,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    minHeight = it
                                }
                            },
                            label = { Text("最小身高") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = { Text("如: 40") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = maxHeight,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    maxHeight = it
                                }
                            },
                            label = { Text("最大身高") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = { Text("如: 105") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    // 实时匹配结果显示
                    standardMatchResult?.let { result ->
                        StandardMatchResultCard(result = result)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 产品类型选择
                    Text(
                        text = "产品类型",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ProductTypeSelector(
                        selectedProductType = selectedProductType,
                        onProductTypeSelected = { selectedProductType = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 产品专属配置
                    Text(
                        text = "产品专属配置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ProductConfigurationSelector(
                        productType = selectedProductType,
                        selectedConfigurations = selectedConfigurations,
                        onConfigurationSelected = { configId, isSelected ->
                            selectedConfigurations = if (isSelected) {
                                selectedConfigurations + configId
                            } else {
                                selectedConfigurations - configId
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 生成方案按钮
                    Button(
                        onClick = {
                            val question = TechnicalQuestion(
                                category = QuestionCategory.STRUCTURAL_DESIGN,
                                question = "生成$selectedProductType的合规设计方案",
                                context = null
                            )
                            viewModel.generateTechnicalRecommendation(
                                "${minHeight}-${maxHeight}",
                                "0-0",
                                selectedProductType,
                                question
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is UiState.Loading &&
                                  minHeight.isNotBlank() &&
                                  maxHeight.isNotBlank() &&
                                  standardMatchResult != null
                    ) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("生成合规设计方案")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 结构化合规设计方案
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // 标准匹配结果模块
                item {
                    StandardMatchModuleCard(standardMatchResult)
                }

                // 核心设计参数模块
                item {
                    DesignParametersTableCard(designParameters)
                }

                // 合规测试矩阵模块
                item {
                    ComplianceTestMatrixCard(complianceTests)
                }
            }
        }
    }

    // 条款详情对话框
    if (showClauseDialog && selectedClause != null) {
        StandardClauseDialog(
            clause = selectedClause!!,
            onDismiss = {
                showClauseDialog = false
                selectedClause = null
            }
        )
    }
}

/**
 * 实时标准匹配结果卡片
 */
@Composable
fun StandardMatchResultCard(
    result: StandardMatchResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "已匹配标准",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "${result.standardName}: ${result.productClassification}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "年龄区间: ${result.ageRange}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "身高范围: ${result.heightRange}",
                style = MaterialTheme.typography.bodySmall
            )
            if (result.configurationRequirements.isNotEmpty()) {
                Text(
                    text = result.getComplianceDescription(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 产品配置选择器
 */
@Composable
fun ProductConfigurationSelector(
    productType: ProductType,
    selectedConfigurations: Set<String>,
    onConfigurationSelected: (String, Boolean) -> Unit
) {
    val configurations = ChildSafetySeatConfigurations.getConfigurationsForProduct(productType)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        configurations.forEach { config ->
            val isSelected = selectedConfigurations.contains(config.configId)

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 1.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = config.configName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (config.isRequired) {
                                Text(
                                    text = "*",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = config.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        if (config.relatedClauses.isNotEmpty()) {
                            Text(
                                text = "符合 ${config.relatedClauses.first().getFullReference()} 要求",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onConfigurationSelected(config.configId, it) }
                    )
                }
            }
        }
    }
}

/**
 * 标准匹配模块卡片
 */
@Composable
fun StandardMatchModuleCard(
    result: StandardMatchResult?,
    modifier: Modifier = Modifier
) {
    result ?: return

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryBooks,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "标准匹配结果",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow("匹配标准", result.standardName)
                InfoRow("产品分类", result.productClassification)
                InfoRow("年龄区间", result.ageRange)
                InfoRow("身高范围", result.heightRange)
                InfoRow("重量范围", result.weightRange)

                if (result.configurationRequirements.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "配置合规性：",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    result.configurationRequirements.forEach { requirement ->
                        Text(
                            text = "• $requirement",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 设计参数表格卡片
 */
@Composable
fun DesignParametersTableCard(
    parameters: List<DesignParameter>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "核心设计参数",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            parameters.forEach { parameter ->
                DesignParameterItem(parameter = parameter)
            }
        }
    }
}

/**
 * 设计参数项
 */
@Composable
fun DesignParameterItem(
    parameter: DesignParameter
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 设计项和参数
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = parameter.parameterName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = parameter.specificParameter,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 公差
            parameter.tolerance?.let { tolerance ->
                Text(
                    text = "公差: $tolerance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // 条款溯源
            parameter.relatedClause?.let { clause ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "对应条款: ${clause.getFullReference()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    // 条款直达按钮
                    TextButton(
                        onClick = {
                            parameter.relatedClause?.let { clause ->
                                selectedClause = clause
                                showClauseDialog = true
                            }
                        },
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = "查看原文",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 合规测试矩阵卡片
 */
@Composable
fun ComplianceTestMatrixCard(
    tests: List<ComplianceTestItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "合规测试矩阵",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            tests.forEach { test ->
                ComplianceTestItemCard(test = test)
            }
        }
    }
}

/**
 * 合规测试项卡片
 */
@Composable
fun ComplianceTestItemCard(
    test: ComplianceTestItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 测试项名称
            Text(
                text = test.testName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // 测试详情表格
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InfoRow("测试假人", test.testDummy)
                InfoRow("测试条件", test.testConditions)
                InfoRow("合格阈值", test.acceptanceCriteria)
            }

            // 条款溯源
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "测试标准: ${test.testStandard}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 4.dp)
                )
                // 条款直达按钮
                if (test.relatedClause != null) {
                    TextButton(
                        onClick = {
                            test.relatedClause?.let { clause ->
                                selectedClause = clause
                                showClauseDialog = true
                            }
                        },
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = "查看原文",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 信息行组件
 */
@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.5f)
        )
    }
}
