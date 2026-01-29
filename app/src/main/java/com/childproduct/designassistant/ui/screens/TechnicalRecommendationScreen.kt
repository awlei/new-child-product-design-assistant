package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    var selectedProductType by remember { mutableStateOf(ProductType.TOY) }
    var selectedQuestionCategory by remember { mutableStateOf(QuestionCategory.HEADREST_ADJUSTMENT) }
    var questionInput by remember { mutableStateOf("") }

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
                    enabled = uiState !is MainViewModel.UiState.Loading &&
                              heightRange.isNotBlank() &&
                              weightRange.isNotBlank()
                ) {
                    if (uiState is MainViewModel.UiState.Loading) {
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

@Composable
fun ProductTypeSelector(
    selectedProductType: ProductType,
    onProductTypeSelected: (ProductType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(
            ProductType.TOY to "安全座椅",
            ProductType.FURNITURE to "婴儿提篮",
            ProductType.STATIONERY to "推车"
        ).forEach { (type, label) ->
            FilterChip(
                selected = selectedProductType == type,
                onClick = { onProductTypeSelected(type) },
                label = { Text(label) }
            )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📜 标准匹配结果",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (matchedStandards.isEmpty()) {
                Text(
                    text = "未找到匹配的标准，请检查输入范围",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                matchedStandards.forEach { match ->
                    StandardMatchItem(match)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun StandardMatchItem(
    match: StandardMatch
) {
    val matchColor = when {
        match.matchScore >= 0.8 -> Color(0xFF4CAF50)
        match.matchScore >= 0.6 -> Color(0xFFFF9800)
        match.matchScore >= 0.4 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = matchColor.copy(alpha = 0.1f)
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
                    text = match.standard.code,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.0f", match.matchScore * 100)}% 匹配",
                    style = MaterialTheme.typography.bodySmall,
                    color = matchColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = match.matchingGroup.code,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = match.notes,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun BrandComparisonCard(
    comparison: BrandComparison,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏢 品牌参数对比",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (comparison.brands.isEmpty()) {
                Text("未找到匹配的品牌数据")
            } else {
                // 品牌列表
                Text(
                    text = "参考品牌 (${comparison.brands.size}):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                comparison.brands.take(3).forEach { brand ->
                    Text(
                        text = "• ${brand.brandName} - ${brand.productName}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 平均规格
                Text(
                    text = "平均规格:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "内部宽度: ${String.format("%.1f", comparison.averageSpecs.avgInternalWidth)} cm",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "内部深度: ${String.format("%.1f", comparison.averageSpecs.avgInternalDepth)} cm",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 建议
                Text(
                    text = "💡 建议:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                comparison.recommendations.forEach { rec ->
                    Text(
                        text = "• $rec",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📐 建议规格",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 内部尺寸
            Text(
                text = "内部尺寸:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "座椅宽度: ${String.format("%.1f", specs.internalDimensions.seatWidth)} cm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "座椅深度: ${String.format("%.1f", specs.internalDimensions.seatDepth)} cm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "靠背高度: ${String.format("%.1f", specs.internalDimensions.backrestHeight)} cm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 外部尺寸
            Text(
                text = "外部尺寸:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "宽度: ${String.format("%.1f", specs.externalDimensions.width)} cm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "高度: ${String.format("%.1f", specs.externalDimensions.height)} cm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "深度: ${String.format("%.1f", specs.externalDimensions.depth)} cm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 产品重量
            Text(
                text = "产品重量: ${String.format("%.1f", specs.weight)} kg",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 功能特性
            Text(
                text = "推荐功能特性:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            specs.features.forEach { feature ->
                Text(
                    text = "• ${feature.name}: ${feature.description}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 推荐标准
            Text(
                text = "推荐符合标准: ${specs.recommendedStandards.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DVPCard(
    dvp: DVP,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📋 DVP 测试矩阵",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 摘要
            DVPSummarySection(dvp.summary)

            Spacer(modifier = Modifier.height(12.dp))

            // 测试用例列表
            Text(
                text = "测试用例 (${dvp.matrix.size}):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            dvp.matrix.take(5).forEach { testCase ->
                TestCaseItem(testCase)
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (dvp.matrix.size > 5) {
                Text(
                    text = "... 还有 ${dvp.matrix.size - 5} 个测试用例",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun DVPSummarySection(
    summary: DVPSummary
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "• 总测试数: ${summary.totalTests}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "• 关键测试: ${summary.criticalTests}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFF44336),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "• 预计时间: ${summary.estimatedTimeline}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun TestCaseItem(
    testCase: TestCase
) {
    val priorityColor = when (testCase.priority) {
        Priority.CRITICAL -> Color(0xFFF44336)
        Priority.HIGH -> Color(0xFFFF9800)
        Priority.MEDIUM -> Color(0xFFFFC107)
        Priority.LOW -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = testCase.id,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = testCase.priority.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = testCase.testItem,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "测试方法: ${testCase.testMethod}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "接受标准: ${testCase.acceptanceCriteria}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💬 附加说明",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            notes.forEach { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}
