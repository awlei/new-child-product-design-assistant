package com.childproduct.designassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.childproduct.designassistant.model.DesignDocument
import com.childproduct.designassistant.ui.MainViewModel
import com.childproduct.designassistant.ui.UiState

@Composable
fun DocumentScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val designDocument by viewModel.designDocument.collectAsState()
    val creativeIdea by viewModel.creativeIdea.collectAsState()

    var productName by remember { mutableStateOf("") }

    if (designDocument == null) {
        // 没有文档时，使用可滚动的 Column
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝 设计文档",
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // 提示信息
                    if (creativeIdea != null) {
                        Text(
                            text = "ℹ️ 将使用之前生成的创意和安全检查结果生成文档",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 生成文档按钮
                    Button(
                        onClick = {
                            if (productName.isNotBlank()) {
                                viewModel.generateDesignDocument(productName)
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
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("生成设计文档")
                        }
                    }
                }
            }
        }
    } else {
        // 有文档时，使用不带滚动的 Column + LazyColumn
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "📝 设计文档",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 输入表单（简化版）
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "当前文档: $productName",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 显示生成的文档
            designDocument?.let { document ->
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DesignDocumentCard(document = document)
                    }
                }
            }
        }
    }
}

@Composable
fun DesignDocumentCard(
    document: DesignDocument,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = document.productName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "版本: ${document.version} | 日期: ${document.createdDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            document.sections.sortedBy { it.order }.forEach { section ->
                DocumentSection(section = section)
            }
        }
    }
}

@Composable
fun DocumentSection(
    section: com.childproduct.designassistant.model.DocumentSection,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = section.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
