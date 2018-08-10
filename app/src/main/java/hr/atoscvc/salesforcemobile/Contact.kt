package hr.atoscvc.salesforcemobile

import java.io.Serializable

data class Contact(
        var documentID: String,
        var status: Int,
        var title: Int,
        var firstName: String,
        var lastName: String,
        var company: Company,
        var phone: String,
        var email: String,
        var preferredTime: Int,
        var details: String
) : Serializable