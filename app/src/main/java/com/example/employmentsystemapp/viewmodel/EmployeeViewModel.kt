package com.example.employmentsystemapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.employmentsystemapp.data.Employee
import com.example.employmentsystemapp.data.EmployeeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val allEmployees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val employees: StateFlow<List<Employee>> = combine(allEmployees, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.department.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee: StateFlow<Employee?> = _selectedEmployee.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessage = _toastMessage.asSharedFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun selectEmployee(employee: Employee?) {
        _selectedEmployee.value = employee
    }

    fun addEmployee(name: String, position: String, department: String, salary: Double) {
        viewModelScope.launch {
            repository.insert(Employee(name = name, position = position, department = department, salary = salary))
            _toastMessage.emit("Employee added")
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.update(employee)
            _selectedEmployee.value = null
            _toastMessage.emit("Employee updated")
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.delete(employee)
            _toastMessage.emit("Employee deleted")
        }
    }
}

class EmployeeViewModelFactory(private val repository: EmployeeRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            return EmployeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
