package com.example.employmentsystemapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.employmentsystemapp.data.Employee
import com.example.employmentsystemapp.data.EmployeeRepository
import com.example.employmentsystemapp.ui.theme.Emerald600
import com.example.employmentsystemapp.ui.theme.Red600
import com.example.employmentsystemapp.ui.theme.Indigo100
import com.example.employmentsystemapp.ui.theme.Indigo50
import com.example.employmentsystemapp.ui.theme.Indigo500
import com.example.employmentsystemapp.ui.theme.Indigo600
import com.example.employmentsystemapp.ui.theme.Indigo700
import com.example.employmentsystemapp.ui.theme.Slate100
import com.example.employmentsystemapp.ui.theme.Slate200
import com.example.employmentsystemapp.ui.theme.Slate400
import com.example.employmentsystemapp.ui.theme.Slate500
import com.example.employmentsystemapp.ui.theme.Slate800
import com.example.employmentsystemapp.viewmodel.EmployeeViewModel
import com.example.employmentsystemapp.viewmodel.EmployeeViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(repository: EmployeeRepository) {
    val context = LocalContext.current
    val viewModel: EmployeeViewModel = viewModel(factory = EmployeeViewModelFactory(repository))
    val employees by viewModel.employees.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<Employee?>(null) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Slate100,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Employment System", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text("Jetpack Compose + Room DB", fontSize = 11.sp, color = Indigo100)
                    }
                },
                actions = {
                    Surface(
                        color = Indigo500,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "${employees.size} Employee${if (employees.size == 1) "" else "s"}",
                            color = Indigo100,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Indigo600)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingEmployee = null
                    viewModel.selectEmployee(null)
                    showSheet = true
                },
                containerColor = Indigo600,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add employee", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Indigo50)
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Search by name or department...", color = Slate400, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // List
            if (employees.isEmpty() || employees.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.department.contains(searchQuery, ignoreCase = true)
                }.isEmpty()) {
                // empty handled inside LazyColumn? Show placeholder
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filtered = employees.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.department.contains(searchQuery, ignoreCase = true)
                }
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = Slate200, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("No employees found", color = Slate400, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { employee ->
                        EmployeeCard(
                            employee = employee,
                            onEdit = {
                                editingEmployee = employee
                                viewModel.selectEmployee(employee)
                                showSheet = true
                            },
                            onDelete = { employeeToDelete = employee }
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                editingEmployee = null
                viewModel.selectEmployee(null)
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color.White
        ) {
            EmployeeFormSheet(
                employee = editingEmployee,
                onSave = { name, position, department, salary ->
                    val current = editingEmployee
                    if (current == null) {
                        viewModel.addEmployee(name, position, department, salary)
                    } else {
                        viewModel.updateEmployee(current.copy(name = name, position = position, department = department, salary = salary))
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showSheet = false
                            editingEmployee = null
                            viewModel.selectEmployee(null)
                        }
                    }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                        editingEmployee = null
                        viewModel.selectEmployee(null)
                    }
                }
            )
        }
    }

    employeeToDelete?.let { emp ->
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            shape = RoundedCornerShape(16.dp),
            title = { Text("Delete Employee?", fontWeight = FontWeight.Bold, color = Slate800) },
            text = { Text("Are you sure you want to remove this record from the SQLite database?", fontSize = 13.sp, color = Slate500) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEmployee(emp)
                    employeeToDelete = null
                }) { Text("Delete", color = Red600, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { employeeToDelete = null }) { Text("Cancel", color = Slate500) }
            }
        )
    }
}

@Composable
fun EmployeeCard(employee: Employee, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(employee.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Slate800)
                    Surface(color = Indigo50, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            employee.department,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Indigo600,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(employee.position, fontSize = 12.sp, color = Slate500)
                Text(
                    "₱" + String.format(java.util.Locale.US, "%,.2f", employee.salary),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald600,
                    fontFamily = FontFamily.Monospace
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Slate400, modifier = Modifier.size(14.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Slate400, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun EmployeeFormSheet(
    employee: Employee?,
    onSave: (String, String, String, Double) -> Unit,
    onCancel: () -> Unit
) {
    val departmentPositionsMap = mapOf(
        "Human Resources" to listOf(
            "HR Specialist",
            "HR Manager",
            "Recruitment Officer",
            "Payroll Specialist"
        ),
        "IT Operations" to listOf(
            "IT Specialist",
            "Software Engineer",
            "System Admin",
            "Network Engineer"
        ),
        "Finance" to listOf(
            "Accountant",
            "Financial Analyst",
            "Auditor",
            "Billing Specialist"
        ),
        "Marketing" to listOf(
            "Marketing Officer",
            "Content Creator",
            "SEO Specialist",
            "Brand Manager"
        ),
        "Sales" to listOf(
            "Sales Executive",
            "Account Manager",
            "Sales Representative"
        )
    )

    val departmentList = departmentPositionsMap.keys.toList()

    var name by remember(employee) { mutableStateOf(employee?.name ?: "") }
    var selectedDepartment by remember(employee) {
        mutableStateOf(employee?.department ?: departmentList.first())
    }

    val availablePositions = remember(selectedDepartment) {
        departmentPositionsMap[selectedDepartment] ?: listOf("General Staff")
    }

    var selectedPosition by remember(selectedDepartment) {
        mutableStateOf(
            if (employee != null && availablePositions.contains(employee.position)) {
                employee.position
            } else {
                availablePositions.first()
            }
        )
    }
    var salary by remember(employee) { mutableStateOf(employee?.salary?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (employee == null) "Add New Employee" else "Edit Employee",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Slate800
            )
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Slate400, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Full Name", fontSize = 12.sp) },
            placeholder = { Text("e.g. Juan Cruz", fontSize = 12.sp, color = Slate400) },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DropdownField(
                label = "Department",
                selectedValue = selectedDepartment,
                options = departmentList,
                onValueChange = { newDepartment ->
                    selectedDepartment = newDepartment
                },
                modifier = Modifier.weight(1f).height(56.dp)
            )
            DropdownField(
                label = "Position",
                selectedValue = selectedPosition,
                options = availablePositions,
                onValueChange = { newPosition ->
                    selectedPosition = newPosition
                },
                modifier = Modifier.weight(1f).height(56.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = salary, onValueChange = { salary = it },
            label = { Text("Salary (PHP)", fontSize = 12.sp) },
            placeholder = { Text("e.g. 25000", fontSize = 12.sp, color = Slate400) },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        error?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, color = Red600, fontSize = 11.sp)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Slate500, fontSize = 13.sp, maxLines = 1)
            }
            Button(
                onClick = {
                    val salaryValue = salary.toDoubleOrNull()
                    if (name.isBlank() || selectedPosition.isBlank() || selectedDepartment.isBlank() || salaryValue == null) {
                        error = "Please fill all fields with valid values"
                    } else {
                        onSave(name.trim(), selectedPosition, selectedDepartment, salaryValue)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Save Employee", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
