package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.childproduct.designassistant.model.*
import com.childproduct.designassistant.ui.MainViewModel
import com.childproduct.designassistant.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalRecommendationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val technicalRecommendation by viewModel.technicalRecommendation.collectAsState()

    var heightRange by remember { mutableStateOf("") }
    var weightRange by remember { mutableStateOf("") }
    var selectedProductType by remember { mutableStateOf(ProductType.CHILD_SAFETY_SEAT) }
    var selectedQuestionCategory by remember { mutableStateOf(QuestionCategory.HEADREST_ADJUSTMENT) }
    var questionInput by remember { mutableStateOf("") }

    if (technicalRecommendation == null) {
        // 没有结果时，使用可滚动的 Column
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "🔬 技术建议",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 身高范围
                    OutlinedTextField(
                        value = heightRange,
                        onValueChange = { heightRange = it },
                        label = { Text("身高范围 (cm, 如: 60-120)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例: 60-120") }
                    )

                    // 重量范围
                    OutlinedTextField(
                        value = weightRange,
                        onValueChange = { weightRange = it },
                        label = { Text("重量范围 (kg, 如: 9-36)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例: 9-36") }
                    )

                    // 产品类型选择
                    Text(
                        text = "产品类型",
                        style = MaterialTheme.typography.titleMedium
                    )
                    ProductTypeSelector(
                        selectedProductType = selectedProductType,
                        onProductTypeSelected = { selectedProductType = it }
                    )

                    // 技术问题类别
                    Text(
                        text = "技术问题类别",
                        style = MaterialTheme.typography.titleMedium
                    )
                    QuestionCategorySelector(
                        selectedCategory = selectedQuestionCategory,
                        onCategorySelected = { selectedQuestionCategory = it }
                    )

                    // 技术问题详情
                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        label = { Text("技术问题描述（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    // 生成建议按钮
                    Button(
                        onClick = {
                            val question = TechnicalQuestion(
                                category = selectedQuestionCategory,
                                question = questionInput.ifEmpty { selectedQuestionCategory.name },
                                context = null
                            )
                            viewModel.generateTechnicalRecommendation(
                                heightRange,
                                weightRange,
                                selectedProductType,
                                question
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is UiState.Loading &&
                                  heightRange.isNotBlank() &&
                                  weightRange.isNotBlank()
                    ) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("生成技术建议")
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
                text = "🔬 技术建议",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 身高范围
                    OutlinedTextField(
                        value = heightRange,
                        onValueChange = { heightRange = it },
                        label = { Text("身高范围 (cm, 如: 60-120)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例: 60-120") }
                    )

                    // 重量范围
                    OutlinedTextField(
                        value = weightRange,
                        onValueChange = { weightRange = it },
                        label = { Text("重量范围 (kg, 如: 9-36)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("例: 9-36") }
                    )

                    // 产品类型选择
                    Text(
                        text = "产品类型",
                        style = MaterialTheme.typography.titleMedium
                    )
                    ProductTypeSelector(
                        selectedProductType = selectedProductType,
                        onProductTypeSelected = { selectedProductType = it }
                    )

                    // 技术问题类别
                    Text(
                        text = "技术问题类别",
                        style = MaterialTheme.typography.titleMedium
                    )
                    QuestionCategorySelector(
                        selectedCategory = selectedQuestionCategory,
                        onCategorySelected = { selectedQuestionCategory = it }
                    )

                    // 技术问题详情
                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        label = { Text("技术问题描述（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    // 生成建议按钮
                    Button(
                        onClick = {
                            val question = TechnicalQuestion(
                                category = selectedQuestionCategory,
                                question = questionInput.ifEmpty { selectedQuestionCategory.name },
                                context = null
                            )
                            viewModel.generateTechnicalRecommendation(
                                heightRange,
                                weightRange,
                                selectedProductType,
                                question
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is UiState.Loading &&
                                  heightRange.isNotBlank() &&
                                  weightRange.isNotBlank()
                    ) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("生成技术建议")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 显示技术建议结果
            technicalRecommendation?.let { recommendation ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // 输入参数摘要
                    item {
                        InputParametersCard(recommendation.inputParameters)
                    }

                    // 标准匹配结果
                    item {
                        StandardMatchCard(recommendation.matchedStandards)
                    }

                    // 品牌比较
                    item {
                        BrandComparisonCard(recommendation.brandComparison)
                    }

                    // 建议规格
                    item {
                        SuggestedSpecsCard(recommendation.suggestedSpecifications)
                    }

                    // DVP 测试矩阵
                    item {
                        DVPCard(recommendation.dvp)
                    }

                    // 附加说明
                    if (recommendation.additionalNotes.isNotEmpty()) {
                        item {
                            AdditionalNotesCard(recommendation.additionalNotes)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCategorySelector(
    selectedCategory: QuestionCategory,
    onCategorySelected: (QuestionCategory) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuestionCategory.values().forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(getQuestionCategoryName(category)) }
            )
        }
    }
}

private fun getQuestionCategoryName(category: QuestionCategory): String {
    return when (category) {
        QuestionCategory.HEADREST_ADJUSTMENT -> "头托调节设计"
        QuestionCategory.IMPACT_TESTING -> "碰撞测试要求"
        QuestionCategory.INSTALLATION -> "安装要求"
        QuestionCategory.MATERIAL_SELECTION -> "材料选择"
        QuestionCategory.STRUCTURAL_DESIGN -> "结构设计"
        QuestionCategory.SAFETY_FEATURES -> "安全特性"
        QuestionCategory.REGULATORY_COMPLIANCE -> "法规合规"
    }
}

@Composable
fun InputParametersCard(
    input: InputParameters,
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📋 输入参数",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text("身高范围: ${input.heightRange}")
            Text("重量范围: ${input.weightRange}")
            Text("产品类型: ${input.productType.displayName}")
            Text("技术问题: ${input.technicalQuestion.question}")
        }
    }
}

@Composable
fun StandardMatchCard(
    matchedStandards: List<StandardMatch>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📚 标准匹配",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            matchedStandards.forEach { standard ->
                StandardItem(standard = standard)
            }
        }
    }
}

@Composable
fun StandardItem(
    standard: StandardMatch
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = standard.standardName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (standard.isApplicable) "✓ 适用" else "✗ 不适用",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (standard.isApplicable) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
            if (standard.relevantSections.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "相关条款: ${standard.relevantSections.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            if (standard.complianceNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "合规备注: ${standard.complianceNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun BrandComparisonCard(
    brandComparison: BrandComparison,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔍 品牌对比",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            brandComparison.competitors.forEach { competitor ->
                BrandItem(competitor = competitor)
            }
        }
    }
}

@Composable
fun BrandItem(
    competitor: CompetitorInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = competitor.brandName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (competitor.modelName.isNotEmpty()) {
                Text(
                    text = "型号: ${competitor.modelName}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (competitor.keyFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "关键特性:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                competitor.keyFeatures.forEach { feature ->
                    Text(
                        text = "• $feature",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            if (competitor.advantages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "优势:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                competitor.advantages.forEach { advantage ->
                    Text(
                        text = "• $advantage",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestedSpecsCard(
    specs: SuggestedSpecifications,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📏 建议规格",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (specs.adjustableHeightRanges.isNotEmpty()) {
                Text(
                    text = "可调节高度范围:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                specs.adjustableHeightRanges.forEach { range ->
                    Text(
                        text = "• $range",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (specs.weightCapacity.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "重量容量:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = specs.weightCapacity,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (specs.materialRecommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "材料建议:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                specs.materialRecommendations.forEach { material ->
                    Text(
                        text = "• $material",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (specs.performanceMetrics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "性能指标:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                specs.performanceMetrics.forEach { metric ->
                    Text(
                        text = "• $metric",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (specs.safetyFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "安全特性:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                specs.safetyFeatures.forEach { feature ->
                    Text(
                        text = "• $feature",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DVPCard(
    dvp: DVPTestMatrix,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🧪 DVP测试矩阵",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                dvp.testCases.forEach { testCase ->
                    DVPTestCaseItem(testCase = testCase)
                }
            }
        }
    }
}

@Composable
fun DVPTestCaseItem(
    testCase: DVPTestCase
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = testCase.testId,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = testCase.testName,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "测试标准: ${testCase.testStandard}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "测试方法: ${testCase.testMethod}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "验收标准: ${testCase.acceptanceCriteria}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "测试阶段: ${testCase.testingStage}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun AdditionalNotesCard(
    notes: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📝 附加说明",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            notes.forEach { note ->
                Text(
                    text = "• $note",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}
