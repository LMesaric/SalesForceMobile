package hr.atoscvc.salesforcemobile

import android.content.res.Resources
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
) : Serializable {

    fun toFormattedString(resources: Resources): String {
        // Deliberately leaving out contact status as it is most likely not important when using this method.
        with(StringBuilder()) {
            append(ConcatenateObjectToString.concatenateContactName(this@Contact, resources, true))
            append("\r\n${resources.getString(R.string.formattingWorksAt)}: ${company?.name}")
            if (company?.webPage != null) append(" (${resources.getString(R.string.formattingWeb)}: ${company?.webPage})")
            if (phone != null) append("\r\n${resources.getString(R.string.formattingPhone)}: $phone")
            if (email != null) append("\r\n${resources.getString(R.string.formattingEmail)}: $email")
            if (preferredTime > 0) append("\r\n${resources.getString(R.string.formattingPrefTime)}: ${resources.getStringArray(R.array.contactPreferredTime_array)[preferredTime]}")
            if (details != null) append("\r\n${resources.getString(R.string.formattingDetails)}: $details")
            return this.toString()
        }
    }
}
