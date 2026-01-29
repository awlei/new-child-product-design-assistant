package com.childproduct.designassistant.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.childproduct.designassistant.model.*
import com.childproduct.designassistant.service.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val creativeService = CreativeService()
    private val safetyService = SafetyService()
    private val documentService = DocumentService()
    private val technicalAnalysisEngine = TechnicalAnalysisEngine()

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _creativeIdea = MutableStateFlow<CreativeIdea?>(null)
    val creativeIdea: StateFlow<CreativeIdea?> = _creativeIdea.asStateFlow()

    private val _safetyCheck = MutableStateFlow<SafetyCheck?>(null)
    val safetyCheck: StateFlow<SafetyCheck?> = _safetyCheck.asStateFlow()

    private val _designDocument = MutableStateFlow<DesignDocument?>(null)
    val designDocument: StateFlow<DesignDocument?> = _designDocument.asStateFlow()

    private val _technicalRecommendation = MutableStateFlow<TechnicalRecommendation?>(null)
    val technicalRecommendation: StateFlow<TechnicalRecommendation?> = _technicalRecommendation.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun generateCreativeIdea(
        ageGroup: AgeGroup,
        productType: ProductType,
        theme: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val idea = creativeService.generateCreativeIdea(ageGroup, productType, theme)
                _creativeIdea.value = idea
                _uiState.value = UiState.Success("创意生成成功！")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("生成失败：${e.message}")
            }
        }
    }

    fun performSafetyCheck(
        productName: String,
        ageGroup: AgeGroup
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val check = safetyService.performSafetyCheck(productName, ageGroup)
                _safetyCheck.value = check
                _uiState.value = UiState.Success("安全检查完成！得分：${check.overallScore}")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("检查失败：${e.message}")
            }
        }
    }

    fun generateDesignDocument(productName: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val document = documentService.generateDesignDocument(
                    productName,
                    _creativeIdea.value,
                    _safetyCheck.value
                )
                _designDocument.value = document
                _uiState.value = UiState.Success("文档生成成功！")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("文档生成失败：${e.message}")
            }
        }
    }

    fun generateTechnicalRecommendation(
        heightRange: String,
        weightRange: String,
        productType: ProductType,
        technicalQuestion: TechnicalQuestion
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val recommendation = technicalAnalysisEngine.generateTechnicalRecommendation(
                    heightRange,
                    weightRange,
                    productType,
                    technicalQuestion
                )
                _technicalRecommendation.value = recommendation
                _uiState.value = UiState.Success("技术建议生成成功！")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("生成失败：${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Initial
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
