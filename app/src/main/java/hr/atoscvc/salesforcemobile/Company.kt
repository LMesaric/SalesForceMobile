package hr.atoscvc.salesforcemobile

import java.io.Serializable

data class Company(
        var documentID: String? = null,     // 'documentID' set to 'null' means we are creating a new Company
        var status: Int = 0,                    // mandatory
        var OIB: String = "",                    // mandatory
        var name: String = "",                   // mandatory
        var webPage: String? = null,
        var cvsSegment: Int = 0,
        var details: String? = null,
        var phone: String? = null,
        var communicationType: Int = 0,
        var employees: Int = 0,
        var income: String? = null
) : Serializable
