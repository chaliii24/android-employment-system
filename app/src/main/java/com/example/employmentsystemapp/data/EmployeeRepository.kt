package com.example.employmentsystemapp.data

import kotlinx.coroutines.flow.Flow

class EmployeeRepository(private val dao: EmployeeDao) {
    val allEmployees: Flow<List<Employee>> = dao.getAllEmployees()

    suspend fun insert(employee: Employee) = dao.insertEmployee(employee)
    suspend fun update(employee: Employee) = dao.updateEmployee(employee)
    suspend fun delete(employee: Employee) = dao.deleteEmployee(employee)

    suspend fun seedIfEmpty() {
        if (dao.getCount() == 0) {
            dao.insertEmployee(Employee(name = "Charles Arradaza", position = "IT Specialist", department = "IT Operations", salary = 32000.0))
            dao.insertEmployee(Employee(name = "Ian Manatad", position = "Software Engineer", department = "Dev Team", salary = 35000.0))
            dao.insertEmployee(Employee(name = "Rolando Caporas", position = "System Admin", department = "Infrastructure", salary = 30000.0))
        }
    }
}
