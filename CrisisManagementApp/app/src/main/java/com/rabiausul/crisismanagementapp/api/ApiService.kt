package com.rabiausul.crisismanagementapp.api

import com.rabiausul.crisismanagementapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // USER
    @POST("api/users/login")
    suspend fun login(@Body user: User): Response<User>

    @POST("api/users/register")
    suspend fun register(@Body user: User): Response<User>

    // REQUESTS
    @GET("api/requests")
    suspend fun getAllRequests(): Response<List<AidRequest>>

    @POST("api/requests")
    suspend fun createRequest(@Body request: AidRequest): Response<AidRequest>

    @GET("api/requests/victim/{victimId}")
    suspend fun getRequestsByVictim(@Path("victimId") victimId: Int): Response<List<AidRequest>>

    @GET("api/requests/urgent/{level}")
    suspend fun getUrgentRequests(@Path("level") level: Int): Response<List<AidRequest>>

    @PUT("api/requests/{id}/status")
    suspend fun updateRequestStatus(
        @Path("id") id: Int,
        @Query("status") status: String
    ): Response<AidRequest>

    // RESOURCES
    @GET("api/resources")
    suspend fun getAllResources(): Response<List<Resource>>

    @POST("api/resources")
    suspend fun createResource(@Body resource: Resource): Response<Resource>

    @GET("api/resources/provider/{providerId}")
    suspend fun getResourcesByProvider(@Path("providerId") providerId: Int): Response<List<Resource>>

    @PUT("api/resources/{id}/quantity")
    suspend fun updateResourceQuantity(
        @Path("id") id: Int,
        @Query("quantity") quantity: Int
    ): Response<Resource>

    // ASSIGNMENTS
    @GET("api/assignments")
    suspend fun getAllAssignments(): Response<List<Assignment>>

    @POST("api/assignments")
    suspend fun createAssignment(@Body assignment: Assignment): Response<Assignment>

    @GET("api/assignments/request/{requestId}")
    suspend fun getAssignmentsByRequest(@Path("requestId") requestId: Int): Response<List<Assignment>>

    @PUT("api/assignments/{id}/status")
    suspend fun updateAssignmentStatus(
        @Path("id") id: Int,
        @Query("status") status: String
    ): Response<Assignment>

    // MATCHES
    @GET("api/matches")
    suspend fun getAllMatches(): Response<List<RequestResourceMatch>>

    @POST("api/matches")
    suspend fun createMatch(@Body match: RequestResourceMatch): Response<RequestResourceMatch>

    @GET("api/matches/request/{requestId}")
    suspend fun getMatchesByRequest(@Path("requestId") requestId: Int): Response<List<RequestResourceMatch>>

    // NEARBY
    @GET("api/nearby/requests")
    suspend fun getNearbyRequests(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<List<Map<String, Any>>>

    @GET("api/nearby/resources")
    suspend fun getNearbyResources(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<List<Map<String, Any>>>

    @GET("api/nearby/volunteers")
    suspend fun getNearbyVolunteers(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<List<Map<String, Any>>>

    @GET("api/nearby/critical-resources")
    suspend fun getCriticalResources(): Response<List<Map<String, Any>>>

    @GET("api/nearby/unfulfilled-requests")
    suspend fun getUnfulfilledRequests(): Response<List<Map<String, Any>>>
}