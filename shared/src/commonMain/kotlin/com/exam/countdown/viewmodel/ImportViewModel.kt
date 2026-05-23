package com.exam.countdown.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.countdown.parser.DatesheetParser
import com.exam.countdown.parser.ParseResult
import com.exam.countdown.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportViewModel(private val repository: ExamRepository) : ViewModel() {

    val rawText = MutableStateFlow("")
    val parseResults = MutableStateFlow<List<ParseResult>>(emptyList())
    val importDone = MutableStateFlow(false)
    val isImporting = MutableStateFlow(false)

    val successCount get() = parseResults.value.filterIsInstance<ParseResult.Success>().size
    val errorCount get() = parseResults.value.filterIsInstance<ParseResult.Error>().size

    fun onTextChanged(text: String) {
        rawText.update { text }
        parseResults.update { emptyList() } // reset preview on edit
    }

    fun parsePreview() {
        val results = DatesheetParser.parse(rawText.value)
        parseResults.update { results }
    }

    fun importValid() {
        val toInsert = parseResults.value
            .filterIsInstance<ParseResult.Success>()
            .map { it.exam }
        if (toInsert.isEmpty()) return

        viewModelScope.launch {
            isImporting.update { true }
            repository.insertAll(toInsert)
            isImporting.update { false }
            importDone.update { true }
        }
    }
}
