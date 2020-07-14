package com.example.walkin.models

class VisitorResponseModel(val contact_code: String, val name: String, val idcard: String, val vehicle_id: String, val temperature: String, val checkin_time: String, val checkout_time: String, val status: String, val stauts_name: String, val images: ImageModel): BaseResponseModel()