package com.childproduct.designassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childproduct.designassistant.ui.MainViewModel
import com.childproduct.designassistant.ui.UiState
import com.childproduct.designassistant.ui.screens.CreativeScreen
import com.childproduct.designassistant.ui.screens.DocumentScreen
import com.childproduct.designassistant.ui.screens.SafetyScreen
import com.childproduct.designassistant.ui.screens.TechnicalRecommendationScreen
import com.childproduct.designassistant.ui.screens.DocumentLearningScreen
import com.childproduct.designassistant.ui.screens.ChatQAScreen
import com.childproduct.designassistant.ui.screens.OneClickGenerationScreen
import com.childproduct.designassistant.ui.screens.IntegratedReportScreen
import com.childproduct.designassistant.ui.screens.TestMatrixScreen
import com.childproduct.designassistant.ui.screens.DesignSuggestionScreen
import com.childproduct.designassistant.ui.screens.CompetitorReferenceScreen
import com.childproduct.designassistant.ui.screens.ComplianceManagementScreen
import com.childproduct.designassistant.theme.ChildProductDesignAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChildProductDesignAssistantTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "儿童产品设计助手",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    // 一键输出入口
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "一键输出"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text("一键生成") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                    label = { Text("方案整合") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Science, contentDescription = null) },
                    label = { Text("测试矩阵") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                    label = { Text("设计建议") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    label = { Text("竞品参考") }
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { viewModel.selectTab(5) },
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text("文档学习") }
                )
                NavigationBarItem(
                    selected = selectedTab == 6,
                    onClick = { viewModel.selectTab(6) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text("智能问答") }
                )
                NavigationBarItem(
                    selected = selectedTab == 7,
                    onClick = { viewModel.selectTab(7) },
                    icon = { Icon(Icons.Default.Verified, contentDescription = null) },
                    label = { Text("合规管理") }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> OneClickGenerationScreen(viewModel = viewModel)
                1 -> IntegratedReportScreen(viewModel = viewModel)
                2 -> TestMatrixScreen(viewModel = viewModel)
                3 -> DesignSuggestionScreen(viewModel = viewModel)
                4 -> CompetitorReferenceScreen(viewModel = viewModel)
                5 -> DocumentLearningScreen(viewModel = viewModel)
                6 -> ChatQAScreen(viewModel = viewModel)
                7 -> ComplianceManagementScreen(viewModel = viewModel)
            }

            // 显示状态消息
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is UiState.Success -> {
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2000)
                        viewModel.resetState()
                    }
                }
                else -> {}
            }
        }
    }

    // 一键输出对话框
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("导出设计建议")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("请选择导出格式：")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { /* TODO: 导出PDF */ }) {
                            Text("PDF文档")
                        }
                        Button(onClick = { /* TODO: 导出Excel */ }) {
                            Text("Excel表格")
                        }
                    }
                    Text(
                        text = "导出内容将包含：",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "• 创意生成\n• 安全检查\n• 技术建议\n• 设计文档",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
