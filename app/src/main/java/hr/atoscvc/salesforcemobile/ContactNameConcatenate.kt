package hr.atoscvc.salesforcemobile

import android.content.res.Resources

object ContactNameConcatenate {
    fun fullName(contact: Contact, resources: Resources, formal: Boolean = true): String {
        val contactTitle: String = if (contact.title == 0) {
            ""
        } else {
            resources.getStringArray(R.array.contactTitle_array)[contact.title] + " "
        }
        return if (formal) {
            "$contactTitle${contact.firstName} ${contact.lastName}"
        } else {
            "$contactTitle${contact.lastName}"
        }
    }
}