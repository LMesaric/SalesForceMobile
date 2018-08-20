package hr.atoscvc.salesforcemobile

import java.io.Serializable

data class Contact(
        var documentID: String? = null,     // 'documentID' set to 'null' means we are creating a new Contact
        var status: Int = 0,                // mandatory!
        var title: Int = 0,
        var firstName: String = "",         // mandatory!
        var lastName: String = "",          // mandatory!
        var company: Company? = null,       // mandatory!
        var phone: String? = null,
        var email: String? = null,
        var preferredTime: Int = 0,
        var details: String? = null
) : Serializable
