package com.example.walkin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.walkin.models.DepartmentModel;
import com.example.walkin.models.LoginResponseModel;
import com.example.walkin.models.ObjectiveTypeModel;
import com.example.walkin.models.SignatureModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class PreferenceUtils {

    private static final String PREFERENCE_KEY_TOKEN = "token";
    private static final String PREFERENCE_KEY_LOGIN_USER_NAME = "login_user_name";
    private static final String PREFERENCE_KEY_LOGIN_PASSWORD = "login_password";
    private static final String PREFERENCE_KEY_LOGIN_SUCCESS = "login_success";
    private static final String PREFERENCE_KEY_USER_ID = "user_id";
    private static final String PREFERENCE_KEY_USER_NAME = "user_name";
    private static final String PREFERENCE_KEY_COMPANY_ID = "company_id";
    private static final String PREFERENCE_KEY_COMPANY_NAME = "company_name";
    private static final String PREFERENCE_KEY_COMPANY_ADDRESS = "company_address";
    private static final String PREFERENCE_KEY_COMPANY_PHONE = "company_phone";
    private static final String PREFERENCE_KEY_COMPANY_EMAIL = "company_email";
    private static final String PREFERENCE_KEY_COMPANY_STATUS = "company_status";
    private static final String PREFERENCE_KEY_SIGNATURE = "signature";
    private static final String PREFERENCE_KEY_DEPARTMENT = "department";
    private static final String PREFERENCE_KEY_OBJECTIVE_TYPE = "objective_type";

    private static Context mAppContext;

    // Prevent instantiation
    private PreferenceUtils() {
    }

    public static void init(Context appContext) {
        mAppContext = appContext;
    }

    private static SharedPreferences getSharedPreferences() {
        return mAppContext.getSharedPreferences("walkin", Context.MODE_PRIVATE);
    }

    public static void setUserId(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_USER_ID, userId).apply();
    }

    public static String getUserId() {
        return getSharedPreferences().getString(PREFERENCE_KEY_USER_ID, "");
    }

    public static void setUserName(String userName) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_USER_NAME, userName).apply();
    }

    public static String getUserName() {
        return getSharedPreferences().getString(PREFERENCE_KEY_USER_NAME, "");
    }

    public static void setCompanyId(String companyId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_COMPANY_ID, companyId).apply();
    }

    public static String getCompanyId() {
        return getSharedPreferences().getString(PREFERENCE_KEY_COMPANY_ID, "");
    }

    public static void setCompanyName(String companyName) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_COMPANY_NAME, companyName).apply();
    }

    public static String getCompanyName() {
        return getSharedPreferences().getString(PREFERENCE_KEY_COMPANY_NAME, "");
    }


    public static void setCompanyAddress(String companyAddress) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_COMPANY_ADDRESS, companyAddress).apply();
    }

    public static String getCompanyAddress() {
        return getSharedPreferences().getString(PREFERENCE_KEY_COMPANY_ADDRESS, "");
    }

    public static void setCompanyPhone(String companyPhone) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_COMPANY_PHONE, companyPhone).apply();
    }

    public static String getCompanyPhone() {
        return getSharedPreferences().getString(PREFERENCE_KEY_COMPANY_PHONE, "");
    }


    public static void setCompanyEmail(String companyEmail) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_COMPANY_EMAIL, companyEmail).apply();
    }

    public static String getCompanyEmail() {
        return getSharedPreferences().getString(PREFERENCE_KEY_COMPANY_EMAIL, "");
    }


    public static void setCompanyStatus(String companyStatus) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_COMPANY_STATUS, companyStatus).apply();
    }

    public static String getCompanyStatus() {
        return getSharedPreferences().getString(PREFERENCE_KEY_COMPANY_STATUS, "");
    }

    public static void setSignature(String signature) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_SIGNATURE, signature).apply();
    }

    public static SignatureModel getSignature() {
        String signature = getSharedPreferences().getString(PREFERENCE_KEY_SIGNATURE, "");
        return new Gson().fromJson(signature, SignatureModel.class);
    }

    public static void setDepartment(String department) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_DEPARTMENT, department).apply();
    }

    public static List<DepartmentModel> getDepartment() {
        String department = getSharedPreferences().getString(PREFERENCE_KEY_DEPARTMENT, "");
        return new Gson().fromJson(department, new TypeToken<List<DepartmentModel>>() {}.getType());
    }

    public static void setObjectiveType(String type) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_OBJECTIVE_TYPE, type).apply();
    }

    public static List<ObjectiveTypeModel> getObjectiveType() {
        String objectiveType = getSharedPreferences().getString(PREFERENCE_KEY_OBJECTIVE_TYPE, "");
        return new Gson().fromJson(objectiveType, new TypeToken<List<ObjectiveTypeModel>>() {}.getType());
    }

    public static void setLoginSuccess() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_LOGIN_SUCCESS, true).apply();
    }

    public static boolean isLoginSuccess() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_LOGIN_SUCCESS, false);
    }


    public static void setToken(String token) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_TOKEN, token).apply();
    }

    public static String getToken() {
        return getSharedPreferences().getString(PREFERENCE_KEY_TOKEN, "");
    }

    public static void setLoginUserName(String userName) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_LOGIN_USER_NAME, userName).apply();
    }

    public static String getLoginUserName() {
        return getSharedPreferences().getString(PREFERENCE_KEY_LOGIN_USER_NAME, "");
    }

    public static void setLoginPassword(String password) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_LOGIN_PASSWORD, password).apply();
    }

    public static String getLoginPassword() {
        return getSharedPreferences().getString(PREFERENCE_KEY_LOGIN_PASSWORD, "");
    }
}
