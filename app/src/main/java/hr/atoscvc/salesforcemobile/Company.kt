package hr.atoscvc.salesforcemobile

import java.io.Serializable

data class Company(
        var documentID: String,
        var status: Int,
        var OIB: String,
        var name: String,
        var webPage: String,
        var cvsSegment: Int,
        var details: String,
        var phone: String,
        var communicationType: Int,
        var employees: Int,
        var income: String
) : Serializable