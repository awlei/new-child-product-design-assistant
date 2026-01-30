package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.childproduct.designassistant.model.AgeGroup
import com.childproduct.designassistant.model.CheckStatus
import com.childproduct.designassistant.model.SafetyCheck
import com.childproduct.designassistant.ui.MainViewModel
import com.childproduct.designassistant.ui.UiState

@Composable
fun SafetyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val safetyCheck by viewModel.safetyCheck.collectAsState()

    var productName by remember { mutableStateOf("") }
    var selectedAgeGroup by remember { mutableStateOf(AgeGroup.PRESCHOOL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🛡️ 安全检查",
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
                // 产品名称
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("产品名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 年龄段选择
                Text(
                    text = "目标年龄段",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AgeGroupSelector(
                    selectedAgeGroup = selectedAgeGroup,
                    onAgeGroupSelected = { selectedAgeGroup = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 检查按钮
                Button(
                    onClick = {
                        if (productName.isNotBlank()) {
                            viewModel.performSafetyCheck(productName, selectedAgeGroup)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is UiState.Loading && productName.isNotBlank()
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("开始安全检查")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 显示检查结果
        safetyCheck?.let { check ->
            SafetyCheckResultCard(check = check)
        }
    }
}

@Composable
fun SafetyCheckResultCard(
    check: SafetyCheck,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (check.passed) {
                Color(0xFFE8F5E9)
            } else {
                Color(0xFFFFEBEE)
            }
        )
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
                    text = check.productName,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${check.overallScore}分",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (check.overallScore >= 80) {
                        Color(0xFF4CAF50)
                    } else if (check.overallScore >= 60) {
                        Color(0xFFFF9800)
                    } else {
                        Color(0xFFF44336)
                    }
                )
            }

            Text(
                text = "年龄段: ${check.ageGroup.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "检查项详情",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(check.checks) { checkItem ->
                    SafetyCheckItem(checkItem = checkItem)
                }
            }

            if (check.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "建议",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                check.recommendations.forEach { recommendation ->
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SafetyCheckItem(
    checkItem: com.childproduct.designassistant.model.SafetyItem,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor) = when (checkItem.status) {
        CheckStatus.PASSED -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        CheckStatus.WARNING -> Icons.Default.Warning to Color(0xFFFF9800)
        CheckStatus.FAILED -> Icons.Default.Cancel to Color(0xFFF44336)
        CheckStatus.NOT_APPLICABLE -> Icons.Default.RemoveCircle to Color(0xFF9E9E9E)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column {
                    Text(
                        text = checkItem.itemName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = checkItem.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
