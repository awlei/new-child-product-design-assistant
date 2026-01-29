package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.childproduct.designassistant.model.AgeGroup
import com.childproduct.designassistant.model.CreativeIdea
import com.childproduct.designassistant.model.ProductType
import com.childproduct.designassistant.ui.MainViewModel

@Composable
fun CreativeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val creativeIdea by viewModel.creativeIdea.collectAsState()

    var selectedAgeGroup by remember { mutableStateOf(AgeGroup.PRESCHOOL) }
    var selectedProductType by remember { mutableStateOf(ProductType.TOY) }
    var theme by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
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
                modifier = Modifier.padding(16.dp)
            ) {
                // 年龄段选择
                Text(
                    text = "选择年龄段",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AgeGroupSelector(
                    selectedAgeGroup = selectedAgeGroup,
                    onAgeGroupSelected = { selectedAgeGroup = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 产品类型选择
                Text(
                    text = "选择产品类型",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ProductTypeSelector(
                    selectedProductType = selectedProductType,
                    onProductTypeSelected = { selectedProductType = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 设计主题
                OutlinedTextField(
                    value = theme,
                    onValueChange = { theme = it },
                    label = { Text("设计主题（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 生成按钮
                Button(
                    onClick = {
                        viewModel.generateCreativeIdea(selectedAgeGroup, selectedProductType, theme)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is MainViewModel.UiState.Loading
                ) {
                    if (uiState is MainViewModel.UiState.Loading) {
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

@Composable
fun ProductTypeSelector(
    selectedProductType: ProductType,
    onProductTypeSelected: (ProductType) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 200.dp)
    ) {
        items(ProductType.values()) { productType ->
            FilterChip(
                selected = selectedProductType == productType,
                onClick = { onProductTypeSelected(productType) },
                label = { Text(productType.displayName) }
            )
        }
    }
}

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "色彩方案:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                idea.colorPalette.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(2.dp)
                    ) {
                        Text(
                            text = "●",
                            color = android.graphics.Color.parseColor(color),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            if (idea.safetyNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "安全提示:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                idea.safetyNotes.forEach { note ->
                    Text(
                        text = "⚠️ $note",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                    )
                }
            }
        }
    }
}
