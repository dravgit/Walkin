package com.cyp.walkin.cyp.models

class VisitorResponseModel(val person_contact: String, val objective_type: String, val objective_note: String, val department: String, val contact_code: String, val name: String, val idcard: String, val vehicle_id: String, val temperature: String, val checkin_time: String, val checkout_time: String, val status: String, val stauts_name: String, val images: List<ImageModel>, var from: String?): BaseResponseModel(){
    fun objective(): String { return objective_type }
    fun objective_note(): String { return objective_note }
    fun department(): String { return department }
    fun contact_code(): String { return contact_code }
    fun name(): String { return name }
    fun idcard(): String { return idcard }
    fun vehicle_id(): String { return vehicle_id }
    fun temperature(): String { return temperature }
    fun checkin_time(): String { return checkin_time }
    fun from(): String? {
        if (from == null) {
            from = ""
        }
        return from
    }
}
