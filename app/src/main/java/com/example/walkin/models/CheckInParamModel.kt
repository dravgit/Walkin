package com.example.walkin.models

class CheckInParamModel private constructor(val idcard: String?, val name: String, val vehicleId: String?, val temperature: String?,
                                            val departmentId: String, val objectiveId: String, val images: String) {
    data class Builder(val name: String, val department_id: String, val objective_id: String, val images: String) {
        var idcard: String? = null
            private set
        var vehicle_id: String? = null
            private set
        var temperature: String? = null
            private set

        fun idcard(idcard: String) = apply { this.idcard = idcard }
        fun vehicleId(vehicleId: String) = apply { this.vehicle_id = vehicleId }
        fun temperature(temperature: String) = apply { this.temperature = temperature }

        fun build() = CheckInParamModel(idcard, name, vehicle_id, temperature, department_id, objective_id, images)
    }
}