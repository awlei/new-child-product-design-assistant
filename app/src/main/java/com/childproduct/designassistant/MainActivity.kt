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
import com.childproduct.designassistant.ui.theme.ChildProductDesignAssistantTheme

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
    var selectedModule by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("儿童产品设计助手") },
                actions = {
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Share, "导出")
                    }
                }
            )
        },
        bottomBar = {
            val tabLabels = listOf("创意生成", "安全检查", "技术建议", "文档生成", "更多")
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Lightbulb, "创意生成") },
                    label = { Text(tabLabels[0]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Security, "安全检查") },
                    label = { Text(tabLabels[1]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Build, "技术建议") },
                    label = { Text(tabLabels[2]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Description, "文档生成") },
                    label = { Text(tabLabels[3]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.MoreVert, "更多") },
                    label = { Text(tabLabels[4]) }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> CreativeScreen(
                viewModel = viewModel
            )
            1 -> SafetyScreen(
                viewModel = viewModel
            )
            2 -> TechnicalRecommendationScreen(
                viewModel = viewModel
            )
            3 -> DocumentScreen(
                viewModel = viewModel
            )
            4 -> MoreScreen(
                onNavigate = { screen -> selectedModule = screen }
            )
        }
    }
}

@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "文档学习" to Icons.Default.School,
            "智能问答" to Icons.Default.QuestionAnswer,
            "一键生成" to Icons.Default.AutoAwesome,
            "综合报告" to Icons.Default.Assessment,
            "测试矩阵" to Icons.Default.GridOn,
            "设计建议" to Icons.Default.Recommend,
            "竞品参考" to Icons.Default.CompareArrows,
            "合规管理" to Icons.Default.Gavel
        ).forEach { (name, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(name) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(icon, name)
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ChevronRight, null)
                }
            }
        }
    }
}
