package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.childproduct.designassistant.model.*
import com.childproduct.designassistant.ui.MainViewModel
import com.childproduct.designassistant.ui.UiState

/**
 * 改进的创意生成界面
 * 使用身高范围精准输入替代年龄段选择
 */
@Composable
fun CreativeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val creativeIdea by viewModel.creativeIdea.collectAsState()

    var minHeight by remember { mutableStateOf("") }
    var maxHeight by remember { mutableStateOf("") }
    var selectedProductType by remember { mutableStateOf(ProductType.CHILD_SAFETY_SEAT) }
    var theme by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎨 创意生成",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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
                    text = "目标身高范围（cm）",
                    style = MaterialTheme.typography.titleMedium
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

                // 年龄区间提示
                if (minHeight.isNotBlank() && maxHeight.isNotBlank()) {
                    val ageHint = getAgeRangeHint(minHeight.toIntOrNull() ?: 0, maxHeight.toIntOrNull() ?: 0)
                    if (ageHint != null) {
                        Text(
                            text = "对应年龄区间: $ageHint",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // 产品类型选择
                Text(
                    text = "选择产品类型",
                    style = MaterialTheme.typography.titleMedium
                )
                ProductTypeSelector(
                    selectedProductType = selectedProductType,
                    onProductTypeSelected = { selectedProductType = it }
                )

                // 设计主题
                OutlinedTextField(
                    value = theme,
                    onValueChange = { theme = it },
                    label = { Text("设计主题（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 生成按钮
                Button(
                    onClick = {
                        val ageGroup = inferAgeGroup(minHeight.toIntOrNull() ?: 0, maxHeight.toIntOrNull() ?: 0)
                        viewModel.generateCreativeIdea(ageGroup, selectedProductType, theme)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is UiState.Loading &&
                              minHeight.isNotBlank() &&
                              maxHeight.isNotBlank()
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("生成创意")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 显示生成的创意
        creativeIdea?.let { idea ->
            CreativeIdeaCard(idea = idea)
        }
    }
}

/**
 * 根据身高范围推断年龄区间提示
 */
private fun getAgeRangeHint(minHeight: Int, maxHeight: Int): String? {
    if (minHeight <= 0 || maxHeight <= 0) return null

    return when {
        maxHeight <= 75 -> "0-18个月"
        minHeight >= 75 && maxHeight <= 100 -> "15个月-4岁"
        minHeight >= 100 && maxHeight <= 150 -> "4岁-12岁"
        else -> null
    }
}

/**
 * 根据身高范围推断年龄段
 */
private fun inferAgeGroup(minHeight: Int, maxHeight: Int): AgeGroup {
    return when {
        maxHeight <= 75 -> AgeGroup.INFANT
        minHeight >= 75 && maxHeight <= 100 -> AgeGroup.PRESCHOOL
        minHeight >= 100 && maxHeight <= 150 -> AgeGroup.SCHOOL_AGE
        else -> AgeGroup.PRESCHOOL
    }
}

/**
 * 年龄段选择器（保留用于其他场景）
 */
@Composable
fun AgeGroupSelector(
    selectedAgeGroup: AgeGroup,
    onAgeGroupSelected: (AgeGroup) -> Unit
) {
    Column {
        AgeGroup.values().forEach { ageGroup ->
            FilterChip(
                selected = selectedAgeGroup == ageGroup,
                onClick = { onAgeGroupSelected(ageGroup) },
                label = { Text(ageGroup.displayName) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

/**
 * 产品类型选择器
 */
@Composable
fun ProductTypeSelector(
    selectedProductType: ProductType,
    onProductTypeSelected: (ProductType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductType.values().forEach { productType ->
            FilterChip(
                selected = selectedProductType == productType,
                onClick = { onProductTypeSelected(productType) },
                label = { Text(productType.displayName) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 创意展示卡片
 */
@Composable
fun CreativeIdeaCard(
    idea: CreativeIdea,
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
                text = idea.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "设计主题: ${idea.theme}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "年龄段: ${idea.ageGroup.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "产品类型: ${idea.productType.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "功能特性:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            idea.features.forEach { feature ->
                Text(
                    text = "• $feature",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
            }

            if (idea.materials.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "推荐材料:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                idea.materials.forEach { material ->
                    Text(
                        text = "• $material",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }
            }
        }
    }
}
