package com.health.gounihealth.utils;

/**
 * Created by LAL on 6/3/2016.
 */
public class ApiManager {

    //APIs
    public static final String BASE_URL = "http://52.200.186.237:8080/gounihealth/api";
    public static final String LOGIN_URL = BASE_URL+"/auth/login";
    public static final String SIGN_UP_URL = BASE_URL+"/user/signup";
    public static final String LOGOUT_URL = BASE_URL+"/auth/logout";
    public static final String COLLECTION_URL = " https://www.getpostman.com/collections/2274b8f0d2d787194f51";
    public static final String CITY_LIST_URL =BASE_URL+"/city/search";
    public static final String BLOOD_GROUP_LIST_URL =BASE_URL+"/bloodbank/bg/all";
    public static final String ICU_TYPE_LIST_URL =BASE_URL+"/icu/types";
    public static final String ICU_LIST_URL =BASE_URL+"/icu";
    public static final String HOSPITAL_LIST_URL =BASE_URL+"/hospital/nearby";
    public static final String USER_PANIC_URL =BASE_URL+"/user-panic";
    public static final String USER_PANIC_STATUS_URL =BASE_URL+"/user-panic";
    public static final String USER_PANIC_CALL_URL =BASE_URL+"/user-panic/call";
    public static final String USER_BLOOD_BANK =BASE_URL+"/bloodbank/list";
    public static final String MORE_INFO_HOSPITAL_ICU =BASE_URL+"/icu/list";
    public static final String PHARMACY_LIST =BASE_URL+"/pharmacy/list";
    public static final String CREATE_MEDICAL_RECORDS =BASE_URL+"/incident";
    public static final String CREATE_MEDICAL_RECORDS_DELETE =BASE_URL+"/incident";
    public static final String GET_MEDICAL_RECORDS =BASE_URL+"/incident/search";
    public static final String GET_OTP_VERIFY =BASE_URL+"/otp/verify";
    public static final String UPLOAD_MEDICAL_RECORD=BASE_URL+"/medical-records";
    public static final String DOWNLOAD_ID_MEDICAL_RECORD=BASE_URL+"/medical-records";
    public static final String DOWNLOAD_FILE_MEDICAL_RECORD=BASE_URL+"/medical-records/download";
    public static final String USER_GET_MY_PROFILE_URL=BASE_URL+"/user/my-profile";
    public static final String USER_UPDATE_PROFILE_URL=BASE_URL+"/user";
    public static final String USER_SEARCH_URL=BASE_URL+"/search";


}
