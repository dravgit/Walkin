package com.example.walkin.cyp.models

class DepartmentModel(val id: String, val name: String, val description: String){
    override fun toString(): String {
        return name
    }
    fun getID(): String {
        return id
    }
}