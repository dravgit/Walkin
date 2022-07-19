package com.cyp.walkin.cyp.models

class CheckInResponseModel(var from: String?, val contact_code: String, val idcard: String, val fullname: String, val vehicle_id: String, val temperature: String, val department: String, val objective_type: String, val chcekin_time: String, val objective_note: String, val person_contact: String): BaseResponseModel() {
    fun from(): String? {
        if (from == null) {
            from = ""
        }
        return from
    }
}